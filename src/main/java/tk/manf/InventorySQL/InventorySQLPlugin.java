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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import tk.manf.InventorySQL.commands.CommandManager;
import tk.manf.InventorySQL.manager.ConfigManager;
import tk.manf.InventorySQL.manager.DataHandlingManager;
import tk.manf.InventorySQL.manager.DatabaseManager;
import tk.manf.InventorySQL.manager.InventoryLockingSystem;
import tk.manf.InventorySQL.manager.LoggingManager;
import tk.manf.InventorySQL.manager.LoggingManager.DeveloperMessages;
import tk.manf.InventorySQL.manager.UpdateEventManager;

import static net.h31ix.updater.Updater.UpdateResult.NO_UPDATE;

public class InventorySQLPlugin extends JavaPlugin {    
    private CommandManager manager;
    
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
            InventoryLockingSystem.getInstance().initialise(this);
            manager = new CommandManager();
            manager.initialise(this);
        } catch (Exception ex) {
            LoggingManager.getInstance().log(ex);
            getPluginLoader().disablePlugin(this);
        }

        //May add just a check and let the user update manually?
        if (ConfigManager.getInstance().isAutoUpdateEnabled()) {
            Updater updater = new Updater(this, "inventorysql", this.getFile(), Updater.UpdateType.DEFAULT, false);

            switch (updater.getResult()){
                case SUCCESS:
                    LoggingManager.getInstance().log(999, "Updated to Version: " + updater.getLatestVersionString());
                case NO_UPDATE:
                    LoggingManager.getInstance().log(999, "You are up to date!");
                    break;
                default:
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
        manager.disable();
    }

    public ClassLoader getReflectionLoader() {
        return getClassLoader();
    }

}