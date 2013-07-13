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

import tk.manf.InventorySQL.util.jackson.TypeReferences;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import tk.manf.InventorySQL.datahandling.Serializer;
import tk.manf.InventorySQL.datahandling.exceptions.SerializationException;
import tk.manf.InventorySQL.manager.LoggingManager;

public class JSONSerializer implements Serializer {
    private ObjectMapper mapper;

    public JSONSerializer() {
        mapper = new ObjectMapper();
    }

    public byte[] serializeItemStacks(ItemStack[] inv) throws SerializationException {
        try {
            final Map<Integer, String> tmp = new HashMap<Integer, String>(inv.length);
            for (int slot = 0; slot < inv.length; slot++) {
                LoggingManager.getInstance().d("Saving: " + slot + " # " + inv[slot]);
                tmp.put(slot, inv[slot] == null ? "null" : serialize(serializeItemStack(inv[slot])));
            }
            return serialize(tmp).getBytes("UTF-8");
        } catch (IOException ex) {
            throw new SerializationException(inv, ex);
        }
    }

    public ItemStack[] deserializeItemStacks(byte[] b) throws SerializationException {
        try {
            final Map<Integer, String> tmp = deserialize(b, TypeReferences.INTEGER_STRING_MAP);
            if (tmp == null) {
                return null;
            }
            ItemStack[] is = new ItemStack[tmp.size()];
            for (int slot : tmp.keySet()) {
                is[slot] = tmp.get(slot).equals("null") ? null : unpack(deserialize(tmp.get(slot).getBytes("UTF-8"), TypeReferences.STRING_OBJECT_MAP));
                LoggingManager.getInstance().d("SLOT " + slot + " is now " + is[slot]);
            }
            return is;
        } catch (IOException ex) {
            throw new SerializationException(new String(b), ex);
        }
    }

    private Map<String, Object> serializeItemStack(ItemStack is) {
        final Map<String, Object> parent = is.serialize();
        if (is.getItemMeta() != null) {
            final Map<String, Object> meta = new LinkedHashMap<String, Object>(is.getItemMeta().serialize());
            meta.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(is.getItemMeta().getClass()));
            parent.put("meta", meta);
        }
        return parent;
    }

    private static ItemStack unpack(Map<String, Object> map) {
        deserializeConfiguratioSerializable(map, "meta");
        return ItemStack.deserialize(map);
    }

    private String serialize(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    private <T> T deserialize(byte[] data, TypeReference<T> type) throws IOException {
        return mapper.readValue(data, type);
    }

    @SuppressWarnings("unchecked")
    private static void deserializeConfiguratioSerializable(Map<String, Object> map, String node) {
        if (map.get(node) != null && map.get(node) instanceof Map) {
            map.put(node, ConfigurationSerialization.deserializeObject((Map<String, Object>) map.get(node)));
        }
    }

}