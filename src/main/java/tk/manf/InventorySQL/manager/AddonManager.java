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

package tk.manf.InventorySQL.manager;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tk.manf.InventorySQL.addons.Addon;
import tk.manf.InventorySQL.util.ReflectionUtil;

import java.io.IOException;

public class AddonManager {    
    private ImmutableSet<Addon> addons;
    
    private AddonManager() {
    }
    
    public void initialise(JavaPlugin plugin, ClassLoader loader) throws IOException {
        FileConfiguration config = ConfigManager.getConfig(plugin, "addons.yml");
        ImmutableSet.Builder<Addon> builder = new ImmutableSet.Builder<Addon>();
        for (String addon : config.getStringList("addons")) {
            try {
                Addon a = ReflectionUtil.getInstance(Addon.class, loader, addon);
                LoggingManager.getInstance().log(LoggingManager.Level.ADDONS, "Enabling " + a.getName() + " Version " + a.getVersion());
                a.onEnable(plugin);
                builder.add(a);
            } catch (ReflectiveOperationException ex) {
                LoggingManager.getInstance().log(LoggingManager.Level.ERROR, "Error while initialising " + addon);
                LoggingManager.getInstance().log(ex);
            }
        }
        addons = builder.build();
    }
    
    public void disable(JavaPlugin plugin) {
        for(Addon addon:addons) {
            LoggingManager.getInstance().log(LoggingManager.Level.ADDONS, "Disabling " + addon.getName() + " Version " + addon.getVersion());
            addon.onDisable(plugin);
        }
        addons = null;
    }
    
    @Getter
    private static final AddonManager instance = new AddonManager();
}
