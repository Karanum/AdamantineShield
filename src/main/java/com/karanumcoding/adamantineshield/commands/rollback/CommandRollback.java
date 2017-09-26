package com.karanumcoding.adamantineshield.commands.rollback;

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
import com.karanumcoding.adamantineshield.rollback.RollbackJob;
import com.karanumcoding.adamantineshield.util.FilterParser;

public class CommandRollback implements CommandExecutor {

	private AdamantineShield plugin;
	
	public CommandRollback(AdamantineShield plugin) {
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
		
		new RollbackJob(plugin, p, filterSet, false);
		return CommandResult.success();
	}
	
	public static Text getHelpEntry() {
		return Text.of(TextColors.AQUA, "/ashield rollback [filters]", TextColors.WHITE, " - Performs a rollback");
	}

}
