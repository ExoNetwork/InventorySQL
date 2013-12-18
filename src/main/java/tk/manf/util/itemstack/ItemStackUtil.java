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

package tk.manf.util.itemstack;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static tk.manf.util.itemstack.ItemStackProperties.*;

/**
 * Offers various ItemStack related Utils for serialization
 * <p/>
 * @author Björn 'manf' Heinrichs
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemStackUtil {
    /**
     * Creates an ItemStack based on the given Informations
     * <p/>
     * @param mat    Material name
     * @param amount amount
     * @param damage damage
     * <p/>
     * @return ItemStack created
     */
    public static ItemStack create(String mat, int amount, int damage) {
        return new ItemStack(Material.getMaterial(mat), amount, (short) damage);
    }

    /**
     * Creates an ItemStack based on a map
     * <p/>
     * @param is ItemStack
     * <p/>
     * @return ItemStack created
     */
    public static ItemStack create(Map<String, String> is) {
        return create(get(is, MATERIAL), Integer.valueOf(get(is, AMOUNT)), Short.valueOf(get(is, DURABILITY)));
    }

    /**
     * Packs the given ItemStack into a map
     * <p/>
     * @return
     */
    public static Map<String, String> pack(ItemStack is) {
        Map<String, String> map = new HashMap<String, String>(3);
        put(map, MATERIAL, is.getType().name());
        put(map, AMOUNT, is.getAmount());
        put(map, DURABILITY, is.getDurability());
        return map;
    }

    private static String get(Map<String, String> map, ItemStackProperties prop) {
        return map.get(prop.getName());
    }

    private static void put(Map<String, String> map, ItemStackProperties prop, Object value) {
        map.put(prop.getName(), String.valueOf(value));
    }

}