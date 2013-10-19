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

import com.google.common.collect.ImmutableList;
import java.lang.reflect.Method;
import java.util.List;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class DependenciesManager implements Listener {
    private JavaPlugin plugin;
    private ClassLoader loader;
    private String name;
    private List<String> dependencies;

    private DependenciesManager() {
    }

    public void initialise(JavaPlugin plugin, ClassLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
        this.name = plugin.getDescription().getName();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        dependencies = getDependencies(plugin.getDescription());
        checkDatabaseHandler();
    }

    @EventHandler
    public void onPluginEnable(final PluginEnableEvent ev) {
        switch (scanPlugin(ev.getPlugin())){
            case PARENT:
                hookPlugin(ev.getPlugin());
                return;
            case CHILD:
                checkDatabaseHandler();
                return;
            case NONE:
                // This plugins does not like us nor do we :(
                return;
            default:
                throw new IllegalArgumentException("Unknown DependencyType!");
        }
    }

    private void checkDatabaseHandler() {
        LoggingManager.getInstance().d("Checking for Database Manager");
        if (findDatabaseHandler()) {
            try {
                LoggingManager.getInstance().d("Database Manager found!");
                DatabaseManager.getInstance().reload(plugin, loader);
                loader = null;
                plugin = null;
            } catch (Exception ex) {
                LoggingManager.getInstance().log(ex);
            }
        }
    }

    private boolean findDatabaseHandler() {
        if (loader == null) {
            LoggingManager.getInstance().d("Loader is null: Cannot find DatabaseManager");
            return false;
        }
        try {
            Method method = loader.getClass().getDeclaredMethod("findClass", new Class<?>[]{String.class});
            method.setAccessible(true);
            return method.invoke(loader, ConfigManager.getInstance().getDatabaseHandler()) != null;
        } catch (ReflectiveOperationException ex) {
            // Does not have our handler yet
            LoggingManager.getInstance().d("This may be not important!");
            LoggingManager.getInstance().d(ex);
        } catch (RuntimeException ex) {
            LoggingManager.getInstance().log(ex);
        }
        return false;
    }

    private void hookPlugin(Plugin p) {
        LoggingManager.getInstance().log(LoggingManager.Level.DEVELOPER, "Hooked into " + p.getName() + " (" + p.getDescription().getVersion() + ")");
        // Uhm we do not have any dependency yet, so we don't need any further steps
    }

    private DependencyType scanPlugin(Plugin p) {
        if (dependencies.contains(p.getName())) {
            return DependencyType.PARENT;
        }
        if (getDependencies(p.getDescription()).contains(name)) {
            return DependencyType.CHILD;
        }
        return DependencyType.NONE;
    }

    private List<String> getDependencies(PluginDescriptionFile pdf) {
        ImmutableList.Builder<String> builder = new ImmutableList.Builder<String>();
        addToBuilder(builder, pdf.getDepend());
        addToBuilder(builder, pdf.getSoftDepend());
        return builder.build();
    }

    private void addToBuilder(ImmutableList.Builder<String> builder, List<String> list) {
        if (list != null) {
            builder.addAll(list);
        }
    }
    
    @Getter
    private static final DependenciesManager instance = new DependenciesManager();

    private enum DependencyType {
        NONE, CHILD, PARENT
    }
}
