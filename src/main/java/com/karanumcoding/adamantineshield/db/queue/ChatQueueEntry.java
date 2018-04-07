package com.karanumcoding.adamantineshield.db.queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.enums.ChatType;

public class ChatQueueEntry extends QueueEntry {

	private static final String QUERY = "INSERT INTO AS_Chat (x, y, z, world, type, cause, message, time) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
	
	Player p;
	ChatType type;
	String message;
	long timestamp;
	
	public ChatQueueEntry(Player p, ChatType type, String message, long timestamp) {
		this.p = p;
		this.type = type;
		this.message = message;
		this.timestamp = timestamp;
	}
	
	@Override
	public void writeToConnection(Connection c) throws SQLException {
		Location<World> loc = p.getLocation();
		
		PreparedStatement ps = c.prepareStatement(QUERY);
		ps.setInt(1, loc.getBlockX());
		ps.setInt(2, loc.getBlockY());
		ps.setInt(3, loc.getBlockZ());
		ps.setInt(4, Database.worldCache.getDataId(c, loc.getExtent().getUniqueId().toString()));
		ps.setInt(5, (byte) type.ordinal());
		ps.setInt(6, Database.causeCache.getDataId(c, p.getUniqueId().toString()));
		ps.setString(7, message);
		ps.setLong(8, timestamp);
		
		ps.executeUpdate();
	}

}
