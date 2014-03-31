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

import java.lang.reflect.Method;
import java.util.HashMap;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class UpdateEventManager implements Listener {
    private final HashMap<String, UpdateEvent> events;

    private UpdateEventManager() {
        events = new HashMap<String, UpdateEvent>(7);
        //I dont think its useful to save whenever we're joining
        //put("join", PlayerJoinEvent.class, "doGenericEvent");
        put("quit", PlayerQuitEvent.class, "doGenericEvent");
        put("changeworld", PlayerChangedWorldEvent.class, "doGenericEvent");
        put("respawn", PlayerRespawnEvent.class, "doGenericEvent");
        put("bedenter", PlayerBedEnterEvent.class, "doGenericEvent");
        put("bedleave", PlayerBedLeaveEvent.class, "doGenericEvent");
        put("death", PlayerDeathEvent.class, "doPlayerDeath", false);
    }

    public void initialise(Plugin plugin) {
        UpdateEvent.setManager(plugin.getServer().getPluginManager());
        for (String event : events.keySet()) {
            if (ConfigManager.getInstance().getUpdateEvents().contains(event)) {
                events.get(event).register(this, plugin);
            }
        }
    }

    private void put(String name, Class<? extends Event> event, String method) {
        put(name, event, method, true);
    }

    private void put(String name, Class<? extends Event> event, String method, boolean useSuper) {
        try {
            LoggingManager.getInstance().d("Handling " + event.getName() + " AS " + method + "(Using super: " + useSuper + ")");
            events.put(name, new UpdateEvent(event, getClass().getMethod(method, useSuper ? event.getSuperclass() : event)));
        } catch (Exception ex) {
            LoggingManager.getInstance().log(ex);
        }
    }

    public void doPlayerDeath(PlayerDeathEvent event) {
        doGenericEvent(event, event.getEntity());
    }

    public void doGenericEvent(PlayerEvent event) {
        doGenericEvent(event, event.getPlayer());
    }

    private void doGenericEvent(Event event, Player player) {
        LoggingManager.getInstance().d("on" + event.getEventName() + "(" + player.getName().toString() + ")");
        DatabaseManager.getInstance().savePlayer(player);
    }
    @Getter
    private static final UpdateEventManager instance = new UpdateEventManager();
}

@AllArgsConstructor(access = AccessLevel.PRIVATE)
final class UpdateEvent {
    private final Class<? extends Event> event;
    private final EventExecutor exec;

    protected UpdateEvent(final Class<? extends Event> type, final Method m) {
        this(type, new EventExecutor() {
            public void execute(Listener listener, Event event) throws EventException {
                try {
                    if (listener instanceof UpdateEventManager) {
                        // Prevent illegal Argument Exception
                        if(m.getParameterTypes()[0] == event.getClass()) {
                             m.invoke(listener, event);
                        }
                    }
                } catch (Exception e) {
                    LoggingManager.getInstance().log(LoggingManager.Level.ERROR, "Error with Event: " + String.valueOf(listener) + " - EV: " + event == null ? "failed" : event.getEventName());
                    LoggingManager.getInstance().log(e);
                }
            }
        });
    }

    public void register(Listener listener, Plugin plugin) {
        LoggingManager.getInstance().d("Registering " + event.getName());
        manager.registerEvent(event, listener, EventPriority.NORMAL, exec, plugin, false);
    }
    @Setter
    private static PluginManager manager;
}