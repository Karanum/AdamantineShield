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

	private final String QUERY_GET_WORLD = "SELECT id FROM AS_World WHERE world = ?";
	private final String INSPECT_QUERY = "SELECT * FROM AS_Block WHERE x = ? AND y = ? AND z = ? AND world = ? ORDER BY time DESC;";
	
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
	
	public void inspect(Player p, UUID world, Vector3i pos) {
		Connection c = db.getConnection();
		LookupResult lookup = null;
		
		try {
			if (!Database.worldCache.containsKey(world)) {
				PreparedStatement ps1 = c.prepareStatement(QUERY_GET_WORLD);
				ps1.setString(1, world.toString());
				ResultSet result1 = ps1.executeQuery();
				result1.next();
				Database.worldCache.put(world.toString(), result1.getInt("id"));
			}
			int worldId = Database.worldCache.get(world);
			
			PreparedStatement ps = c.prepareStatement(INSPECT_QUERY);
			ps.setInt(1, pos.getX());
			ps.setInt(2, pos.getY());
			ps.setInt(3, pos.getZ());
			ps.setInt(4, worldId);
			ResultSet result = ps.executeQuery();
			
			lookup = new LookupResult(result);
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
