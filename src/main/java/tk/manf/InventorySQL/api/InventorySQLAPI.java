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

package tk.manf.InventorySQL.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tk.manf.InventorySQL.manager.DatabaseManager;
import tk.manf.InventorySQL.manager.InventoryLockingSystem;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@NoArgsConstructor
public class InventorySQLAPI {
    private static final String CHANNEL = "BungeeCord";
    private JavaPlugin plugin;
    private boolean sending;

    /**
     * Initialises the API
     * Called internally. Should ONLY be called by
     * InventorySQL
     * @param plugin InventorySQL Plugin 
     */
    public void init(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Destroy references to Plugin
     * Called internally. Should ONLY be called by
     * InventorySQL 
     * 
     * @param plugin InventorySQL Plugin
     */
    public void disable(JavaPlugin plugin) {
        this.plugin = null;
    }
    
    /**
     * Switches given Player to given Server
     * Saving Inventory of Player and then move him to the given Server
     * @param target Target Player
     * @param server Servername
     */
    public void switchPlayer(Player target, String server) {
        InventoryLockingSystem.getInstance().addLock(target.getName());
        DatabaseManager.getInstance().savePlayer(target);
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
        }
        sendPluginMessage(target, b.toByteArray());
    }

    private void sendPluginMessage(Player player, byte[] message) {
        if (!sending) {
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
            sending = true;
        }
        player.sendPluginMessage(plugin, CHANNEL, message);
    }
    
    @Getter
    private static final InventorySQLAPI API = new InventorySQLAPI();
}
