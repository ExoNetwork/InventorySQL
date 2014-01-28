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

package tk.manf.InventorySQL.datahandling.serializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import tk.manf.InventorySQL.datahandling.Serializer;
import tk.manf.InventorySQL.datahandling.exceptions.SerializationException;

public class SimpleSerializer implements Serializer {
    private final ItemStack AIR = new ItemStack(Material.AIR);
    
    public ItemStack[] deserializeItemStacks(byte[] b) throws SerializationException {
        try {
            return deserial(JSONValue.parseWithException(new String(b)));
        } catch (ParseException ex) {
            throw new SerializationException("[Parsing Error]", ex);
        }
    }

    public byte[] serializeItemStacks(ItemStack[] inv) throws SerializationException {
        List<Map<String, Object>> inventory = new ArrayList<Map<String, Object>>(inv.length);
        for (ItemStack is : inv) {
            inventory.add((is == null ? AIR: is).serialize());
        }
        return JSONValue.toJSONString(inventory).getBytes();
    }

    private ItemStack[] deserial(Object o) throws SerializationException {
        try {
            if (o instanceof List) {
                final List<?> data = (List) o;
                List<ItemStack> items = new ArrayList<ItemStack>(data.size());
                for (Object t : data) {
                    if (t instanceof Map) {
                        final Map<?, ?> mdata = (Map) t;
                        final Map<String, Object> conv = new HashMap<String, Object>(mdata.size());
                        for (Map.Entry<?, ?> e : mdata.entrySet()) {
                            conv.put(String.valueOf(e.getKey()), e.getValue());
                        }
                        items.add(ItemStack.deserialize(conv));
                    } else {
                        throw new IllegalArgumentException("Not a Map");
                    }
                }
                return items.toArray(new ItemStack[items.size()]);
            }
            throw new IllegalArgumentException("Not a List");
        } catch (IllegalArgumentException ex) {
            throw new SerializationException(o, ex);
        }
    }
}