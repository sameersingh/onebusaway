package edu.uw.modelab.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	// interval index - end time of the interval
	private static Map<Integer, String> endTimesPerInterval;

	public static final double REFERENCE_LAT = 47.609982;
	public static final double REFERENCE_LON = -122.335365;

	static {
		endTimesPerInterval = new HashMap<Integer, String>();
		endTimesPerInterval.put(0, "1:59:59");
		endTimesPerInterval.put(1, "3:59:59");
		endTimesPerInterval.put(2, "5:59:59");
		endTimesPerInterval.put(3, "7:59:59");
		endTimesPerInterval.put(4, "9:59:59");
		endTimesPerInterval.put(5, "11:59:59");
		endTimesPerInterval.put(6, "13:59:59");
		endTimesPerInterval.put(7, "15:59:59");
		endTimesPerInterval.put(8, "17:59:59");
		endTimesPerInterval.put(9, "19:59:59");
		endTimesPerInterval.put(10, "21:59:59");
		endTimesPerInterval.put(11, "23:59:59");
	}

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

	// monday = 1, sunday = 7
	public static int dayOfWeek(final long timestamp) {
		final DateTime dt = new DateTime(timestamp,
				DateTimeZone.forID("America/Los_Angeles"));
		return dt.getDayOfWeek();
	}

	public static int dayOfMonth(final long timestamp) {
		final DateTime dt = new DateTime(timestamp,
				DateTimeZone.forID("America/Los_Angeles"));
		return dt.getDayOfMonth();
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
			final long diffInMillis = d2.getTime() - d1.getTime();
			if (diffInMillis != 0) {
				diff = diffInMillis / 1000;
			}
		} catch (final Exception e) {
			LOG.error("Exception calculating diff between {} and {}", from,
					toTimestamp);
		}
		return diff < 0 ? -diff : diff;
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
			final long diffInMillis = d2.getTime() - d1.getTime();
			if (diffInMillis != 0) {
				diff = diffInMillis / 1000;
			}
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
				+ "_" + segment.getId();
	}

	public static double getDistance(double lat1, final double lon1,
			double lat2, final double lon2) {
		final double R = 6371; // km
		final double dLat = ((lat2 - lat1) * Math.PI) / 180;
		final double dLon = ((lon2 - lon1) * Math.PI) / 180;
		lat1 = (lat1 * Math.PI) / 180;
		lat2 = (lat2 * Math.PI) / 180;
		final double a = (Math.sin(dLat / 2) * Math.sin(dLat / 2))
				+ (Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math
						.cos(lat2));
		final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		final double d = R * c;
		return d;
	}

	public static void main(final String[] args) {
		System.out.println(Utils.dayOfMonth(1377586800000L));
		System.out.println(Utils.dayOfMonth(1377846000000L));
		System.out.println(Utils.dayOfMonth(1377932400000L));

		// System.out.println(Utils.dayOfWeek(1379574000000L));
		// System.out.println(Utils.diff("10:51:00", "10:50:00"));
		// System.out.println(Utils.time(1372662000000L, "10:50:00"));
		// System.out.println(Utils.monthOfYear(1380265200000L));
		// System.out.println(Utils.diff("23:00:00", "25:00:00"));
		// System.out.println(Utils.timefDay("00:00:00")); // 0
		// System.out.println(Utils.timefDay("00:01:00")); // 0
		// System.out.println(Utils.timefDay("01:00:00")); // 0
		// System.out.println(Utils.timefDay("06:00:00")); // 0
		// System.out.println(Utils.timefDay("06:10:00")); // 1
		// System.out.println(Utils.timefDay("07:00:00")); // 1
		// System.out.println(Utils.timefDay("12:00:00")); // 1
		// System.out.println(Utils.timefDay("12:01:00")); // 2
		// System.out.println(Utils.timefDay("15:00:00")); // 2
		// System.out.println(Utils.timefDay("18:00:00")); // 2
		// System.out.println(Utils.timefDay("18:01:00")); // 3
		// System.out.println(Utils.timefDay("20:01:00")); // 3
		// System.out.println(Utils.timefDay("23:59:00")); // 3
		// System.out.println(Utils.timefDay("24:00:00")); // 0
		// System.out.println(Utils.timefDay("24:30:00")); // 0
		// System.out.println(Utils.timefDay("25:40:00")); // 0

		// System.out.println(Utils.toHHMMssUTC(1372701294000L));
		// System.out.println(Utils.toHHMMssPST(1372701294000L));
		// System.out.println(Utils.toHHMMssPST(1372701385000L));
		// System.out.println(Utils.toHHMMssPST(1372701653000L));

		// System.out.println(Utils.getTimeOfDayVector("01:50:00", "2:10:00"));
		// System.out.println(Utils.getTimeOfDayVector("01:55:00", "2:10:00"));
		// System.out.println(Utils.getTimeOfDayVector("01:55:00", "2:15:00"));
		// System.out.println(Utils.getTimeOfDayVector("07:55:00", "8:15:00"));
		// System.out.println(Utils.getTimeOfDayVector("23:50:00", "24:10:00"));

		// System.out.println(Utils.year(1380265200000L));

		System.out.println(Utils.getDistance(47.604253, -122.329913, 47.603253,
				-122.328913));

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

	public static int getTimeOfDay(final String time) {
		final String[] tokens = time.split(":");
		final int hours = Integer.valueOf(tokens[0]);
		if ((hours >= 0) && (hours < 2)) {
			return 0;
		} else if ((hours >= 2) && (hours < 4)) {
			return 1;
		} else if ((hours >= 4) && (hours < 6)) {
			return 2;
		} else if ((hours >= 6) && (hours < 8)) {
			return 3;
		} else if ((hours >= 8) && (hours < 10)) {
			return 4;
		} else if ((hours >= 10) && (hours < 12)) {
			return 5;
		} else if ((hours >= 12) && (hours < 14)) {
			return 6;
		} else if ((hours >= 14) && (hours < 16)) {
			return 7;
		} else if ((hours >= 16) && (hours < 18)) {
			return 8;
		} else if ((hours >= 18) && (hours < 20)) {
			return 9;
		} else if ((hours >= 20) && (hours < 22)) {
			return 10;
		} else if ((hours >= 22) && (hours < 24)) {
			return 11;
		} else if ((hours >= 24) && (hours < 26)) {
			return 0;
		} else if ((hours >= 26) && (hours < 28)) {
			return 1;
		} else if ((hours >= 28) && (hours < 30)) {
			return 2;
		}
		throw new RuntimeException("Unable to determine time of day, hours"
				+ hours);
	}

	public static String getTimeOfDayVector(final String from, final String to) {
		final int fromInterval = Utils.getTimeOfDay(from);
		final int toInterval = Utils.getTimeOfDay(to);
		final double[] timeOfDays = new double[12];
		if (fromInterval == toInterval) {
			timeOfDays[fromInterval] = 1;
		} else {
			final String endOfFromInterval = endTimesPerInterval
					.get(fromInterval);
			final long range = Utils.diff(to, from);
			final long diff = Utils.diff(endOfFromInterval, from);
			final double alpha = (double) diff / (double) range;
			timeOfDays[fromInterval] = alpha;
			timeOfDays[toInterval] = (1 - alpha);
		}
		final StringBuilder sb = new StringBuilder();
		final DecimalFormat decimalFormat = new DecimalFormat("###.##");
		for (final double i : timeOfDays) {
			sb.append(decimalFormat.format(i) + "\t");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static int year(final long timestamp) {
		final DateTime dt = new DateTime(timestamp,
				DateTimeZone.forID("America/Los_Angeles"));
		return dt.getYear();
	}
}
