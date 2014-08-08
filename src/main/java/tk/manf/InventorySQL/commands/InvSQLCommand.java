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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tk.manf.InventorySQL.CommandManager;
import tk.manf.InventorySQL.manager.ConfigManager;
import tk.manf.InventorySQL.manager.DatabaseManager;
import tk.manf.InventorySQL.manager.LanguageManager;
import tk.manf.InventorySQL.manager.LoggingManager;
import tk.manf.InventorySQL.util.Language;

public final class InvSQLCommand extends CommandManager.InternalCommand {
    public InvSQLCommand() {
        super("invsql");
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            //TODO:
            return;
        }
        if (args[0].equalsIgnoreCase("save")) {
            savePlayer(sender, getOptionalPlayer(sender, args, 1));
            return;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            reload(sender, args.length == 1 ? "" : args[1]);
        }
    }

    private void savePlayer(CommandSender sender, Player target) {
        checkNotNull(target, "Player not found");
        checkPermission(sender, sender.getName().equals(target.getName()) ? "InventorySQL.save.self" : "InventorySQL.save.other");
        DatabaseManager.getInstance().savePlayer(target);
        sender.sendMessage("Player " + target.getName() + "(" + target.getUniqueId() + ")" + " has been saved!");
    }

    private void reload(CommandSender sender, String target) {
        checkPermission(sender, "InventorySQL.reload");
        try {
            if (target.equalsIgnoreCase("LANGUAGE")) {
                ConfigManager.getInstance().reloadConfig(getPlugin(), getClassLoader());
            } else if (target.equalsIgnoreCase("CONFIG")) {
                ConfigManager.getInstance().loadLanguage(getPlugin());
            } else {
                sender.sendMessage("You can only reload Language or Config");
            }
            //TODO: remove much exceptions less wow
        } catch (Exception ex) {
            LoggingManager.getInstance().log(ex);
            LanguageManager.getInstance().sendMessage(sender, Language.COMMAND_ERROR);
        }

    }
}
