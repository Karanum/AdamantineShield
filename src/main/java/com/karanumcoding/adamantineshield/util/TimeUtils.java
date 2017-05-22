package com.karanumcoding.adamantineshield.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class TimeUtils {

	private TimeUtils() {};
	
	private static final long DAY = TimeUnit.DAYS.toMillis(1);
	private static final long HOUR = TimeUnit.HOURS.toMillis(1);
	private static final long MINUTE = TimeUnit.MINUTES.toMillis(1);
	
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
	
}
