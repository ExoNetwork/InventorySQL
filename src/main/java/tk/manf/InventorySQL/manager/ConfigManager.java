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

import lombok.Cleanup;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public final class ConfigManager {
    @Getter
    private List<String> updateEvents;
    @Getter
    private String dbURL, serializer, compressor, databaseHandler;
    private String serverID;
    @Getter
    private FileConfiguration language;
    @Getter
    private int saveInterval; 
    @Getter
    private boolean metricsEnabled, autoUpdateEnabled;

    private ConfigManager() {
    }

    public void initialise(JavaPlugin plugin) throws IOException, NoSuchAlgorithmException {
        //maybe use testserialisation, just need to finally change something
        loadLanguage(plugin);
        loadConfig(plugin);
        //dbURL = "jdbc:mysql://" + "localhost" + ":" + 3306 + "/" + "mydb" + "?" + "user=" + "root" + "&" + "password=" + "68836883";
    }

    public void loadLanguage(JavaPlugin plugin) throws IOException {
        language = getConfig(plugin, "language.yml");
    }

    public void reloadConfig(JavaPlugin plugin, ClassLoader cl) throws Exception {
        loadConfig(plugin);
        DataHandlingManager.getInstance().initialise(cl);
        DatabaseManager.getInstance().reload(plugin, cl);
    }

    public String getLangugagePattern(String id, String def) {
        return language.getString(id, def);
    }
    
    //Return different ID for Player in case of multiworld support
    public String getServerID(Player player) {
        return serverID; // + " - " + player.getWorld().getName();
    }
    
    private void loadConfig(JavaPlugin plugin) throws IOException, NoSuchAlgorithmException {
        FileConfiguration config = getConfig(plugin, "config.yml");
        serverID = md5(config.getString("serverID", ""));
        serializer = config.getString("data.serializer", "tk.manf.InventorySQL.datahandling.serializer.JSONSerializer");
        compressor = config.getString("data.compressor", "tk.manf.InventorySQL.datahandling.compressor.GZipCompressor");
        databaseHandler = config.getString("database.handler", "tk.manf.InventorySQL.database.handler.MySQLDatabaseHandler");
        updateEvents = config.getStringList("update-events");
        metricsEnabled = config.getBoolean("enable-metrics");
        saveInterval = config.getInt("save-interval") * 20;
        //add default value in case of user did an upgrade to 3.1 
        autoUpdateEnabled = config.getBoolean("auto-update", true);
        dbURL = config.getString("database.url");

        if (!metricsEnabled) {
            LoggingManager.getInstance().logDeveloperMessage("manf", LoggingManager.DeveloperMessages.METRICS_OFF);
        }
    }
    
    public static FileConfiguration getConfig(JavaPlugin plugin, String name) throws IOException {
        File file = new File(plugin.getDataFolder(), name);
        if (file.createNewFile()) {
            @Cleanup
            InputStream is = plugin.getResource(name);
            @Cleanup
            FileOutputStream os = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }
    
    private static String md5(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        return toHex(MessageDigest.getInstance("MD5").digest(input.getBytes("UTF-8")));
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    @Getter
    private static final ConfigManager instance = new ConfigManager();
}
