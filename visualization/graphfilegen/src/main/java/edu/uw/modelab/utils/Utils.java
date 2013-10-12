package edu.uw.modelab.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Utils {

	private Utils() {
	}

	public static String unquote(final String str) {
		return str.replace("\"", "");
	}

	public static String toDate(final long serviceDate) {
		final DateTime dt = new DateTime(serviceDate, DateTimeZone.UTC);
		final DateTimeFormatter fmt = ISODateTimeFormat.dateHourMinute();
		return fmt.print(dt);
	}

}
