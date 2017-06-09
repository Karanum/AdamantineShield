package com.karanumcoding.adamantineshield.commands.lookup;

import java.util.Collection;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.karanumcoding.adamantineshield.AdamantineShield;
import com.karanumcoding.adamantineshield.lookup.FilterSet;
import com.karanumcoding.adamantineshield.lookup.LookupResult;
import com.karanumcoding.adamantineshield.lookup.LookupResultManager;
import com.karanumcoding.adamantineshield.util.FilterParser;

public class CommandFilter implements CommandExecutor {

	private AdamantineShield plugin;
	
	public CommandFilter(AdamantineShield plugin) {
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
		
		LookupResult lookup = LookupResultManager.instance().getLookupResult(p);
		if (lookup == null) {
			src.sendMessage(Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.YELLOW, "You have no lookup history!"));
			return CommandResult.empty();
		}
		
		FilterSet filterSet = new FilterSet(plugin, p, false);
		filterSet.forceLookupType(lookup.getLookupType());
		FilterParser.parse(filters, filterSet, p);
		lookup.filterResult(filterSet);
		
		lookup.showPage(p, 1);
		return CommandResult.success();
	}
	
	public static Text getHelpEntry() {
		return Text.of(TextColors.AQUA, "/ashield filter [filters]", TextColors.WHITE, " - Filters your last result");
	}

}
