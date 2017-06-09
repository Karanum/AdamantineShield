package com.karanumcoding.adamantineshield.commands.rollback;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CommandRollback implements CommandExecutor {

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		// TODO Auto-generated method stub
		return CommandResult.empty();
	}
	
	public static Text getHelpEntry() {
		return Text.of(TextColors.AQUA, "/ashield rollback [filters]", TextColors.WHITE, " - Performs a rollback");
	}

}
