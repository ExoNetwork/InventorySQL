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

package tk.manf.InventorySQL.util;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.ChatColor;
import tk.manf.InventorySQL.manager.ConfigManager;

public final class Language {
    @Getter
    private final String id;
    @Getter
    private final String pattern;

    private Language(String id, String defPattern) {
        this.id = id;
        this.pattern = ChatColor.translateAlternateColorCodes('&', ConfigManager.getInstance().getLangugagePattern(id, defPattern));
        register();
    }

    private void register() {
        if (instances == null) {
            instances = new ArrayList<Language>(PHRASES);
        }
        instances.add(this);
    }

    public static final int PHRASES = 5;
    public static final Language COMMAND_ERROR = new Language("command.error", "Error while performing this Command!");
    public static final Language SAVING_INVENTORY = new Language("inventory.saving", "Your Inventory is saving...");
    public static final Language SAVED_INVENTORY = new Language("inventory.saved", "Your Inventory has been saved!");
    public static final Language SYNCED_INVENTORY = new Language("inventory.synced", "Your Inventory has successfully been synchronised!");
    public static final Language FIRST_JOIN = new Language("first-join", "Welcome on this Server!");
    private static List<Language> instances;
}