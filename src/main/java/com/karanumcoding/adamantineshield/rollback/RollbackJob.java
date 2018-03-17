package com.karanumcoding.adamantineshield.rollback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.collect.Lists;
import com.karanumcoding.adamantineshield.AdamantineShield;
import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.db.QueryHelper;
import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.BlockLookupResult;
import com.karanumcoding.adamantineshield.lookup.ContainerLookupResult;
import com.karanumcoding.adamantineshield.lookup.FilterSet;
import com.karanumcoding.adamantineshield.lookup.LookupLine;
import com.karanumcoding.adamantineshield.lookup.LookupResult;
import com.karanumcoding.adamantineshield.lookup.filters.RolledBackFilter;

public class RollbackJob {

	private List<LookupLine> lines;
	private Iterator<LookupLine> iter;
	
	private AdamantineShield plugin;
	private FilterSet filter;
	private Player player;
	private boolean isUndo;
	
	public RollbackJob(AdamantineShield plugin, Player player, FilterSet filter, boolean isUndo) {
		this.lines = Lists.newArrayList();
		this.iter = null;
		this.isUndo = isUndo;
		this.plugin = plugin;
		
		this.player = player;
		this.filter = filter;
		this.filter.addFilter(new RolledBackFilter(isUndo));
		
		player.sendMessage(Text.of(TextColors.BLUE, "Queueing rollback operation..."));
		Runnable task = () -> {
			LookupResult lookup;
			Connection c = plugin.getDatabase().getConnection();
			try {
				int worldId = Database.worldCache.getDataId(c, player.getWorld().getUniqueId().toString());
				ResultSet r = c.createStatement().executeQuery(QueryHelper.getLookupQuery(filter, player, worldId));
				
				if (filter.getLookupType() == LookupType.ITEM_LOOKUP)
					lookup = new ContainerLookupResult(r);
				else
					lookup = new BlockLookupResult(r);
				
				r.close();
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				player.sendMessage(Text.of(TextColors.DARK_AQUA, "[AC] ", TextColors.RED, "A database error has occurred! Contact your server administrator!"));
				return;
			}
			
			lines = lookup.getLines();
			iter = lines.listIterator();
			plugin.getRollbackManager().queue(this);
		};
		plugin.getThreadPool().execute(task);
	}
	
	public LookupLine getNext() {
		if (iter == null || !iter.hasNext()) 
			return null;
		return iter.next();
	}
	
	public boolean isUndo() {
		return isUndo;
	}
	
	public void commitToDatabase() {
		player.sendMessage(Text.of(TextColors.DARK_AQUA, "[AC] ", TextColors.YELLOW, "Successfully rolled back " + lines.size() + " entries"));
		Runnable task = () -> {
			Connection c = plugin.getDatabase().getConnection();
			try {
				int worldId = Database.worldCache.getDataId(c, player.getWorld().getUniqueId().toString());
				c.createStatement().executeUpdate(QueryHelper.getRollbackUpdateQuery(filter, player, worldId, isUndo));
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				player.sendMessage(Text.of(TextColors.DARK_AQUA, "[AC] ", TextColors.RED, "A database error has occurred! Contact your server administrator!"));
			}
		};
	}
	
}
