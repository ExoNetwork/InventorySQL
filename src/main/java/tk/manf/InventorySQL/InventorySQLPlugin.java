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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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

public class InventorySQLPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        try {
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
            }
        }
        sender.sendMessage("/" + label + " reload " + "[LANGUAGE||CONFIG]");
    }

}