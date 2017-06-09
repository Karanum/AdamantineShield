package com.karanumcoding.adamantineshield.commands;

import java.util.Date;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import com.karanumcoding.adamantineshield.AdamantineShield;

public class CommandPurge implements CommandExecutor {

	private AdamantineShield plugin;
	
	public CommandPurge(AdamantineShield plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		long time = (long) args.getOne("time").get();
		long before = new Date().getTime() - time;
		
		if (src instanceof Player) {
			Text text = Text.of(TextColors.BLUE, "[AS] ", TextColors.RED, 
					"This operation will remove log entries from the database! Are you sure? ",
					Text.builder("<Yes, proceed!>").color(TextColors.GOLD)
							.onClick(TextActions.executeCallback(cbs -> {
								doPurge(cbs, before);
							}
					)));
			src.sendMessage(text);
		} else {
			doPurge(src, before);
		}
		return CommandResult.success();
	}
	
	public void doPurge(CommandSource src, long before) {
		if (plugin.getDatabase().purgeEntries(before)) {
			src.sendMessage(Text.of(TextColors.BLUE, "[AS] ", TextColors.YELLOW, "Purge successful"));
		} else {
			src.sendMessage(Text.of(TextColors.BLUE, "[AS] ", TextColors.RED, "Purge failed! Check console for more information!"));
		}
	}
	
	public static Text getHelpEntry() {
		return Text.of(TextColors.YELLOW, "/ashield purge <time>", TextColors.AQUA, " - Purges old log entries");
	}

}
