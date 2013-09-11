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
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
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
                tmp.put(slot, inv[slot] == null ? "null" : serialize(serializeFully(inv[slot])));
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
                is[slot] = tmp.get(slot).equals("null") ? null : (ItemStack) deserializeFully(deserialize(tmp.get(slot).getBytes("UTF-8"), TypeReferences.STRING_OBJECT_MAP));
                LoggingManager.getInstance().d("SLOT " + slot + " is now " + is[slot]);
            }
            return is;
        } catch (IOException ex) {
            throw new SerializationException(new String(b), ex);
        }
    }

    private static Map<String, Object> serializeFully(ConfigurationSerializable cs) {
        Map<String, Object> base = cs.serialize();
        for (Map.Entry<String, Object> entry : base.entrySet()) {
            Object obj = entry.getValue();
            if (obj instanceof ConfigurationSerializable) {
                base.put(entry.getKey(), serializeFully((ConfigurationSerializable) obj));
            }
        }
        base.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(cs.getClass()));
        return base;
    }

    private static Object deserializeFully(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object obj = entry.getValue();
            if (obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> intern = (Map<String, Object>) obj;
                if (intern.containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
                    // Safe to assume it really is a ConfigSerial
                    map.put(entry.getKey(), deserializeFully(intern));
                }
            }
        }
        return ConfigurationSerialization.deserializeObject((Map<String, Object>) map);
    }

    private String serialize(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    private <T> T deserialize(byte[] data, TypeReference<T> type) throws IOException {
        return mapper.readValue(data, type);
    }
}