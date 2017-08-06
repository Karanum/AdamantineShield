package com.karanumcoding.adamantineshield.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataCache {

	private Map<String, Integer> cache;
	private String tableName;
	private String dataRowName;

	public DataCache(String tableName, String dataRowName) {
		cache = Collections.synchronizedMap(new HashMap<>());
		this.tableName = tableName;
		this.dataRowName = dataRowName;
	}
	
	public Integer getDataId(Connection c, String data) throws SQLException {
		if (cache.containsKey(data))
			return cache.get(data);
		
		ResultSet r = c.createStatement().executeQuery("SELECT id FROM " + tableName + " WHERE " + dataRowName + " = '" + data + "'");
		if (!r.isBeforeFirst()) {
			c.createStatement().executeUpdate("INSERT INTO " + tableName + " (" + dataRowName + ") VALUES ('" + data + "');");
			r = c.createStatement().executeQuery("SELECT id FROM " + tableName + " WHERE " + dataRowName + " = '" + data + "'");
		}
		r.next();
		
		int result = r.getInt("id");
		r.close();
		
		cache.put(data, result);
		return result;
	}
	
	public Set<String> getCachedData() {
		return cache.keySet();
	}
	
}
