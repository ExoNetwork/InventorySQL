package tk.manf.InventorySQL.commands;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se.ranzdo.bukkit.methodcommand.CommandHandler;
import tk.manf.InventorySQL.InventorySQLPlugin;

@NoArgsConstructor
/**
 * Abstract class which needs to be extended for new commands
 */
public abstract class AbstractCommandHandler {
    @Setter(AccessLevel.PACKAGE)
    private CommandManager commandManager;
    
    public final InventorySQLPlugin getPlugin() {
        return commandManager.getPlugin();
    }
    
    public final AbstractCommandHandler initialise(CommandHandler handler) {
        handler.registerCommands(this);
        return this;
    }
}
