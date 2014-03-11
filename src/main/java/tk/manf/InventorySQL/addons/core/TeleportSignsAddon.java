/**
 * Copyright (c) 2013 Exo-Network
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not claim
 * that you wrote the original software. If you use this software in a product,
 * an acknowledgment in the product documentation would be appreciated but is
 * not required.
 *
 * 2. Altered source versions must be plainly marked as such, and must not be
 * misrepresented as being the original software.
 *
 * 3. This notice may not be removed or altered from any source distribution.
 *
 * manf info@manf.tk
 */
package tk.manf.InventorySQL.addons.core;

import de.zh32.teleportsigns.ProxyTeleportEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tk.manf.InventorySQL.addons.AbstractAddon;
import tk.manf.InventorySQL.api.InventorySQLAPI;

/**
 * Intercepts between TeleportSings teleportation to sync Inventory
 */
public class TeleportSignsAddon extends AbstractAddon implements Listener {
    @Override
    public void onEnable(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPreSave(final ProxyTeleportEvent ev) {
        InventorySQLAPI.getAPI().switchPlayer(ev.getPlayer(), ev.getServerInfo().getName());
        ev.setCancelled(true);
    }
}
