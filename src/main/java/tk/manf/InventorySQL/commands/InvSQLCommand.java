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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;
import tk.manf.InventorySQL.manager.ConfigManager;
import tk.manf.InventorySQL.manager.DatabaseManager;
import tk.manf.InventorySQL.manager.LanguageManager;
import tk.manf.InventorySQL.manager.LoggingManager;
import tk.manf.InventorySQL.util.Language;

public class InvSQLCommand extends AbstractCommandHandler {
    private static final String IDENTIFIER = "invsql";
    private static final String SAVE = IDENTIFIER + " " + "save";
    private static final String RELOAD = IDENTIFIER + " " + "reload";

    @Command(identifier = SAVE,
            description = "Saves yourself",
            permissions = {"InventorySQL.save.self"})
    public void saveCurrentPlayer(Player sender) {
        savePlayer(sender, sender);
    }

    @Command(identifier = SAVE,
            description = "Saves specific player",
            onlyPlayers = false,
            permissions = {"InventorySQL.save.other"})
    public void savePlayer(CommandSender sender, @Arg(name = "target") Player target) {
        if (target != null) {
            DatabaseManager.getInstance().savePlayer(target);
            sender.sendMessage("Player " + target.getName() + " has been saved!");
        }
    }

    @Command(identifier = InvSQLCommand.RELOAD,
            description = "Reloads the Language or the Config",
            onlyPlayers = false,
            permissions = {"InventorySQL.reload"})
    public void reload(CommandSender sender, @Arg(name = "Language|Config") String target) {
        try {
            if (target.equalsIgnoreCase("LANGUAGE")) {
                ConfigManager.getInstance().reloadConfig(getPlugin(), getClassLoader());
            } else if (target.equalsIgnoreCase("CONFIG")) {
                ConfigManager.getInstance().loadLanguage(getPlugin());
            } else {
                sender.sendMessage("You can only reload Language or Config");
            }
        } catch (Exception ex) {
            LoggingManager.getInstance().log(ex);
            LanguageManager.getInstance().sendMessage(sender, Language.COMMAND_ERROR);
        }

    }

}
