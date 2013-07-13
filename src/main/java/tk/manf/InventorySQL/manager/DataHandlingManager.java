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

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import tk.manf.InventorySQL.datahandling.Compressor;
import tk.manf.InventorySQL.datahandling.Serializer;
import tk.manf.InventorySQL.datahandling.exceptions.DataHandlingException;
import tk.manf.InventorySQL.util.ReflectionUtil;

public class DataHandlingManager {
    private Compressor compressor;
    private Serializer serializer;

    private DataHandlingManager() {
    }

    public void initialise(ClassLoader cl) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        compressor = ReflectionUtil.getInstance(Compressor.class, cl, ConfigManager.getInstance().getCompressor());
        serializer = ReflectionUtil.getInstance(Serializer.class, cl, ConfigManager.getInstance().getSerializer());
    }
    
    public void reload(ClassLoader cl) {
        
    }
    
    public ItemStack[] deserial(byte[] b) throws DataHandlingException {
        return serializer.deserializeItemStacks(compressor.uncompress(b));
    }

    public byte[] serial(ItemStack[] stacks) throws DataHandlingException {
        return compressor.compress(serializer.serializeItemStacks(stacks));
    }

    @Getter
    private static final DataHandlingManager instance = new DataHandlingManager();
}
