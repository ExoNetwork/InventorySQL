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

package tk.manf.InventorySQL.database;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface DatabaseHandler {
    /**
     * Called whenever Handler is initialised by Databasehandler
     * Used to createDatabases etc
     * @param JavaPlugin instance
     */
    public void init(JavaPlugin plugin) throws Exception;
    
    /**
     * Saves the Inventory for the given Player
     * @param player Player
     * @throws Exception if something went wrong
     */
    public void savePlayerInventory(Player player) throws Exception;
    
    /**
     * Loads the Inventory for the given Player
     * @param player Player
     * @return sucess
     * @throws Exception if something went wrong
     */
    public boolean loadPlayerInventory(Player player) throws Exception;
}
