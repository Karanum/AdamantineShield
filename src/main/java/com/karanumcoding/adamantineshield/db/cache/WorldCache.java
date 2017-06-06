package com.karanumcoding.adamantineshield.db.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class WorldCache {

	private Map<String, Integer> cache;
	
	public WorldCache() {
		cache = Collections.synchronizedMap(new HashMap<>());
	}
	
	public Integer getDataId(Connection c, String uuid) throws SQLException {
		if (cache.containsKey(uuid))
			return cache.get(uuid);
		
		ResultSet r = c.createStatement().executeQuery("SELECT id FROM AS_World WHERE world = '" + uuid + "'");
		if (!r.isBeforeFirst()) {
			c.createStatement().executeUpdate("INSERT INTO AS_World (world) VALUES ('" + uuid + "');");
			r = c.createStatement().executeQuery("SELECT id FROM AS_World WHERE world = '" + uuid + "'");
		}
		r.next();
		
		int result = r.getInt("id");
		cache.put(uuid, result);
		return result;
	}
	
}
