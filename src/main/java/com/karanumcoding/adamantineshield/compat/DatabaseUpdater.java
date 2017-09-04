package com.karanumcoding.adamantineshield.compat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.karanumcoding.adamantineshield.db.DataCache;
import com.karanumcoding.adamantineshield.db.Database;

public final class DatabaseUpdater {
	
	private DatabaseUpdater() {}
	
	public static int checkVersion(Connection c) {
		try (ResultSet r = c.createStatement().executeQuery("SELECT * FROM AS_Meta;")) {
			r.next();
			return r.getInt("version_id");
		} catch (SQLException e) {
			return 0;
		}
	}
	
	public static void updateDatabase(Connection c, int fromVersion) throws SQLException {
		if (fromVersion >= Database.DB_VERSION || fromVersion < 1) return;
		
		if (fromVersion < 2)
			migrateIds(c);
		if (fromVersion < 3)
			addRollbackField(c);
	}
	
	private static void migrateIds(Connection c) throws SQLException {
		DataCache cache = Database.idCache;
		
		ResultSet r = c.createStatement().executeQuery("SELECT DISTINCT item FROM AS_Container;");
		if (r.isBeforeFirst()) {
			while (r.next()) {
				cache.getDataId(c, r.getString("item"));
			}
		}
		
		r = c.createStatement().executeQuery("SELECT DISTINCT block FROM AS_Block;");
		if (r.isBeforeFirst()) {
			while (r.next()) {
				cache.getDataId(c, r.getString("block"));
			}
		}
		
		c.createStatement().executeUpdate("ALTER TABLE AS_Block ADD id INT;");
		c.createStatement().executeUpdate("ALTER TABLE AS_Container ADD id INT;");
		
		PreparedStatement blockQuery = c.prepareStatement("UPDATE AS_Block SET id = ? WHERE block = ?;");
		PreparedStatement itemQuery = c.prepareStatement("UPDATE AS_Container SET id = ? WHERE item = ?;");
		for (String value : cache.getCachedData()) {
			int id = cache.getDataId(c, value);
			blockQuery.setInt(1, id);
			blockQuery.setString(2, value);
			blockQuery.executeUpdate();
			itemQuery.setInt(1, id);
			itemQuery.setString(2, value);
			itemQuery.executeUpdate();
		}
		blockQuery.close();
		itemQuery.close();
		
		c.createStatement().executeUpdate("ALTER TABLE AS_Block DROP COLUMN block;");
		c.createStatement().executeUpdate("ALTER TABLE AS_Container DROP COLUMN item;");
	}
	
	private static void addRollbackField(Connection c) throws SQLException {
		c.createStatement().executeUpdate("ALTER TABLE AS_Block ADD rolled_back TINYINT(1) DEFAULT 0;");
		c.createStatement().executeUpdate("ALTER TABLE AS_Container ADD rolled_back TINYINT(1) DEFAULT 0;");
		
		c.createStatement().executeUpdate("UPDATE AS_Block SET rolled_back = 0;");
		c.createStatement().executeUpdate("UPDATE AS_Container SET rolled_back = 0;");
	}
	
}
