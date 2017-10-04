package com.karanumcoding.adamantineshield.db;

import org.spongepowered.api.entity.living.player.Player;

import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.FilterSet;

public class QueryHelper {

	private QueryHelper() {}
	
	public static final String INSPECT_BLOCK_QUERY = "SELECT x, y, z, type, AS_Id.value, data, rolled_back, time, AS_Cause.cause, AS_World.world FROM AS_Block, AS_World, AS_Cause, AS_Id "
			+ "WHERE x = ? AND y = ? AND z = ? AND AS_World.world = ? AND AS_Block.cause = AS_Cause.id AND AS_Block.world = AS_World.id AND AS_Block.id = AS_Id.id ORDER BY time DESC, type;";
	
	public static final String INSPECT_CONTAINER_QUERY = "SELECT x, y, z, type, slot, AS_Id.value, count, data, slot, rolled_back, time, AS_Cause.cause, AS_World.world "
			+ "FROM AS_Container, AS_World, AS_Cause, AS_Id "
			+ "WHERE x = ? AND y = ? AND z = ? AND AS_World.world = ? AND AS_Container.cause = AS_Cause.id AND AS_Container.world = AS_World.id AND AS_Container.id = AS_Id.id "
			+ "ORDER BY time DESC, type;";
	
	public static String getLookupQuery(FilterSet filters, Player p, int worldId) {
		LookupType type = filters.getLookupType();
		String table = type.getTable();
		
		String query = "SELECT x, y, z, type, time, data, rolled_back, AS_Cause.cause, AS_World.world, AS_Id.value";
		if (type.getRelevantColumns() != null) {
			query += ", " + type.getRelevantColumns();
		}
		query += " FROM " + table + ", AS_Cause, AS_World, AS_Id "
				+ "WHERE AS_Cause.id = " + table + ".cause AND " + table + ".world = " + worldId + " "
				+ "AND AS_Id.id = " + table + ".id AND " + filters.getQueryConditions(p) + " "
				+ "ORDER BY time DESC, type";
		
		return query;
	}
	
	public static String getRollbackUpdateQuery(FilterSet filters, Player p, int worldId, boolean rolledBack) {
		//TODO: Fix syntax error in this query
		
		String table = filters.getLookupType().getTable();
		
		String query = "UPDATE AS_Cause, AS_Id, " + table + " "
				+ "SET " + table + ".rolled_back = " + (rolledBack ? "0" : "1") + " "
				+ "WHERE " + filters.getQueryConditions(p);
		return query;
	}
	
}
