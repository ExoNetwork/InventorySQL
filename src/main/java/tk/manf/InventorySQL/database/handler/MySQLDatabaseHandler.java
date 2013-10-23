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
package tk.manf.InventorySQL.database.handler;

import com.google.common.io.CharStreams;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.Cleanup;

import lombok.SneakyThrows;
import lombok.ToString;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import tk.manf.InventorySQL.database.DatabaseHandler;
import tk.manf.InventorySQL.datahandling.exceptions.DataHandlingException;
import tk.manf.InventorySQL.manager.ConfigManager;
import tk.manf.InventorySQL.manager.DataHandlingManager;
import tk.manf.InventorySQL.manager.LoggingManager;

@ToString(doNotUseGetters = true)
public class MySQLDatabaseHandler implements DatabaseHandler {
    private Connection connection;
    private Queries q;

    @SneakyThrows(ClassNotFoundException.class)
    public MySQLDatabaseHandler() {
        Class.forName("com.mysql.jdbc.Driver");
    }

    @Override
    public void init(JavaPlugin plugin) throws SQLException, IOException {
        Connection con = getConnection();
        @Cleanup
        Statement stmt = con.createStatement();
        String[] queries = CharStreams.toString(new InputStreamReader(plugin.getResource("mysql/CREATE.sql"))).split(";");
        for (String query : queries) {
            LoggingManager.getInstance().d(query);
            stmt.execute(query);
        }
        q = new Queries(ConfigManager.getConfig(plugin, "dbhandler.yml"));
    }

    @Override
    public void savePlayerInventory(Player player) throws Exception {
        savePlayerInventory(ConfigManager.getInstance().getServerID(player), player.getName(), player.getInventory(), player.getEnderChest());
    }

    @Override
    public boolean loadPlayerInventory(Player player) throws Exception {
        LoggingManager.getInstance().d("Getting Player Inventory");
        final ItemStack[][] tmp = getPlayerInventory(ConfigManager.getInstance().getServerID(player), player.getName().toLowerCase());
        if (tmp != null) {
            LoggingManager.getInstance().d("Inventory found! Replacing");
            player.getInventory().setContents(tmp[0]);
            player.getInventory().setArmorContents(tmp[1]);
            player.getEnderChest().setContents(tmp[2]);
            return true;
        } else {
            LoggingManager.getInstance().d("No Inventory found");
            return false;
        }

    }

    private void savePlayerInventory(String serverID, String playername, PlayerInventory inv, Inventory ender) throws SQLException, DataHandlingException {
        savePlayerInventory(serverID, playername, inv.getContents(), inv.getArmorContents(), ender.getContents());
    }

    private void savePlayerInventory(String serverID, String playername, ItemStack[] content, ItemStack[] armor, ItemStack[] ender) throws SQLException, DataHandlingException {
        Connection con = getConnection();
        final int playerID = getPlayerID(playername, con);

        //Normal Inventory
        update(con, q.INSERT_INVENTORY_QUERY, playerID, serverID, new ItemStack[][]{content, armor});
        //Ender Chest
        update(con, q.INSERT_ENDERCHEST_QUERY, playerID, serverID, new ItemStack[][]{ender});
    }

    private ItemStack[][] getPlayerInventory(String serverID, String playername) throws SQLException, DataHandlingException {
        Connection con = getConnection();
        final int playerID = getPlayerID(playername, con);
        @Cleanup
        PreparedStatement invStmt = prepare(con, q.GET_PLAYER_INVENTORY_DATA_QUERY, playerID, serverID);
        ResultSet inv = invStmt.executeQuery();
        @Cleanup
        PreparedStatement enderStmt = prepare(con, q.GET_PLAYER_ENDERCHEST_DATA_QUERY, playerID, serverID);
        ResultSet ender = enderStmt.executeQuery();
        if (inv.next()) {
            return new ItemStack[][]{
                DataHandlingManager.getInstance().deserial(inv.getBytes("content")),
                DataHandlingManager.getInstance().deserial(inv.getBytes("armor")),
                ender.next() ? DataHandlingManager.getInstance().deserial(inv.getBytes("content")) : new ItemStack[]{}
            };
        }
        return null;
    }

    /**
     * Returns the ID of the given Player
     *
     * @param con Connection object
     * @param playername lowercased name of Player
     * <p/>
     * @return id
     * <p/>
     * @throws SQLException
     */
    private int getPlayerID(String playername, Connection con) throws SQLException {
        int id;
        PreparedStatement stmt = con.prepareStatement(q.GET_PLAYER_ID_QUERY);
        stmt.setString(1, playername);
        LoggingManager.getInstance().d(stmt.toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getInt(1);
        } else {
            stmt.close();
            stmt = con.prepareStatement(q.INSERT_PLAYER_QUERY, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, playername);
            LoggingManager.getInstance().d(stmt.toString());
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            } else {
                id = -1;
            }
        }
        stmt.close();
        return id;
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(3)) {
            connection = DriverManager.getConnection(ConfigManager.getInstance().getDbURL());
        }
        return connection;
    }

    private PreparedStatement prepare(Connection con, String query, int playerID, String serverID) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setInt(1, playerID);
        stmt.setString(2, serverID);
        LoggingManager.getInstance().d(stmt.toString());
        return stmt;
    }

    private void update(Connection con, String query, int playerID, String serverID, ItemStack[][] data) throws SQLException, DataHandlingException {
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setInt(1, playerID);
        int i = 2;
        for (ItemStack[] is : data) {
            stmt.setBytes(i, DataHandlingManager.getInstance().serial(is));
            i++;
        }
        stmt.setString(i, serverID);
        LoggingManager.getInstance().d(stmt.toString());
        stmt.executeUpdate();
        stmt.close();
    }

    private class Queries {
        private final String PREFIX;
        private final String SUFFIX;
        //DATABASES
        final String PLAYER_DATABASE;
        final String INVENTORY_DATABASE;
        final String ENDERCHEST_DATABASE;
        //LOAD QUERIES
        private final String GET_PLAYER_ID_QUERY;
        private final String GET_PLAYER_INVENTORY_DATA_QUERY;
        private final String GET_PLAYER_ENDERCHEST_DATA_QUERY;
        //INSERT QUERIES
        private final String INSERT_PLAYER_QUERY;
        private final String INSERT_INVENTORY_QUERY;
        private final String INSERT_ENDERCHEST_QUERY;
        //CONFIG VALUES
        private static final String CONFIG_TABLES_PREFIX = "tables.prefix";
        private static final String CONFIG_TABLES_SUFFIX = "tables.suffix";
        private static final String CONFIG_TABLES_PLAYER = "tables.player";
        private static final String CONFIG_TABLES_INVENTORY = "tables.inventory";
        private static final String CONFIG_TABLES_ENDERCHEST = "tables.enderchest";
        
        public Queries(FileConfiguration config) {
            this.PREFIX = config.getString(CONFIG_TABLES_PREFIX);
            this.SUFFIX = config.getString(CONFIG_TABLES_SUFFIX);
            //TABLES
            this.PLAYER_DATABASE = initialise(config, CONFIG_TABLES_PLAYER);
            this.INVENTORY_DATABASE = initialise(config, CONFIG_TABLES_INVENTORY);
            this.ENDERCHEST_DATABASE = initialise(config, CONFIG_TABLES_ENDERCHEST);
            //QUERIES
            this.GET_PLAYER_ID_QUERY = "SELECT id FROM " + PLAYER_DATABASE + " WHERE playername=? LIMIT 1";
            this.GET_PLAYER_INVENTORY_DATA_QUERY = "SELECT content, armor FROM " + INVENTORY_DATABASE + " WHERE playerID=? AND server=? LIMIT 1";
            this.GET_PLAYER_ENDERCHEST_DATA_QUERY = "SELECT content FROM " + ENDERCHEST_DATABASE + " WHERE playerID=? AND server=? LIMIT 1";
            this.INSERT_PLAYER_QUERY = "INSERT INTO " + PLAYER_DATABASE + " (id, playername) VALUES (NULL, ?)";
            this.INSERT_INVENTORY_QUERY = "INSERT INTO " + INVENTORY_DATABASE + " (id, playerID, content, armor, server) VALUES (NULL, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE content=VALUES(content), armor=VALUES(armor)";
            this.INSERT_ENDERCHEST_QUERY = "INSERT INTO " + ENDERCHEST_DATABASE + " (id, playerID, content, server) VALUES (NULL, ?, ?, ?) ON DUPLICATE KEY UPDATE content=VALUES(content)";
        }
        
        private String initialise(FileConfiguration config, final String NODE) {
            return PREFIX + config.getString(NODE) + SUFFIX;
        }
    }
}