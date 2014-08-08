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

package tk.manf.InventorySQL.tasks;

import com.evilmidget38.UUIDFetcher;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class NameConversionTask extends BukkitRunnable {
    private ArrayList<String> namesToConvert = null;

    private Connection con = null;
    private String tableName = null;

    private final String ALTER_TABLE_REMOVE_QUERY;
    private final String GET_PLAYER_NAMES_QUERY;

    public NameConversionTask(ArrayList<String> namesToConvert, Connection con, String tableName) {
       this.namesToConvert = namesToConvert;

        this.con = con;
        this.tableName = tableName;

        this.ALTER_TABLE_REMOVE_QUERY = "ALTER TABLE " + tableName + " DROP `playername`";
        this.GET_PLAYER_NAMES_QUERY = "SELECT playername FROM " + tableName;
    }

    @Override
    public void run() {
        try {
            UUIDFetcher uuidFetcher = new UUIDFetcher(namesToConvert);

            Map<String,  UUID> response = uuidFetcher.call();

            PreparedStatement stmt = con.prepareStatement(GET_PLAYER_NAMES_QUERY);

            ResultSet rs = stmt.executeQuery();

            PreparedStatement batchStmt = con.prepareStatement("UPDATE " + tableName + " SET playeruuid = ? WHERE playername = ?");

            while(rs.next()) {
                if(!response.keySet().contains(rs.getString("playername"))) {continue;}
                batchStmt.setString(1, String.valueOf(response.get(rs.getString("playername"))));
                batchStmt.setString(2, rs.getString("playername"));
                batchStmt.addBatch();
            }

            stmt.close();

            batchStmt.executeBatch();
            batchStmt.close();

            PreparedStatement rStmt = con.prepareStatement(ALTER_TABLE_REMOVE_QUERY);
            rStmt.execute();
            rStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
