package com.karanumcoding.adamantineshield.commands.pages;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.karanumcoding.adamantineshield.lookup.LookupResult;
import com.karanumcoding.adamantineshield.lookup.LookupResultManager;

public class CommandPrevPage implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.RED, "Only players can use this command!"));
			return CommandResult.empty();
		}
		
		LookupResult result = LookupResultManager.instance().getLookupResult((Player) src);
		if (result == null) {
			src.sendMessage(Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.YELLOW, "You have no lookup history!"));
			return CommandResult.empty();
		}
		
		int page = result.getLastSeenPage() - 1;
		if (page <= 0) {
			src.sendMessage(Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.YELLOW, "Already on the first page!"));
			return CommandResult.empty();
		}
		
		result.showPage((Player) src, page);
		return CommandResult.success();
	}

}
