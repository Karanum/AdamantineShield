package com.karanumcoding.adamantineshield.lookup;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.karanumcoding.adamantineshield.ActionType;
import com.karanumcoding.adamantineshield.util.TimeUtils;

public class LookupResult {

	private List<LookupLine> lines;
	private int lastPage;
	
	public LookupResult(ResultSet results) throws SQLException {
		lines = Lists.newArrayList();
		while (results.next()) {
			Vector3i pos = new Vector3i(results.getInt("x"), results.getInt("y"), results.getInt("z"));
			UUID world = UUID.fromString(results.getString("world"));
			ActionType type = ActionType.fromString(results.getString("type"));
			String cause = results.getString("cause");
			BlockType block = Sponge.getRegistry().getType(BlockType.class, results.getString("block")).get();
			long timestamp = results.getLong("time");
			lines.add(new LookupLine(pos, world, type, cause, block, timestamp));
		}
		lastPage = 0;
	}
	
	public int getPages() {
		return (lines.size() / 15) + 1;
	}
	
	public int getLastSeenPage() {
		return lastPage;
	}
	
	public void showPage(Player p, int page) {
		if (page > getPages()) {
			p.sendMessage(Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.RED, "Page number exceeds amount of pages (max ", getPages(), ")"));
			return;
		}
		
		if (lines.isEmpty()) {
			p.sendMessage(Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.GRAY, "No results at this location..."));
			return;
		}
		
		Text text = Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.YELLOW, "Showing results, page ", page, "/", getPages());
		for (int i = (page - 1) * 15; i < page * 15 && i < lines.size(); ++i) {
			LookupLine line = lines.get(i);
			Text hover = Text.builder(line.toString())
					.color(TextColors.GOLD)
					.onHover(TextActions.showText(Text.of("Location: ", line.getPos().toString())))
					.build();
			text = Text.of(text, Text.NEW_LINE, TextColors.AQUA, TimeUtils.timeAgoToString(line.getTime()),
					TextColors.DARK_AQUA, " - ", hover);
		}
		p.sendMessage(text);
	}
	
}