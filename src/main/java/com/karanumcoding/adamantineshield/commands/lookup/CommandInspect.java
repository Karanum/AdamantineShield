package com.karanumcoding.adamantineshield.commands.lookup;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.karanumcoding.adamantineshield.AdamantineShield;

public class CommandInspect implements CommandExecutor {
	
	private AdamantineShield plugin;
	
	public CommandInspect(AdamantineShield plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {
			src.sendMessage(Text.of(TextColors.RED, "The inspector can only be toggled by players"));
			return CommandResult.empty();
		}
		
		Player p = (Player) src;
		if (plugin.getInspectManager().toggleInspector(p)) {
			p.sendMessage(Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.YELLOW, "Inspector mode has been ", TextColors.GREEN, "enabled"));
		} else {
			p.sendMessage(Text.of(TextColors.DARK_AQUA, "[AS] ", TextColors.YELLOW, "Inspector mode has been ", TextColors.RED, "disabled"));
		}
		
		return CommandResult.success();
	}

}
