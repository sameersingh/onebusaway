package edu.uw.modelab.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class Utils {

	// ugly stuff, asuming that the start and end day of the trip are the same
	private static final String DATE_START = "02/08/2012 ";
	private static final String DATE_END = "02/08/2012 ";

	private Utils() {
	}

	public static String unquote(final String str) {
		return str.replace("\"", "");
	}

	public static String toHHMMssUTC(final long timestamp) {
		String result = "";
		if (timestamp != 0) {
			final DateTime dt = new DateTime(timestamp, DateTimeZone.UTC);
			final DateTimeFormatter fmt = ISODateTimeFormat
					.dateHourMinuteSecond();
			final String date = fmt.print(dt);
			result = date.split("T")[1];
		}
		return result;
	}

	public static String toHHMMssPST(final long timestamp) {
		String result = "";
		if (timestamp != 0) {
			final DateTimeFormatter fmt = DateTimeFormat
					.forPattern("HH:mm:ss Z '(PST)'");
			final String date = fmt.print(timestamp);
			result = date.split(" ")[0];
		}
		return result;
	}

	public static int dayOfWeek(final long timestamp) {
		final DateTime dt = new DateTime(timestamp,
				DateTimeZone.forID("America/Los_Angeles"));
		return dt.getDayOfWeek();
	}

	public static int monthOfYear(final long timestamp) {
		final DateTime dt = new DateTime(timestamp,
				DateTimeZone.forID("America/Los_Angeles"));
		return dt.getMonthOfYear();
	}

	public static long diff(final String scheduled, final long timestamp) {
		final SimpleDateFormat format = new SimpleDateFormat(
				"MM/dd/yyyy HH:mm:ss");
		Date d1 = null;
		Date d2 = null;
		long diff = 0;
		try {
			d1 = format.parse(DATE_START + scheduled);
			d2 = format.parse(DATE_END + toHHMMssPST(timestamp));
			diff = ((d2.getTime() - d1.getTime()) / 1000);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return diff;

	}

	public static double euclideanDistance(final double x, final double toX,
			final double y, final double toY) {
		return Math.sqrt(Math.pow(x - toX, 2) + Math.pow(y - toY, 2));
	}

	public static void main(final String[] args) {
		System.out.println(Utils.dayOfWeek(1372662000000L));
		System.out.println(Utils.dayOfWeek(1379574000000L));
		// System.out.println(Utils.toHHMMssUTC(1372701294000L));
		// System.out.println(Utils.toHHMMssPST(1372701294000L));
		// System.out.println(Utils.toHHMMssPST(1372701385000L));
		// System.out.println(Utils.toHHMMssPST(1372701653000L));
	}

}
