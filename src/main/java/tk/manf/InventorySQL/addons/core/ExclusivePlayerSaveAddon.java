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

package tk.manf.InventorySQL.addons.core;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tk.manf.InventorySQL.addons.AbstractAddon;
import tk.manf.InventorySQL.event.PrePlayerEvent;
import tk.manf.InventorySQL.event.PrePlayerSavedEvent;

/**
 * Saves only authorised Players
 */
public class ExclusivePlayerSaveAddon extends AbstractAddon implements Listener {
    private static final String AUTHORISATION = "InventorySQL.addons.authorised";

    public ExclusivePlayerSaveAddon() {
    }

    @Override
    public void onEnable(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPreSave(final PrePlayerSavedEvent ev) {
        handle(ev);
    }

    @EventHandler
    public void onPreLoad(final PrePlayerSavedEvent ev) {
        handle(ev);
    }

    private void handle(final PrePlayerEvent ev) {
        if (!ev.getPlayer().hasPermission(AUTHORISATION)) {
            log("Player is not Authorised: " + ev.getPlayer().getName());
            ev.setCancelled(true);
        }
    }
}
