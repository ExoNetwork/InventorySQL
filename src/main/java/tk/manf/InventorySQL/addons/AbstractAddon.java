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

package tk.manf.InventorySQL.addons;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.plugin.java.JavaPlugin;
import tk.manf.InventorySQL.manager.LoggingManager;

@RequiredArgsConstructor
@ToString
public abstract class AbstractAddon implements Addon {
    public String getName() {
        return getClass().getSimpleName();
    }

    public String getVersion() {
        return "1.0";
    }

    // We do not require Addons to override both
    public void onEnable(JavaPlugin plugin) {}

    public void onDisable(JavaPlugin plugin) {}
    
    @Override
    public final int hashCode() {
        return toString().hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj.hashCode() == hashCode() && obj.toString().equals(toString()) && obj instanceof Addon;
    }

    protected final void log(String message) {
        LoggingManager.getInstance().log(LoggingManager.Level.ADDONS, "[" + getName() + "] " + message);
    }
}
