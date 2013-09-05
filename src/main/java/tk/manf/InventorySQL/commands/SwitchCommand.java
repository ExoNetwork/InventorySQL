package tk.manf.InventorySQL.commands;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.entity.Player;
import se.ranzdo.bukkit.methodcommand.Arg;
import se.ranzdo.bukkit.methodcommand.Command;
import tk.manf.InventorySQL.manager.DatabaseManager;

public class SwitchCommand extends AbstractCommandHandler {
    private static final String IDENTIFIER = "switch";

    @Command(identifier = IDENTIFIER,
            description = "Switches your Server",
            permissions = {"InventorySQL.switch.self"})
    public void changeServerSelf(Player sender, @Arg(name = "server") String server) {
        changeServerTarget(sender, server, sender);
    }

    @Command(identifier = IDENTIFIER,
            description = "Switches the Server for others",
            permissions = {"InventorySQL.switch.other"})
    public void changeServerTarget(Player sender, @Arg(name = "server") String server, @Arg(name = "target") Player target) {
        DatabaseManager.getInstance().savePlayer(target);
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {}
        
        target.sendPluginMessage(getPlugin(), "BungeeCord", b.toByteArray());
    }

}
