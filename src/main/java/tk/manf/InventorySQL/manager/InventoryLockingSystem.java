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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryLockingSystem implements Listener {
    private final List<String> locked;

    private InventoryLockingSystem() {
        locked = Collections.synchronizedList(new ArrayList<String>(0));
    }

    public void initialise(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addLock(String player) {
        locked.add(player);
    }
    
    @EventHandler
    public void onPlayerPickup(final PlayerPickupItemEvent ev) {
        check(ev);
    }

    @EventHandler
    public void onPlayerDrop(final PlayerDropItemEvent ev) {
        check(ev);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent ev) {
        check(ev, ev.getWhoClicked());
    }
    
    
    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent ev) {
        check(ev, ev.getPlayer());
    }
    
    public boolean isLocked(String player) {
        synchronized (locked) {
            Iterator<String> i = locked.iterator();
            while (i.hasNext()) {
                if (i.next().equals(player)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    public void removeLock(String player) {
        locked.remove(player);
    }
    
    private <T extends PlayerEvent & Cancellable> void  check(T ev) {
       check(ev, ev.getPlayer());
    }
    
    private void check(Cancellable ev, HumanEntity player) {
        ev.setCancelled(isLocked(player.getName()));
    }

    @Getter
    private static final InventoryLockingSystem instance = new InventoryLockingSystem();
}
