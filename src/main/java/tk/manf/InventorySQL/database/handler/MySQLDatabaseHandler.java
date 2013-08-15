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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.zip.DataFormatException;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import tk.manf.InventorySQL.database.DatabaseHandler;
import tk.manf.InventorySQL.datahandling.exceptions.DataHandlingException;
import tk.manf.InventorySQL.manager.ConfigManager;
import tk.manf.InventorySQL.manager.DataHandlingManager;
import tk.manf.InventorySQL.manager.LoggingManager;

public class MySQLDatabaseHandler implements DatabaseHandler {
    private Connection connection;
    private static final String PLAYER_DATABASE = "player";
    private static final String INVENTORY_DATABASE = "inventory";
    private static final String GET_PLAYER_ID_QUERY = "SELECT id FROM " + PLAYER_DATABASE + " WHERE playername=? LIMIT 1";
    private static final String GET_PLAYER_INVENTORY_DATA_QUERY = "SELECT content, armor FROM " + INVENTORY_DATABASE + " WHERE playerID=? AND server=? LIMIT 1";
    private static final String INSERT_PLAYER_QUERY = "INSERT INTO " + PLAYER_DATABASE + " (id, playername) VALUES (NULL, ?)";
    private static final String INSERT_INVENTORY_QUERY = "INSERT INTO " + INVENTORY_DATABASE + " (id, playerID, content, armor, server) VALUES (NULL, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE content=VALUES(content), armor=VALUES(armor)";

    @SneakyThrows(ClassNotFoundException.class)
    public MySQLDatabaseHandler() {
        Class.forName("com.mysql.jdbc.Driver");
    }

    public void savePlayerInventory(Player player) throws Exception {
        savePlayerInventory(player.getName(), player.getInventory());
    }

    public boolean loadPlayerInventory(Player player) throws Exception {
        LoggingManager.getInstance().d("Getting Player Inventory");
        final ItemStack[][] tmp = getPlayerInventory(player.getName().toLowerCase());
        if (tmp != null) {
            LoggingManager.getInstance().d("Inventory found! Replacing");
            player.getInventory().setContents(tmp[0]);
            player.getInventory().setArmorContents(tmp[1]);
            return true;
        } else {
            LoggingManager.getInstance().d("No Inventory found");
            return false;
        }

    }

    private void savePlayerInventory(String playername, PlayerInventory inv) throws SQLException, DataHandlingException {
        savePlayerInventory(playername, inv.getContents(), inv.getArmorContents());
    }

    private void savePlayerInventory(String playername, ItemStack[] content, ItemStack[] armor) throws SQLException, DataHandlingException {
        Connection con = getConnection();
        PreparedStatement stmt = con.prepareStatement(INSERT_INVENTORY_QUERY);
        stmt.setInt(1, getPlayerID(playername));
        stmt.setBytes(2, DataHandlingManager.getInstance().serial(content));
        stmt.setBytes(3, DataHandlingManager.getInstance().serial(armor));
        stmt.setString(4, ConfigManager.getInstance().getServerID());
        LoggingManager.getInstance().d(stmt.toString());
        stmt.executeUpdate();
        stmt.close();
    }

    private ItemStack[][] getPlayerInventory(String playername) throws SQLException, DataFormatException, DataHandlingException {
        Connection con = getConnection();
        @Cleanup
        PreparedStatement stmt = con.prepareStatement(GET_PLAYER_INVENTORY_DATA_QUERY);
        stmt.setInt(1, getPlayerID(playername));
        stmt.setString(2, ConfigManager.getInstance().getServerID());
        LoggingManager.getInstance().d(stmt.toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new ItemStack[][]{
                DataHandlingManager.getInstance().deserial(rs.getBytes("content")),
                DataHandlingManager.getInstance().deserial(rs.getBytes("armor"))
            };
        }
        return null;
    }

    /**
     * Returns the ID of the given Player
     *
     * @param playername lowercased name of Player
     * <p/>
     * @return id
     * <p/>
     * @throws SQLException
     */
    private int getPlayerID(String playername) throws SQLException {
        int id;
        Connection con = getConnection();
        PreparedStatement stmt = con.prepareStatement(GET_PLAYER_ID_QUERY);
        stmt.setString(1, playername);
        LoggingManager.getInstance().d(stmt.toString());
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            id = rs.getInt(1);
        } else {
            stmt.close();
            stmt = con.prepareStatement(INSERT_PLAYER_QUERY, Statement.RETURN_GENERATED_KEYS);
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
}