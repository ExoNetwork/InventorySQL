package tk.manf.InventorySQL.commands;

import java.util.ArrayList;
import java.util.List;
import se.ranzdo.bukkit.methodcommand.CommandHandler;
import tk.manf.InventorySQL.InventorySQLPlugin;

public class CommandManager {
    private final List<AbstractCommandHandler> commands;
    private CommandHandler handler;
    private InventorySQLPlugin plugin;
    
    public CommandManager() {
        commands = new ArrayList<AbstractCommandHandler>(1);
        commands.add(new InvSQLCommand());
    }

    public void initialise(InventorySQLPlugin plugin) {
        this.plugin = plugin;
        handler = new CommandHandler(plugin);
        for (AbstractCommandHandler command : commands) {
            command.initialise(handler);
            command.setCommandManager(this);
        }
    }
    
    public void disable() {
        this.plugin = null;
    }

    public InventorySQLPlugin getPlugin() {
        return plugin;
    }
}
