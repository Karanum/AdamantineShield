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

	private static final String QUERY = "INSERT INTO AS_Container (x, y, z, world, type, slot, cause, id, count, data, time) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	
	private BlockCarrier carrier;
	private int slot;
	private ItemStackSnapshot item;
	private ActionType type;
	private String cause;
	private long timestamp;
	
	public InventoryQueueEntry(BlockCarrier carrier, int slot, ItemStackSnapshot item, ActionType type, Player cause, long timestamp) {
		this.carrier = carrier;
		this.slot = slot;
		this.item = item;
		this.type = type;
		this.cause = cause.getUniqueId().toString();
		this.timestamp = timestamp;
	}
	
	public InventoryQueueEntry(BlockCarrier carrier, int slot, ItemStackSnapshot item, ActionType type, String cause, long timestamp) {
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
		ps = prepareSingleBlockQuery(c);
		ps.executeUpdate();
	}
	
	private PreparedStatement prepareSingleBlockQuery(Connection c) throws SQLException {
		Location<World> loc = carrier.getLocation();
		
		PreparedStatement ps = c.prepareStatement(QUERY);
		ps.setInt(1, loc.getBlockX());
		ps.setInt(2, loc.getBlockY());
		ps.setInt(3, loc.getBlockZ());
		ps.setInt(4, Database.worldCache.getDataId(c, carrier.getLocation().getExtent().getUniqueId().toString()));
		ps.setByte(5, (byte) type.ordinal());
		ps.setInt(6, slot);
		ps.setInt(7, Database.causeCache.getDataId(c, cause));
		ps.setInt(8, Database.idCache.getDataId(c, item.getType().getId()));
		ps.setByte(9, (byte) item.getQuantity());
		
		ps.setNull(10, Types.VARCHAR);
		try {
			String data = DataUtils.dataToString(item.toContainer());
			if (data != null)
				ps.setString(10, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ps.setLong(11, timestamp);
		return ps;
	}

}
