package com.karanumcoding.adamantineshield.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtils {

	private TimeUtils() {};
	
	private static final long DAY = TimeUnit.DAYS.toMillis(1);
	private static final long HOUR = TimeUnit.HOURS.toMillis(1);
	private static final long MINUTE = TimeUnit.MINUTES.toMillis(1);
	private static final long SECOND = TimeUnit.SECONDS.toMillis(1);
	
	private static final Pattern timeRegex = Pattern.compile("((?<days>\\d+)d)?((?<hours>\\d+)h)?((?<mins>\\d+)m)?((?<secs>\\d+)s)?");
	
	public static String timeAgoToString(long time) {
		long current = new Date().getTime();
		long diff = current - time;
		
		if (diff < 0)
			return "Near future";
		
		if (diff >= DAY) {
			float days = diff / (float) DAY;
			return String.format("%.2fd ago", days);
		}
		if (diff >= HOUR) {
			float hours = diff / (float) HOUR;
			return String.format("%.2fh ago", hours);
		}
		float mins = diff / (float) MINUTE;
		return String.format("%.2fm ago", mins);
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
