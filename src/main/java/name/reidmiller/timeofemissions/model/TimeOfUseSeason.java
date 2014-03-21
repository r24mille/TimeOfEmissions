package name.reidmiller.timeofemissions.model;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public enum TimeOfUseSeason {
	SUMMER, WINTER;
	
	public static TimeOfUseSeason valueOfDateTime(DateTime dateTime) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
		DateTime winterTouStart = dateTimeFormatter.parseDateTime(dateTime.getYear() + "-11-01 00:00:00.000");
		DateTime winterTouEnd = dateTimeFormatter.parseDateTime(dateTime.getYear() + "-04-30 23:59:59.999");
		
		// Check time-of-use season
		if (dateTime.isAfter(winterTouEnd) && dateTime.isBefore(winterTouStart)) {
			return SUMMER;
		} else {
			return WINTER;
		}
	}
}
