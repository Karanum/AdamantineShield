package com.karanumcoding.adamantineshield.db.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.spongepowered.api.block.BlockSnapshot;

import com.karanumcoding.adamantineshield.ActionType;

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
		PreparedStatement ps = c.prepareStatement(QUERY);
		ps.setInt(1, block.getPosition().getX());
		ps.setInt(2, block.getPosition().getY());
		ps.setInt(3, block.getPosition().getZ());
		ps.setString(4, block.getWorldUniqueId().toString());
		ps.setString(5, type.toString());
		ps.setString(6, cause);
		ps.setString(7, block.getState().getType().getId());
		ps.setString(8, "NULL");
		ps.setLong(9, timestamp);
		ps.executeQuery();
	}
	
}
