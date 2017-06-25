package com.karanumcoding.adamantineshield.lookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.karanumcoding.adamantineshield.db.Database;

public class InspectManager {

	private final String INSPECT_BLOCK_QUERY = "SELECT x, y, z, type, AS_Id.value, data, time, AS_Cause.cause, AS_World.world FROM AS_Block, AS_World, AS_Cause, AS_Id "
			+ "WHERE x = ? AND y = ? AND z = ? AND AS_World.world = ? AND AS_Block.cause = AS_Cause.id AND AS_Block.world = AS_World.id AND AS_Block.id = AS_Id.id ORDER BY time DESC;";
	private final String INSPECT_CONTAINER_QUERY = "SELECT x, y, z, type, slot, AS_Id.value, count, data, time, AS_Cause.cause, AS_World.world "
			+ "FROM AS_Container, AS_World, AS_Cause, AS_Id "
			+ "WHERE x = ? AND y = ? AND z = ? AND AS_World.world = ? AND AS_Container.cause = AS_Cause.id AND AS_Container.world = AS_World.id AND AS_Container.id = AS_Id.id "
			+ "ORDER BY time DESC;";

	private List<Player> inspectors;
	private Database db;
	
	public InspectManager(Database db) {
		inspectors = Lists.newArrayList();
		this.db = db;
	}
	
	public boolean toggleInspector(Player p) {
		if (inspectors.contains(p)) {
			inspectors.remove(p);
			return false;
		}
		inspectors.add(p);
		return true;
	}
	
	public boolean isInspector(Player p) {
		return inspectors.contains(p);
	}
	
	public synchronized void inspect(Player p, UUID world, Vector3i pos) {		
		Connection c = db.getConnection();
		LookupResult lookup = null;
		
		try {			
			PreparedStatement ps = c.prepareStatement(INSPECT_BLOCK_QUERY);
			ps.setInt(1, pos.getX());
			ps.setInt(2, pos.getY());
			ps.setInt(3, pos.getZ());
			ps.setString(4, world.toString());
			ResultSet result = ps.executeQuery();
			
			lookup = new BlockLookupResult(result);
			LookupResultManager.instance().setLookupResult(p, lookup);
			
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
			p.sendMessage(Text.of(TextColors.DARK_AQUA, "[AC] ", TextColors.RED, "A database error has occurred! Contact your server administrator!"));
			return;
		}

		lookup.showPage(p, 1);
	}
	
	public synchronized void inspectContainer(Player p, UUID world, Vector3i pos) {
		Connection c = db.getConnection();
		ContainerLookupResult lookup = null;
		
		try {
			PreparedStatement ps = c.prepareStatement(INSPECT_CONTAINER_QUERY);
			ps.setInt(1, pos.getX());
			ps.setInt(2, pos.getY());
			ps.setInt(3, pos.getZ());
			ps.setString(4, world.toString());
			ResultSet result = ps.executeQuery();
			
			lookup = new ContainerLookupResult(result);
			LookupResultManager.instance().setLookupResult(p, lookup);
			
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
			p.sendMessage(Text.of(TextColors.DARK_AQUA, "[AC] ", TextColors.RED, "A database error has occurred! Contact your server administrator!"));
			return;
		}
		
		lookup.showPage(p, 1);
	}
	
}
