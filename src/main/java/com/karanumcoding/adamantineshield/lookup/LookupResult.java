package com.karanumcoding.adamantineshield.lookup;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.collect.Lists;
import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.util.TimeUtils;

public abstract class LookupResult {

	protected List<LookupLine> lines;
	protected int lastPage;
	
	public LookupResult(ResultSet results) throws SQLException {
		lines = Lists.newArrayList();
		readResult(results);
		lastPage = 0;
	}
	
	protected abstract void readResult(ResultSet results) throws SQLException;
	public abstract void filterResult(FilterSet filter);
	public abstract LookupType getLookupType();
	
	public int getPages(int linesPerPage) {
		return (lines.size() / linesPerPage) + 1;
	}
	
	public int getLastSeenPage() {
		return lastPage;
	}
	
	public void showPage(Player p, int page) {
		int linesPerPage = LookupResultManager.instance().getLinesPerPage();
		int pages = getPages(linesPerPage);
		if (page > pages) {
			p.sendMessage(Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.RED, "Page number exceeds amount of pages (max ", pages, ")"));
			return;
		}
		
		if (lines.isEmpty()) {
			p.sendMessage(Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.GRAY, "No results at this location..."));
			return;
		}
		
		lastPage = page;
		
		Text text = Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.YELLOW, "Showing results, page ", page, "/", pages);
		for (int i = (page - 1) * linesPerPage; i < page * linesPerPage && i < lines.size(); ++i) {
			LookupLine line = lines.get(i);
			Text hover = Text.builder(line.toString())
					.color(TextColors.GOLD)
					.onHover(TextActions.showText(Text.of("Location: ", line.getPos().toString())))
					.build();
			text = Text.of(text, Text.NEW_LINE, TimeUtils.timeAgoToString(line.getTime()),
					TextColors.DARK_AQUA, " - ", hover);
		}
		p.sendMessage(text);
	}
	
}
