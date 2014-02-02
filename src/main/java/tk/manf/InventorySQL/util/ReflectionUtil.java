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

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tk.manf.InventorySQL.manager.LoggingManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtil {
    public static <T> T getInstance(Class<T> type, ClassLoader loader, String target) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Class<?> t = loader.loadClass(target);
        Preconditions.checkArgument(t != null, "Target not found %s", target);
        LoggingManager.getInstance().d("Loading " + t.getName());
        if(t.getAnnotation(Deprecated.class) != null) {
            LoggingManager.getInstance().logDeveloperMessage("manf", LoggingManager.DeveloperMessages.DEPRECATED_CLASS);
        }
        return type.cast(t.newInstance());
    }

}
