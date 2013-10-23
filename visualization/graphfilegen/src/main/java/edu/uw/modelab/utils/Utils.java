package edu.uw.modelab.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.TripInstance;

public class Utils {

	private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

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

	public static long diff(final String from, final long toTimestamp) {
		final SimpleDateFormat format = new SimpleDateFormat(
				"MM/dd/yyyy HH:mm:ss");
		Date d1 = null;
		Date d2 = null;
		long diff = 0;
		try {
			d1 = format.parse(DATE_START + from);
			d2 = format.parse(DATE_END + toHHMMssPST(toTimestamp));
			diff = ((d2.getTime() - d1.getTime()) / 1000);
		} catch (final Exception e) {
			LOG.error("Exception calculating diff between {} and {}", from,
					toTimestamp);
		}
		return diff;

	}

	public static long diff(final String to, final String from) {
		final SimpleDateFormat format = new SimpleDateFormat(
				"MM/dd/yyyy HH:mm:ss");
		Date d1 = null;
		Date d2 = null;
		long diff = 0;
		try {
			d1 = format.parse(DATE_START + from);
			d2 = format.parse(DATE_END + to);
			diff = ((d2.getTime() - d1.getTime()) / 1000);
		} catch (final Exception e) {
			LOG.error("Exception calculating diff between {} and {}", to, from);
		}
		return diff < 0 ? -diff : diff;

	}

	public static double euclideanDistance(final double x, final double toX,
			final double y, final double toY) {
		return Math.sqrt(Math.pow(x - toX, 2) + Math.pow(y - toY, 2));
	}

	public static String label(final TripInstance tripInstance,
			final Segment segment) {
		return tripInstance.getTripId() + "_" + tripInstance.getServiceDate()
				+ "_" + segment.name();
	}

	public static void main(final String[] args) {
		// System.out.println(Utils.dayOfWeek(1372662000000L));
		// System.out.println(Utils.dayOfWeek(1379574000000L));
		// System.out.println(Utils.diff("10:51:00", "10:50:00"));
		// System.out.println(Utils.time(1372662000000L, "10:50:00"));
		// System.out.println(Utils.monthOfYear(1380265200000L));
		// System.out.println(Utils.diff("23:00:00", "25:00:00"));
		System.out.println(Utils.timefDay("00:00:00")); // 0
		System.out.println(Utils.timefDay("00:01:00")); // 0
		System.out.println(Utils.timefDay("01:00:00")); // 0
		System.out.println(Utils.timefDay("06:00:00")); // 0
		System.out.println(Utils.timefDay("06:10:00")); // 1
		System.out.println(Utils.timefDay("07:00:00")); // 1
		System.out.println(Utils.timefDay("12:00:00")); // 1
		System.out.println(Utils.timefDay("12:01:00")); // 2
		System.out.println(Utils.timefDay("15:00:00")); // 2
		System.out.println(Utils.timefDay("18:00:00")); // 2
		System.out.println(Utils.timefDay("18:01:00")); // 3
		System.out.println(Utils.timefDay("20:01:00")); // 3
		System.out.println(Utils.timefDay("23:59:00")); // 3
		System.out.println(Utils.timefDay("24:00:00")); // 0
		System.out.println(Utils.timefDay("24:30:00")); // 0
		System.out.println(Utils.timefDay("25:40:00")); // 0

		// System.out.println(Utils.toHHMMssUTC(1372701294000L));
		// System.out.println(Utils.toHHMMssPST(1372701294000L));
		// System.out.println(Utils.toHHMMssPST(1372701385000L));
		// System.out.println(Utils.toHHMMssPST(1372701653000L));
	}

	public static long time(final long serviceDate, final String actual) {
		DateTimeFormatter fmt = DateTimeFormat
				.forPattern("MM/dd/yyyy Z '(PST)'");
		final String date = fmt.print(serviceDate).split(" ")[0];
		final String fullDate = date + " " + actual;
		fmt = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
		final DateTime dateTime = fmt.parseDateTime(fullDate);
		return dateTime.getMillis();
	}

	/**
	 * 0 -> (OO:00 - 06:00) 1 -> (06:01 - 12:00) 2 -> (12:01 - 18:00) 3 ->
	 * (18:01 - 23:59) 0 -> (24:00 - 30:00)
	 * 
	 * @param schedArrivalTime
	 * @return
	 */
	public static int timefDay(final String schedArrivalTime) {
		final String[] tokens = schedArrivalTime.split(":");
		final int hours = Integer.valueOf(tokens[0]);
		final int minutes = Integer.valueOf(tokens[1]);
		if ((hours == 0) && (minutes == 0)) {
			return 0;
		} else if ((hours == 0) && (minutes > 0)) {
			return 0;
		} else if ((hours > 0) && (hours < 6)) {
			return 0;
		} else if ((hours == 6) && (minutes == 0)) {
			return 0;
		} else if ((hours == 6) && (minutes > 0)) {
			return 1;
		} else if ((hours > 6) && (hours < 12)) {
			return 1;
		} else if ((hours == 12) && (minutes == 0)) {
			return 1;
		} else if ((hours == 12) && (minutes > 0)) {
			return 2;
		} else if ((hours > 12) && (hours < 18)) {
			return 2;
		} else if ((hours == 18) && (minutes == 0)) {
			return 2;
		} else if ((hours == 18) && (minutes > 0)) {
			return 3;
		} else if ((hours > 18) && (hours < 24)) {
			return 3;
		} else if ((hours == 24) && (minutes == 0)) {
			return 0;
		} else if ((hours == 24) && (minutes > 0)) {
			return 0;
		} else if ((hours >= 25) && (hours <= 30)) {
			return 0;
		}
		throw new RuntimeException("Unable to determine time of day");
	}
}
