/**
 * Copyright (c) 2013 Exo-Network
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *    1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 *
 *    2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 *
 *    3. This notice may not be removed or altered from any source
 *    distribution.
 *
 * manf                   info@manf.tk
 */

package tk.manf.InventorySQL.manager;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import tk.manf.InventorySQL.database.DatabaseHandler;
import tk.manf.InventorySQL.database.handler.MySQLDatabaseHandler;
import tk.manf.InventorySQL.event.PlayerLoadedEvent;
import tk.manf.InventorySQL.event.PlayerSavedEvent;
import tk.manf.InventorySQL.event.PrePlayerLoadedEvent;
import tk.manf.InventorySQL.event.PrePlayerSavedEvent;
import tk.manf.InventorySQL.util.Language;
import tk.manf.InventorySQL.util.ReflectionUtil;

import java.util.HashMap;

public final class DatabaseManager implements Listener {
    private DatabaseHandler handler;
    private final HashMap<String, ItemStack[]> inventory;
    private final HashMap<String, ItemStack[]> armor;

    private DatabaseManager() {
        inventory = new HashMap<String, ItemStack[]>(0);
        armor = new HashMap<String, ItemStack[]>(0);
    }

    public void initialise(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void reload(JavaPlugin plugin, ClassLoader cl) throws Exception {
        LoggingManager.getInstance().d("Reloading DatabaseManager");
        handler = ReflectionUtil.getInstance(DatabaseHandler.class, cl, ConfigManager.getInstance().getDatabaseHandler());
        handler.init(plugin);
        LoggingManager.getInstance().d("Handler choosen: " + handler);
    }

    public void savePlayer(Player player) {
        //Allow Plugins to cancel saving
        if (EventManager.getInstance().call(new PrePlayerSavedEvent(player)).isCancelled()) {
            LoggingManager.getInstance().log(LoggingManager.Level.DEBUG, "Canceled Saving for: " + player.getUniqueId());
            return;
        }

        try {
            LanguageManager.getInstance().sendMessage(player, Language.SAVING_INVENTORY);
            handler.savePlayerInventory(player);
            LanguageManager.getInstance().sendMessage(player, Language.SAVED_INVENTORY);
        } catch (Exception ex) {
            LoggingManager.getInstance().log(ex);
        }

        //Inform Listeners! Player has been saved
        EventManager.getInstance().call(new PlayerSavedEvent(player));
    }

    public boolean loadPlayer(Player player) throws Exception {
        if (EventManager.getInstance().call(new PrePlayerLoadedEvent(player)).isCancelled()) {
            return false;
        }
        return EventManager.getInstance().call(new PlayerLoadedEvent(handler.loadPlayerInventory(player), player)).isSuccess();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(final PlayerJoinEvent ev) {
        Player p = ev.getPlayer();
        addPlayerInventoryCache(String.valueOf(p.getUniqueId()), p.getInventory());
        p.getInventory().clear();
        try {
            if (loadPlayer(p)) {
                //Save
                LanguageManager.getInstance().sendMessage(p, Language.SYNCED_INVENTORY);
                removePlayerInventoryCache(false, String.valueOf(p.getUniqueId()), p.getInventory());
            } else {
                //First Join
                LanguageManager.getInstance().sendMessage(p, Language.FIRST_JOIN);
                //Create data
                removePlayerInventoryCache(true, String.valueOf(p.getUniqueId()), p.getInventory());
                savePlayer(p);
            }
        } catch (Exception ex) {
            //Restore player Equipment, because we may accidently have removed it
            removePlayerInventoryCache(true, String.valueOf(p.getUniqueId()), p.getInventory());
            LoggingManager.getInstance().log(LoggingManager.Level.ERROR, "Your Problem was caused by: " + handler);
            LoggingManager.getInstance().log(ex);
        }
    }

    private void addPlayerInventoryCache(String player, PlayerInventory inv) {
        inventory.put(player, inv.getContents());
        armor.put(player, inv.getArmorContents());
    }

    private void removePlayerInventoryCache(boolean restore, String name, PlayerInventory inv) {
        if (restore) {
            if (inventory.containsKey(name)) {
                inv.setContents(inventory.get(name));
            }
            if (armor.containsKey(name)) {
                inv.setArmorContents(armor.get(name));
            }
        }

        inventory.remove(name);
        armor.remove(name);
    }

    @Getter
    private static final DatabaseManager instance = new DatabaseManager();
}
