package com.karanumcoding.adamantineshield.lookup.chat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.flowpowered.math.vector.Vector3i;
import com.karanumcoding.adamantineshield.enums.ChatType;
import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.FilterSet;
import com.karanumcoding.adamantineshield.lookup.LookupResult;

public class ChatLookupResult extends LookupResult {
	
	public ChatLookupResult(ResultSet results) throws SQLException {
		super(results);
	}

	protected void readResult(ResultSet results) throws SQLException {
		while (results.next()) {
			Vector3i pos = new Vector3i(results.getInt("x"), results.getInt("y"), results.getInt("z"));
			UUID world = UUID.fromString(results.getString("world"));
			ChatType type = ChatType.valueCache[results.getByte("type")];
			String cause = results.getString("cause");
			String text = results.getString("message");
			long timestamp = results.getLong("time");
			lines.add(new ChatLookupLine(pos, world, type, cause, text, timestamp));
		}
	}

	public void filterResult(FilterSet filter) {
		lines = filter.apply(lines);
	}

	public LookupType getLookupType() {
		return LookupType.CHAT_LOOKUP;
	}

}
