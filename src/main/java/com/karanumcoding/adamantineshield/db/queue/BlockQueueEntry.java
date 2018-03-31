package com.karanumcoding.adamantineshield.db.queue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.spongepowered.api.block.BlockSnapshot;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.enums.ActionType;
import com.karanumcoding.adamantineshield.util.DataUtils;

public class BlockQueueEntry extends QueueEntry {

	private static final String QUERY = "INSERT INTO AS_Block (x, y, z, world, type, cause, id, data, time) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
	
	private BlockSnapshot block;
	//private TileEntity entity;
	private ActionType type;
	private String cause;
	private long timestamp;
	
	public BlockQueueEntry(BlockSnapshot block, ActionType type, String cause, long timestamp) {
		this.block = block;
		this.type = type;
		this.cause = cause.toLowerCase();
		this.timestamp = timestamp;
		//this.entity = null;
	}

	@Override
	public void writeToConnection(Connection c) throws SQLException {
		String world = block.getWorldUniqueId().toString();
		
		PreparedStatement ps = c.prepareStatement(QUERY);
		ps.setInt(1, block.getPosition().getX());
		ps.setInt(2, block.getPosition().getY());
		ps.setInt(3, block.getPosition().getZ());
		ps.setInt(4, Database.worldCache.getDataId(c, world));
		ps.setByte(5, (byte) type.ordinal());
		ps.setInt(6, Database.causeCache.getDataId(c, cause));
		ps.setInt(7, Database.idCache.getDataId(c, block.getState().getType().getId()));
		
		ps.setNull(8, Types.VARCHAR);
		try {
			String data = DataUtils.dataToString(block.toContainer());
			if (data != null)
				ps.setString(8, data);
		} catch (IOException e) {
			e.printStackTrace();
		}

			
		ps.setLong(9, timestamp);
		ps.executeUpdate();
	}
	
}
