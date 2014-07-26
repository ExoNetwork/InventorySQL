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

package tk.manf.InventorySQL;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.lang.ref.WeakReference;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tk.manf.InventorySQL.commands.InvSQLCommand;
import tk.manf.InventorySQL.commands.SwitchCommand;
import tk.manf.InventorySQL.manager.LoggingManager;

public class CommandManager implements CommandExecutor {
    private Map<String, InternalCommand> commands;
    private WeakReference<JavaPlugin> plugin;
    private WeakReference<ClassLoader> loader;
    
    public CommandManager() {
        ImmutableMap.Builder<String, InternalCommand> b = new ImmutableMap.Builder<String, InternalCommand>();
        put(b, new InvSQLCommand());
        put(b, new SwitchCommand());
        this.commands = b.build();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            Preconditions.checkArgument(commands.containsKey(cmd.getName()), "Command not found");
            commands.get(cmd.getName()).onCommand(sender, args);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(e.getMessage());
        }
        return true;
    }

    public void initialise(JavaPlugin plugin, ClassLoader loader) {
        this.plugin = new WeakReference<JavaPlugin>(plugin);
        this.loader = new WeakReference<ClassLoader>(loader);
        for (InternalCommand cmd : commands.values()) {
            PluginCommand tmp = plugin.getCommand(cmd.getIdentifier());
            if (tmp == null) {
                LoggingManager.getInstance().log(LoggingManager.Level.ERROR, "Command not found: " + cmd.getIdentifier());
                continue;
            }
            tmp.setExecutor(this);
            cmd.setManager(this);
        }
    }

    public JavaPlugin getPlugin() {
        return plugin.get();
    }
    
    public ClassLoader getClassLoader() {
        return loader.get();
    }    
    
    private void put(ImmutableMap.Builder<String, InternalCommand> b, InternalCommand cmd) {
        b.put(cmd.getIdentifier(), cmd);
    }
    
    @Data
    public static abstract class InternalCommand {
        private final String identifier;
        @Setter(AccessLevel.MODULE)
        private CommandManager manager;
        
        public Player getOptionalPlayer(CommandSender sender, String[] args, int index) {
            return args.length == index ? toPlayer(sender) : Bukkit.getPlayer(args[index]);
        }
        
        public Player toPlayer(CommandSender sender) {
            Preconditions.checkArgument(sender instanceof Player, "Command is for Player only");
            return (Player) sender;
        }

        public void checkPermission(CommandSender sender, String perm) {
            Preconditions.checkArgument(sender.hasPermission(perm), ChatColor.RED + "You do not have permissions to do this!");
        }

        public void checkNotNull(Object o, String msg) {
            Preconditions.checkArgument(o != null, msg);
        }

        public JavaPlugin getPlugin() {
            return manager.getPlugin();
        }
        
        public ClassLoader getClassLoader() {
            return manager.getClassLoader();
        }
        
        public abstract void onCommand(CommandSender sender, String[] args);
    }
}
