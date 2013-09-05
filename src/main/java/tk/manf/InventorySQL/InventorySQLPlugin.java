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

package tk.manf.InventorySQL;

import java.io.IOException;
import net.h31ix.updater.Updater;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import tk.manf.InventorySQL.manager.ConfigManager;
import tk.manf.InventorySQL.manager.DataHandlingManager;
import tk.manf.InventorySQL.manager.DatabaseManager;
import tk.manf.InventorySQL.manager.LanguageManager;
import tk.manf.InventorySQL.manager.LoggingManager;
import tk.manf.InventorySQL.manager.LoggingManager.DeveloperMessages;
import tk.manf.InventorySQL.manager.UpdateEventManager;
import tk.manf.InventorySQL.util.Language;

import static net.h31ix.updater.Updater.UpdateResult.NO_UPDATE;

public class InventorySQLPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
            getDataFolder().mkdirs();
            FileConfiguration debug = ConfigManager.getConfig(this, "debug.yml");
            LoggingManager.getInstance().setLevel(debug.getInt("debug-level", 1000));
            LoggingManager.getInstance().setPrefix(getDescription().getPrefix());
            ConfigManager.getInstance().initialise(this);
            DatabaseManager.getInstance().initialise(this, getClassLoader());
            UpdateEventManager.getInstance().initialise(this);
            DataHandlingManager.getInstance().initialise(getClassLoader());
        } catch (Exception ex) {
            LoggingManager.getInstance().log(ex);
            getPluginLoader().disablePlugin(this);
        }

        //May add just a check and let the user update manually?
        if (ConfigManager.getInstance().isAutoUpdateEnabled()) {
            Updater updater = new Updater(this, "inventorysql", this.getFile(), Updater.UpdateType.DEFAULT, false);

            switch (updater.getResult()){
                default:
                    LoggingManager.getInstance().log(new Exception(updater.getResult().name() + " nag Developer!"));
                    break;
                case SUCCESS:
                    LoggingManager.getInstance().log(999, "Updated to Version: " + updater.getLatestVersionString());
                case NO_UPDATE:
                    LoggingManager.getInstance().log(999, "You are up to date!");
                    break;
                case UPDATE_AVAILABLE:
                    //will nether happen or call the Ghastbusters
                    break;
            }
        }

        if (ConfigManager.getInstance().isMetricsEnabled()) {
            try {
                Metrics metrics = new Metrics(this);
                //Add Graph here
                if (metrics.start()) {
                    LoggingManager.getInstance().logDeveloperMessage("manf", DeveloperMessages.METRICS_LOADED);
                } else {
                    LoggingManager.getInstance().logDeveloperMessage("manf", DeveloperMessages.METRICS_OFF);
                }
            } catch (IOException e) {
            }
        }


    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            handleCommand(sender, label, args);
        } catch (Exception ex) {
            LoggingManager.getInstance().log(ex);
            LanguageManager.getInstance().sendMessage(sender, Language.COMMAND_ERROR);
        }
        return true;
    }

    public void handleCommand(CommandSender sender, String label, String[] args) throws Exception {
        //QUICK'N'Dirty gonna use MethodCommand (or implement my own System) to handle them in future updates
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("InventorySQL.reload")) {
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("LANGUAGE")) {
                            ConfigManager.getInstance().reloadConfig(this, getClassLoader());
                            return;
                        } else if (args[1].equalsIgnoreCase("CONFIG")) {
                            ConfigManager.getInstance().loadLanguage(this);
                            return;
                        } else {
                            sender.sendMessage("You can only reload Language or Config");
                        }
                    } else {
                        sender.sendMessage("/" + label + " reload " + "[LANGUAGE||CONFIG]");
                        return;
                    }
                } else {
                    sender.sendMessage("Sorry, but you have no Permission to do this");
                    return;
                }
            }
            if (args[0].equalsIgnoreCase("save")) {
                Player p;
                if (args.length > 1) {
                    if (sender.hasPermission("InventorySQL.save.other")) {
                        p = Bukkit.getPlayer(args[1]);
                        if (p == null) {
                            sender.sendMessage("Sorry, but this Player is not online!");
                            return;
                        }
                    } else {
                        sender.sendMessage("Sorry, but you have no Permission to do this");
                        return;
                    }
                } else {
                    if (sender.hasPermission("InventorySQL.save.self")) {
                        if (sender instanceof Player) {
                            p = (Player) sender;
                        } else {
                            sender.sendMessage("Sorry, but you cannot be saved!");
                            return;
                        }
                    } else {
                        sender.sendMessage("Sorry, but you have no Permission to do this");
                        return;
                    }
                }
                DatabaseManager.getInstance().savePlayer(p);
                sender.sendMessage("Player " + p.getName() + " has been saved!");
                return;
            }
        }
        sender.sendMessage("/" + label + " reload " + "[LANGUAGE||CONFIG]");
        sender.sendMessage("/" + label + " save");
    }

}