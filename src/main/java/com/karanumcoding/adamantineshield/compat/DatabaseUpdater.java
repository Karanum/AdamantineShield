package com.karanumcoding.adamantineshield.compat;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseUpdater {

	private static final int LATEST_VERSION = 2;
	
	private DatabaseUpdater() {}
	
	public static void updateDatabase(Connection c, int fromVersion) throws SQLException {
		if (fromVersion >= LATEST_VERSION || fromVersion < 1) return;
		
		if (fromVersion == 1)
			migrateBlocksAndItems(c);
	}
	
	private static void migrateBlocksAndItems(Connection c) {
		//TODO: Implement moving over block and item IDs to separate tables
	}
	
}
