package com.karanumcoding.adamantineshield.commands.arguments;

import java.util.Collections;
import java.util.List;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.karanumcoding.adamantineshield.util.TimeUtils;

public class TimeStringArgument extends CommandElement {
	
	public TimeStringArgument(Text key) {
		super(key);
	}
	
	@Override
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		CommandArgs oldState = args;
		String input = args.next();
		try {
			return TimeUtils.timeStringToLong(input);
		} catch (NumberFormatException e) {
			throw oldState.createError(Text.of(TextColors.RED, "Invalid time format: " + input));
		}
	}

	@Override
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		return Collections.emptyList();
	}

}
