package com.karanumcoding.adamantineshield.db.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.spongepowered.api.block.BlockSnapshot;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.enums.ActionType;

public class BlockQueueEntry extends QueueEntry {

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
		ResultSet r = null;
		
		if (!Database.worldCache.containsKey(world)) {
			r = c.createStatement().executeQuery("SELECT id FROM AS_World WHERE world = '" + world + "'");
			if (!r.isBeforeFirst()) {
				Statement s = c.createStatement();
				s.executeUpdate("INSERT INTO AS_World (world) VALUES ('" + world + "');");
				r = c.createStatement().executeQuery("SELECT id FROM AS_World WHERE world = '" + world + "'");
			}
			r.next();
			Database.worldCache.put(world, r.getInt("id"));
		}
		int worldId = Database.worldCache.get(world);
		
		if (!Database.causeCache.containsKey(cause)) {
			r = c.createStatement().executeQuery("SELECT id FROM AS_Cause WHERE cause = '" + cause + "'");
			if (!r.isBeforeFirst()) {
				Statement s = c.createStatement();
				s.executeUpdate("INSERT INTO AS_Cause (cause) VALUES ('" + cause + "');");
				r = c.createStatement().executeQuery("SELECT id FROM AS_Cause WHERE cause = '" + cause + "'");
			}
			r.next();
			Database.causeCache.put(cause, r.getInt("id"));
		}
		int causeId = Database.causeCache.get(cause);
		
		PreparedStatement ps = c.prepareStatement(QUERY);
		ps.setInt(1, block.getPosition().getX());
		ps.setInt(2, block.getPosition().getY());
		ps.setInt(3, block.getPosition().getZ());
		ps.setInt(4, worldId);
		ps.setByte(5, (byte) type.ordinal());
		ps.setInt(6, causeId);
		ps.setString(7, block.getState().getType().getId());
		ps.setString(8, "NULL");	//TODO: Find a way to efficiently store block data
		ps.setLong(9, timestamp);
		ps.executeUpdate();
	}
	
}
