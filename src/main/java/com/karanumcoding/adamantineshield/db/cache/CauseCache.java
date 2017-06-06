package com.karanumcoding.adamantineshield.db.cache;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CauseCache {

	private Map<String, Integer> cache;
	
	public CauseCache() {
		cache = Collections.synchronizedMap(new HashMap<>());
	}
	
	public Integer getDataId(Connection c, String cause) throws SQLException {
		if (cache.containsKey(cause))
			return cache.get(cause);
		
		ResultSet r = c.createStatement().executeQuery("SELECT id FROM AS_Cause WHERE cause = '" + cause + "'");
		if (!r.isBeforeFirst()) {
			c.createStatement().executeUpdate("INSERT INTO AS_Cause (cause) VALUES ('" + cause + "');");
			r = c.createStatement().executeQuery("SELECT id FROM AS_Cause WHERE cause = '" + cause + "'");
		}
		r.next();
		
		int result = r.getInt("id");
		cache.put(cause, result);
		return result;
	}
	
}
