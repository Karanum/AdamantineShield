package com.karanumcoding.adamantineshield.compat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.slf4j.Logger;

import com.karanumcoding.adamantineshield.AdamantineShield;
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
	
	public static void updateDatabase(AdamantineShield plugin, Connection c, int fromVersion) throws SQLException {
		if (fromVersion >= Database.DB_VERSION || fromVersion < 1) return;
		
		if (fromVersion < 2)
			try {
				c.setAutoCommit(false);
				migrateIds(plugin.getLogger(), c);
				c.commit();
				c.setAutoCommit(true);
			} catch (SQLException e) { 
				c.rollback();
				c.setAutoCommit(true);
				throw e; 
			}
		
		if (fromVersion < 3)
			try {
				c.setAutoCommit(false);
				addRollbackField(plugin.getLogger(), c);
				c.commit();
				c.setAutoCommit(true);
			} catch (SQLException e) {
				c.rollback();
				c.setAutoCommit(true);
				throw e;
			}
	}
	
	private static void migrateIds(Logger logger, Connection c) throws SQLException {
		DataCache cache = Database.idCache;
		
		logger.info("(v1 -> v2) Collecting all block and item IDs");
		
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
		
		Set<String> cacheData = cache.getCachedData();
		int size = cacheData.size();
		int curr = 0;
		for (String value : cache.getCachedData()) {
			int id = cache.getDataId(c, value);
			blockQuery.setInt(1, id);
			blockQuery.setString(2, value);
			blockQuery.executeUpdate();
			itemQuery.setInt(1, id);
			itemQuery.setString(2, value);
			itemQuery.executeUpdate();
			logger.info("(v1 -> v2) Converting IDs - " + (++curr) + "/" + size );
		}
		blockQuery.close();
		itemQuery.close();
		
		logger.info("(v1 -> v2) Cleaning up old IDs");
		
		c.createStatement().executeUpdate("ALTER TABLE AS_Block DROP COLUMN block;");
		c.createStatement().executeUpdate("ALTER TABLE AS_Container DROP COLUMN item;");
	}
	
	private static void addRollbackField(Logger logger, Connection c) throws SQLException {
		logger.info("(v2 -> v3) Adding rollback field to block table");
		c.createStatement().executeUpdate("ALTER TABLE AS_Block ADD rolled_back TINYINT(1) DEFAULT 0;");
		c.createStatement().executeUpdate("UPDATE AS_Block SET rolled_back = 0;");
		
		logger.info("(v2 -> v3) Adding rollback field to container table");
		c.createStatement().executeUpdate("ALTER TABLE AS_Container ADD rolled_back TINYINT(1) DEFAULT 0;");
		c.createStatement().executeUpdate("UPDATE AS_Container SET rolled_back = 0;");
	}
	
}
