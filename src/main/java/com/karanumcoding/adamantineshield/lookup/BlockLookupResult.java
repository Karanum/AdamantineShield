package com.karanumcoding.adamantineshield.lookup;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;

import com.flowpowered.math.vector.Vector3i;
import com.karanumcoding.adamantineshield.enums.ActionType;
import com.karanumcoding.adamantineshield.enums.LookupType;

public class BlockLookupResult extends LookupResult {

	public BlockLookupResult(ResultSet results) throws SQLException {
		super(results);
	}
	
	protected void readResult(ResultSet results) throws SQLException {
		while (results.next()) {
			Vector3i pos = new Vector3i(results.getInt("x"), results.getInt("y"), results.getInt("z"));
			UUID world = UUID.fromString(results.getString("world"));
			ActionType type = ActionType.valueCache[results.getByte("type")];
			String cause = results.getString("cause");
			String data = results.getString("data");
			BlockType block = Sponge.getRegistry().getType(BlockType.class, results.getString("AS_Id.value")).get();
			boolean rolledBack = results.getBoolean("rolled_back");
			long timestamp = results.getLong("time");
			lines.add(new LookupLine(pos, world, type, cause, data, block, 1, 0, rolledBack, timestamp));
		}
	}
	
	public void filterResult(FilterSet filter) {
		lines = filter.apply(lines);
	}

	public LookupType getLookupType() {
		return LookupType.BLOCK_LOOKUP;
	}

}
