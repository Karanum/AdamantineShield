package com.karanumcoding.adamantineshield.db.queue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.BlockCarrier;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.enums.ActionType;
import com.karanumcoding.adamantineshield.util.DataUtils;

public class InventoryQueueEntry extends QueueEntry {

	private static final String QUERY = "INSERT INTO AS_Container (x, y, z, multiblock, world, type, slot, cause, id, count, data, time) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	
	private BlockCarrier carrier;
	private int slot;
	private ItemStackSnapshot item;
	private ActionType type;
	private Player cause;
	private long timestamp;
	
	public InventoryQueueEntry(BlockCarrier carrier, int slot, ItemStackSnapshot item, ActionType type, Player cause, long timestamp) {
		this.carrier = carrier;
		this.slot = slot;
		this.item = item;
		this.type = type;
		this.cause = cause;
		this.timestamp = timestamp;
	}
	
	@Override
	public void writeToConnection(Connection c) throws SQLException {
		PreparedStatement ps;
//		if (carrier instanceof MultiBlockCarrier) {
//			ps = prepareMultiBlockQuery(c);
//		} else {
//			ps = prepareSingleBlockQuery(c);
//		}
		ps = prepareSingleBlockQuery(c);
		ps.executeUpdate();
	}
	
	private PreparedStatement prepareSingleBlockQuery(Connection c) throws SQLException {
		Location<World> loc = carrier.getLocation();
		
		PreparedStatement ps = c.prepareStatement(QUERY);
		ps.setInt(1, loc.getBlockX());
		ps.setInt(2, loc.getBlockY());
		ps.setInt(3, loc.getBlockZ());
		ps.setNull(4, Types.INTEGER);
		ps.setInt(5, Database.worldCache.getDataId(c, carrier.getLocation().getExtent().getUniqueId().toString()));
		ps.setByte(6, (byte) type.ordinal());
		ps.setInt(7, slot);
		ps.setInt(8, Database.causeCache.getDataId(c, cause.getUniqueId().toString()));
		ps.setInt(9, Database.idCache.getDataId(c, item.getType().getId()));
		ps.setByte(10, (byte) item.getQuantity());
		
		ps.setNull(11, Types.VARCHAR);
		try {
			String data = DataUtils.dataToString(item.toContainer());
			if (data != null)
				ps.setString(11, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ps.setLong(12, timestamp);
		return ps;
	}

}
