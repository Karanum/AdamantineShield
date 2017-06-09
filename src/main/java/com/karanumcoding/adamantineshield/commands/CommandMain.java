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
		Text text = Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.YELLOW, "Available commands: ");
		if (src.hasPermission(Permissions.LOOKUP.get())) {
			hasAnyPermission = true;
			text = Text.of(text, Text.NEW_LINE, CommandInspect.getHelpEntry(), Text.NEW_LINE, CommandLookup.getHelpEntry());
		}
		if (src.hasPermission(Permissions.FILTER.get())) {
			hasAnyPermission = true;
			text = Text.of(text, Text.NEW_LINE, CommandFilter.getHelpEntry());
		}
		if (src.hasPermission(Permissions.LOOKUP.get())) {
			text = Text.of(text, Text.NEW_LINE, CommandPage.getHelpEntry(), Text.NEW_LINE, CommandNextPage.getHelpEntry(), Text.NEW_LINE,
					CommandPrevPage.getHelpEntry());
		}
		if (src.hasPermission(Permissions.PURGE.get())) {
			hasAnyPermission = true;
			text = Text.of(text, Text.NEW_LINE, CommandPurge.getHelpEntry());
		}
		
		if (hasAnyPermission) {
			text = Text.of(text, Text.NEW_LINE, getHelpEntry());
		} else {
			text = Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.YELLOW, "AdamantineShield v",
					Sponge.getPluginManager().getPlugin("adamantineshield").get().getVersion().get());
		}
		
		src.sendMessage(text);
		return CommandResult.success();
	}
	
	public static Text getHelpEntry() {
		return Text.of(TextColors.AQUA, "/ashield help", TextColors.WHITE, " - Shows command help");
	}

}
