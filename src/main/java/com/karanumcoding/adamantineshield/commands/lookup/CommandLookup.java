package com.karanumcoding.adamantineshield.commands.lookup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.karanumcoding.adamantineshield.AdamantineShield;
import com.karanumcoding.adamantineshield.db.Database;
import com.karanumcoding.adamantineshield.enums.LookupType;
import com.karanumcoding.adamantineshield.lookup.BlockLookupResult;
import com.karanumcoding.adamantineshield.lookup.ContainerLookupResult;
import com.karanumcoding.adamantineshield.lookup.FilterSet;
import com.karanumcoding.adamantineshield.lookup.LookupResult;
import com.karanumcoding.adamantineshield.lookup.LookupResultManager;
import com.karanumcoding.adamantineshield.util.FilterParser;

public class CommandLookup implements CommandExecutor {

	private AdamantineShield plugin;
	
	public CommandLookup(AdamantineShield plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.RED, "Lookups can only be performed by players"));
			return CommandResult.empty();
		}
		
		Player p = (Player) src;
		Collection<String> filters = args.getAll("filter");
		
		FilterSet filterSet = new FilterSet(plugin, p, true);
		FilterParser.parse(filters, filterSet, p);
		String targetTable = filterSet.getLookupType().getTable();
		
		p.sendMessage(Text.of(TextColors.BLUE, "Querying database, please wait..."));
		Sponge.getScheduler().createAsyncExecutor(plugin).execute(() -> {
			//String relevantColumns = filterSet.getLookupType().getRelevantColumn();
			String relevantColumns = "AS_Id.value";
			if (filterSet.getLookupType() == LookupType.ITEM_LOOKUP) {
				relevantColumns += ", count";
			}
			
			LookupResult lookup;
			Connection c = plugin.getDatabase().getConnection();
			try {
				int worldId = Database.worldCache.getDataId(c, p.getWorld().getUniqueId().toString());
				ResultSet r = c.createStatement().executeQuery("SELECT x, y, z, type, time, " + relevantColumns + ", AS_Cause.cause, AS_World.world "
						+ "FROM " + targetTable + ", AS_Cause, AS_World, AS_Id "
						+ "WHERE AS_Cause.id = " + targetTable + ".cause AND " + targetTable + ".world = " + worldId + " "
						+ "AND " + filterSet.getQueryConditions(p) + " ORDER BY time DESC;");
				
				if (filterSet.getLookupType() == LookupType.ITEM_LOOKUP)
					lookup = new ContainerLookupResult(r);
				else
					lookup = new BlockLookupResult(r);
				LookupResultManager.instance().setLookupResult(p, lookup);
				
				r.close();
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
				p.sendMessage(Text.of(TextColors.DARK_AQUA, "[AC] ", TextColors.RED, "A database error has occurred! Contact your server administrator!"));
				return;
			}
			
			lookup.showPage(p, 1);
		});
		return CommandResult.success();
	}
	
	public static Text getHelpEntry() {
		return Text.of(TextColors.AQUA, "/ashield lookup [filters]", TextColors.WHITE, " - Performs a lookup");
	}

}
