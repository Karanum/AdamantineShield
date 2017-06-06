package com.karanumcoding.adamantineshield.util;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public final class TimeUtils {

	private TimeUtils() {};
	
	private static final long DAY = TimeUnit.DAYS.toMillis(1);
	private static final long HOUR = TimeUnit.HOURS.toMillis(1);
	private static final long MINUTE = TimeUnit.MINUTES.toMillis(1);
	private static final long SECOND = TimeUnit.SECONDS.toMillis(1);
	
	private static final Pattern timeRegex = Pattern.compile("((?<days>\\d+)d)?((?<hours>\\d+)h)?((?<mins>\\d+)m)?((?<secs>\\d+)s)?");
	
	public static Text timeAgoToString(long time) {
		long current = new Date().getTime();
		long diff = current - time;
		
		if (diff < 0)
			return Text.of("Near future");
		
		Text hoverText = Text.of(TextColors.DARK_AQUA, "Minutes: ", TextColors.AQUA, (diff % HOUR) / MINUTE);
		if (diff >= HOUR) {
			hoverText = Text.of(TextColors.DARK_AQUA, "Hours: ", TextColors.AQUA, (diff % DAY) / HOUR, Text.NEW_LINE, hoverText);
			if (diff >= DAY) {
				hoverText = Text.of(TextColors.DARK_AQUA, "Days: ", TextColors.AQUA, diff / DAY, Text.NEW_LINE, hoverText);
			}
		}
		
		float hours = diff / (float) HOUR;
		return Text.builder(String.format(Locale.ROOT, "%.2fh", hours))
				.color(TextColors.AQUA)
				.onHover(TextActions.showText(hoverText))
				.build();
	}
	
	public static long timeStringToLong(String timeStr) throws NumberFormatException {
		Matcher m = timeRegex.matcher(timeStr);
		if (!m.matches()) {
			throw new NumberFormatException();
		}
		
		String days = m.group("days");
		String hours = m.group("hours");
		String mins = m.group("mins");
		String secs = m.group("secs");
		
		long time = 0;
		if (days != null)
			time += DAY * Integer.parseInt(days);
		if (hours != null)
			time += HOUR * Integer.parseInt(hours);
		if (mins != null)
			time += MINUTE * Integer.parseInt(mins);
		if (secs != null)
			time += SECOND * Integer.parseInt(secs);
		
		return time;
	}
	
}
