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

import com.google.common.base.Preconditions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.manf.InventorySQL.CommandManager;
import tk.manf.InventorySQL.api.InventorySQLAPI;

public final class SwitchCommand extends CommandManager.InternalCommand {
    public SwitchCommand() {
        super("switch");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Preconditions.checkArgument(args.length > 1, "Needs atleast a Server target");
        changeServerTarget(sender, args[0], getOptionalPlayer(sender, args, 1));
    }

    private void changeServerTarget(CommandSender sender, String server, Player target) {
        checkPermission(sender, sender.getName().equals(target.getName()) ? "InventorySQL.switch.self" : "InventorySQL.switch.other");
        InventorySQLAPI.getAPI().switchPlayer(target, server);
    }
}