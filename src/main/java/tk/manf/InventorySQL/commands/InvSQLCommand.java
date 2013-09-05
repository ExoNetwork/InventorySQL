package tk.manf.InventorySQL.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;
import tk.manf.InventorySQL.manager.ConfigManager;
import tk.manf.InventorySQL.manager.DatabaseManager;
import tk.manf.InventorySQL.manager.LanguageManager;
import tk.manf.InventorySQL.manager.LoggingManager;
import tk.manf.InventorySQL.util.Language;

public class InvSQLCommand extends AbstractCommandHandler {
    private static final String IDENTIFIER = "invsql";
    private static final String SAVE = IDENTIFIER + " " + "save";
    private static final String RELOAD = IDENTIFIER + " " + "reload";

    @Command(identifier = SAVE,
            description = "Saves yourself",
            permissions = {"InventorySQL.save.self"})
    public void saveCurrentPlayer(Player sender) {
        savePlayer(sender, sender);
    }

    @Command(identifier = SAVE,
            description = "Saves specific player",
            onlyPlayers = false,
            permissions = {"InventorySQL.save.other"})
    public void savePlayer(CommandSender sender, @Arg(name = "target") Player target) {
        if (target != null) {
            DatabaseManager.getInstance().savePlayer(target);
            sender.sendMessage("Player " + target.getName() + " has been saved!");
        }
    }

    @Command(identifier = InvSQLCommand.RELOAD,
            description = "Reloads the Language or the Config",
            onlyPlayers = false,
            permissions = {"InventorySQL.reload"})
    public void reload(CommandSender sender, @Arg(name = "[Language|Config]") String target) {
        try {
            if (target.equalsIgnoreCase("LANGUAGE")) {
                ConfigManager.getInstance().reloadConfig(getPlugin(), getPlugin().getReflectionLoader());
            } else if (target.equalsIgnoreCase("CONFIG")) {
                ConfigManager.getInstance().loadLanguage(getPlugin());
            } else {
                sender.sendMessage("You can only reload Language or Config");
            }
        } catch (Exception ex) {
            LoggingManager.getInstance().log(ex);
            LanguageManager.getInstance().sendMessage(sender, Language.COMMAND_ERROR);
        }

    }

}
