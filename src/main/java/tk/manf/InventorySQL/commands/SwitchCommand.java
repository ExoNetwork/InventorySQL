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
package tk.manf.InventorySQL.commands;

import tk.manf.InventorySQL.AbstractCommandHandler;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;
import tk.manf.InventorySQL.manager.DatabaseManager;
import tk.manf.InventorySQL.manager.InventoryLockingSystem;

public class SwitchCommand extends AbstractCommandHandler {
    private static final String IDENTIFIER = "switch";

    @Command(identifier = IDENTIFIER,
            description = "Switches the Server of a given Player")
    public void changeServerTarget(Player sender, @Arg(name = "server") String server, @Arg(name = "target", def = "?sender") Player target) {
        String perm = sender.getName().equals(target.getName()) ? "InventorySQL.switch.self" : "InventorySQL.switch.other";
        if (sender.hasPermission(perm)) {
            InventoryLockingSystem.getInstance().addLock(target.getName());
            DatabaseManager.getInstance().savePlayer(target);
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF("Connect");
                out.writeUTF(server);
            } catch (IOException e) {
            }

            target.sendPluginMessage(getPlugin(), "BungeeCord", b.toByteArray());
            InventoryLockingSystem.getInstance().removeLock(target.getName());
        } else {
            // Remove asap
            sender.sendMessage(ChatColor.RED + "You do not have permissions to do this!");
        }
    }

}
