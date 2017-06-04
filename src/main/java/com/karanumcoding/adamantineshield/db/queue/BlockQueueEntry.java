package com.karanumcoding.adamantineshield.db.queue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.tileentity.TileEntity;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.enums.ActionType;
import com.karanumcoding.adamantineshield.util.DataUtils;

public class BlockQueueEntry extends QueueEntry {

	private static final String QUERY = "INSERT INTO AS_Block (x, y, z, world, type, cause, block, data, time) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
	
	private BlockSnapshot block;
	private TileEntity entity;
	private ActionType type;
	private String cause;
	private long timestamp;
	
	public BlockQueueEntry(BlockSnapshot block, ActionType type, String cause, long timestamp) {
		this.block = block;
		this.type = type;
		this.cause = cause;
		this.timestamp = timestamp;
		this.entity = null;
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
//		if ((type == ActionType.DESTROY || type == ActionType.MOB_DESTROY) && entity != null) {
//			try {
//				ps.setString(8, DataUtils.dataToString(entity.toContainer()));
//			} catch (IOException e) {
//				e.printStackTrace();
//				ps.setNull(8, Types.VARCHAR);
//			}
//		} else {
//			ps.setNull(8, Types.VARCHAR);
//		}
		ps.setNull(8, Types.VARCHAR);
		ps.setLong(9, timestamp);
		ps.executeUpdate();
	}
	
}
