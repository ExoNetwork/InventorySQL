package tk.manf.InventorySQL.api;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import tk.manf.InventorySQL.manager.DatabaseManager;
import tk.manf.InventorySQL.manager.InventoryLockingSystem;

@NoArgsConstructor
public class InventorySQLAPI {
    private static final String CHANNEL = "BungeeCord";
    private JavaPlugin plugin;
    private boolean sending;

    /**
     * Initialises the API
     * Called internally. Should ONLY be called by
     * InventorySQL
     * @param plugin InventorySQL Plugin 
     */
    public void init(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Destroy references to Plugin
     * Called internally. Should ONLY be called by
     * InventorySQL 
     * 
     * @param plugin InventorySQL Plugin
     */
    public void disable(JavaPlugin plugin) {
        this.plugin = null;
    }
    
    /**
     * Switches given Player to given Server
     * Saving Inventory of Player and then move him to the given Server
     * @param target Target Player
     * @param server Servername
     */
    public void switchPlayer(Player target, String server) {
        InventoryLockingSystem.getInstance().addLock(target.getName());
        DatabaseManager.getInstance().savePlayer(target);
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException e) {
        }
        sendPluginMessage(target, b.toByteArray());
        InventoryLockingSystem.getInstance().removeLock(target.getName());
    }

    private void sendPluginMessage(Player player, byte[] message) {
        if (!sending) {
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
            sending = true;
        }
        player.sendPluginMessage(plugin, CHANNEL, message);
    }
    
    @Getter
    private static final InventorySQLAPI API = new InventorySQLAPI();
}
