package com.karanumcoding.adamantineshield.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.karanumcoding.adamantineshield.commands.lookup.CommandFilter;
import com.karanumcoding.adamantineshield.commands.lookup.CommandInspect;
import com.karanumcoding.adamantineshield.commands.lookup.CommandLookup;
import com.karanumcoding.adamantineshield.commands.pages.CommandNextPage;
import com.karanumcoding.adamantineshield.commands.pages.CommandPage;
import com.karanumcoding.adamantineshield.commands.pages.CommandPrevPage;
import com.karanumcoding.adamantineshield.enums.Permissions;

public class CommandMain implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		boolean hasAnyPermission = false;
		Text text = Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.YELLOW, "Available commands: ", Text.NEW_LINE);
		if (src.hasPermission(Permissions.LOOKUP.get())) {
			hasAnyPermission = true;
			text = Text.of(text, CommandInspect.getHelpEntry(), Text.NEW_LINE, CommandLookup.getHelpEntry(), Text.NEW_LINE);
		}
		if (src.hasPermission(Permissions.FILTER.get())) {
			hasAnyPermission = true;
			text = Text.of(text, CommandFilter.getHelpEntry(), Text.NEW_LINE);
		}
		if (src.hasPermission(Permissions.LOOKUP.get())) {
			text = Text.of(text, CommandPage.getHelpEntry(), Text.NEW_LINE, CommandNextPage.getHelpEntry(), Text.NEW_LINE,
					CommandPrevPage.getHelpEntry(), Text.NEW_LINE);
		}
		if (src.hasPermission(Permissions.PURGE.get())) {
			hasAnyPermission = true;
			text = Text.of(text, CommandPurge.getHelpEntry(), Text.NEW_LINE);
		}
		
		if (hasAnyPermission) {
			text = Text.of(text, getHelpEntry(), Text.NEW_LINE);
		} else {
			text = Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.YELLOW, "AdmantineShield v",
					Sponge.getPluginManager().getPlugin("adamantineshield").get().getVersion(), " by Karanum");
		}
		
		src.sendMessage(text);
		return CommandResult.success();
	}
	
	public static Text getHelpEntry() {
		return Text.of(TextColors.YELLOW, "/ashield help", TextColors.AQUA, " - Shows command help");
	}

}
