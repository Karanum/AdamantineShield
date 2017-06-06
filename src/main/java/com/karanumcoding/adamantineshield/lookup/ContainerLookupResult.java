package com.karanumcoding.adamantineshield.lookup;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

import com.flowpowered.math.vector.Vector3i;
import com.karanumcoding.adamantineshield.enums.ActionType;

public class ContainerLookupResult extends LookupResult {

	public ContainerLookupResult(ResultSet results) throws SQLException {
		super(results);
	}
	
	@Override
	protected void readResult(ResultSet results) throws SQLException {
		while (results.next()) {
			Vector3i pos = new Vector3i(results.getInt("x"), results.getInt("y"), results.getInt("z"));
			UUID world = UUID.fromString(results.getString("world"));
			ActionType type = ActionType.valueCache[results.getByte("type")];
			String cause = results.getString("cause");
			ItemType item = Sponge.getRegistry().getType(ItemType.class, results.getString("item")).get();
			int count = results.getByte("count");
			long timestamp = results.getLong("time");
			lines.add(new LookupLine(pos, world, type, cause, count + "x " + item.getId(), timestamp));
		}
	}

}
