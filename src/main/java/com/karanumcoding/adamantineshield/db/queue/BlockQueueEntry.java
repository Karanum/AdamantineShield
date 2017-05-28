package com.karanumcoding.adamantineshield.db.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.spongepowered.api.block.BlockSnapshot;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.enums.ActionType;

public class BlockQueueEntry extends QueueEntry {

	private static final String QUERY_GET_WORLD = "SELECT id FROM AS_World WHERE world = ?";
	private static final String QUERY_GET_CAUSE = "SELECT id FROM AS_Cause WHERE cause = ?";
	private static final String QUERY_ADD_WORLD = "INSERT INTO AS_World (world) VALUES (?);";
	private static final String QUERY_ADD_CAUSE = "INSERT INTO AS_Cause (cause) VALUES (?);";
	private static final String QUERY = "INSERT INTO AS_Block (x, y, z, world, type, cause, block, data, time) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
	
	private BlockSnapshot block;
	private ActionType type;
	private String cause;
	private long timestamp;
	
	public BlockQueueEntry(BlockSnapshot block, ActionType type, String cause, long timestamp) {
		this.block = block;
		this.type = type;
		this.cause = cause;
		this.timestamp = timestamp;
	}

	@Override
	public void writeToConnection(Connection c) throws SQLException {
		String world = block.getWorldUniqueId().toString();
		
		if (!Database.worldCache.containsKey(world)) {
			PreparedStatement ps1 = c.prepareStatement(QUERY_GET_WORLD);
			ps1.setString(1, world);
			ResultSet result1 = ps1.executeQuery();
			if (!result1.isBeforeFirst()) {
				//TODO: Add thingamajig to the database instead
				PreparedStatement add = c.prepareStatement(QUERY_ADD_WORLD);
				add.setString(1, world);
				ResultSet r = add.executeQuery();
				Database.worldCache.put(world, r.getInt("id"));
			} else {
				result1.next();
				Database.worldCache.put(world, result1.getInt("id"));
			}
		}
		int worldId = Database.worldCache.get(world);
		
		if (!Database.causeCache.containsKey(cause)) {
			PreparedStatement ps2 = c.prepareStatement(QUERY_GET_CAUSE);
			ps2.setString(1, cause);
			ResultSet result2 = ps2.executeQuery();
			if (!result2.isBeforeFirst()) {
				//TODO: Add thingamajig to the database instead
			} else {
				result2.next();
				Database.causeCache.put(cause, result2.getInt("id"));
			}
		}
		int causeId = Database.causeCache.get(cause);
		
		PreparedStatement ps = c.prepareStatement(QUERY);
		ps.setInt(1, block.getPosition().getX());
		ps.setInt(2, block.getPosition().getY());
		ps.setInt(3, block.getPosition().getZ());
		ps.setInt(4, worldId);
		ps.setString(5, type.toString());
		ps.setInt(6, causeId);
		ps.setString(7, block.getState().getType().getId());
		ps.setString(8, "NULL");
		ps.setLong(9, timestamp);
		ps.executeQuery();
	}
	
}
