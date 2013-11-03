package edu.uw.modelab.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.dao.TripDao;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.pojo.TripInstance;
import edu.uw.modelab.service.DistanceAlongTripCalculator;
import edu.uw.modelab.service.ErrorCalculator;
import edu.uw.modelab.service.TimeService;
import edu.uw.modelab.utils.Utils;

public class DefaultErrorCalculator implements ErrorCalculator {

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultErrorCalculator.class);

	private final Map<String, Double> yHatPerSegmentPerTripInstancePerTrip;
	private final Map<Integer, Map<Integer, Double>> errorObaPerKPerTripId;
	private final Map<Integer, Map<Integer, Double>> errorModePerKPerTripId;
	private final TripDao tripDao;
	private final TimeService timeEstimator;
	private final String filename;
	private final String featureYhatFileName;
	private final DistanceAlongTripCalculator distanceAlongTripCalculator;

	public DefaultErrorCalculator(final String filename, final TripDao tripDao,
			final TimeService timeEstimator, final String featureYhatFileName,
			final DistanceAlongTripCalculator distanceAlongTripCalculator) {
		this.filename = filename;
		this.tripDao = tripDao;
		this.timeEstimator = timeEstimator;
		this.distanceAlongTripCalculator = distanceAlongTripCalculator;
		this.featureYhatFileName = featureYhatFileName;
		this.yHatPerSegmentPerTripInstancePerTrip = new HashMap<String, Double>();
		this.errorObaPerKPerTripId = new LinkedHashMap<>();
		this.errorModePerKPerTripId = new LinkedHashMap<>();
	}

	public void init() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getClass()
					.getResourceAsStream(featureYhatFileName)));
			String line = null;
			while ((line = br.readLine()) != null) {
				final String[] tokens = line.split(",");
				yHatPerSegmentPerTripInstancePerTrip.put(tokens[0],
						Double.valueOf(tokens[1]));
			}
		} catch (final IOException exc) {
			LOG.error("Exception reading input file. Message {}",
					exc.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (final IOException e) {
				}
			}
		}
	}

	@Override
	@Deprecated
	public void calculateTimeBetweenStops(final int tripId) {
		final Trip trip = tripDao.getTripById(tripId);
		final Set<TripInstance> tripInstances = trip.getInstances();
		final Iterator<TripInstance> tripInstancesIt = tripInstances.iterator();
		final List<Segment> segments = new ArrayList<>(trip.getSegments());
		final StringBuilder sb = new StringBuilder();
		while (tripInstancesIt.hasNext()) {
			tripInstancesIt.next();
			for (final Segment segment : segments) {
				final String to = segment.getTo().getStopTime()
						.getSchedArrivalTime();
				final String from = segment.getFrom().getStopTime()
						.getSchedArrivalTime();
				sb.append(Utils.diff(to, from) + "\n");
			}
		}
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(filename));
			pw.print(sb.toString());
			pw.flush();
		} catch (final FileNotFoundException exc) {
			LOG.error("Error creating oba error file. Msg {}", exc.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	@Override
	public void calculateScheduledError(final int tripId,
			final edu.uw.modelab.service.Error error) {
		Trip trip = null;
		if (error == edu.uw.modelab.service.Error.TRAINING) {
			trip = tripDao.getTripByIdAndServiceDateLessThan(tripId,
					1378191600000L);
		} else if (error == edu.uw.modelab.service.Error.TEST) {
			trip = tripDao
					.getTripByIdAndServiceDateFrom(tripId, 1378191600000L);
		} else {
			trip = tripDao.getTripById(tripId);
		}
		distanceAlongTripCalculator.addDistancesAlongTrip(trip);

		final Set<TripInstance> tripInstances = trip.getInstances();
		final Iterator<TripInstance> tripInstancesIt = tripInstances.iterator();
		final List<Segment> segments = new ArrayList<>(trip.getSegments());

		final int numberOfSegments = segments.size();
		int i = 0;
		final List<Double> errorsSchedule = new ArrayList<>();
		while (tripInstancesIt.hasNext()) {
			final TripInstance tripInstance = tripInstancesIt.next();
			for (final Segment segment : segments) {
				double actual_I = 0;
				double sched_I = 0;
				if (segment.isFirst()) {
					sched_I = getScheduled(segment, tripInstance);
					actual_I = sched_I;
				} else if (i == (numberOfSegments - 1)) {
					sched_I = getScheduledLast(segment, tripInstance);
					actual_I = getActualLast(segment, tripInstance);
				} else {
					actual_I = getActual(segment, tripInstance);
					sched_I = getScheduled(segment, tripInstance);
				}
				i++;
				final double errSchedule = Math.pow(
						(actual_I - sched_I) / 1000, 2);
				errorsSchedule.add(errSchedule);
			}
		}
		double sumErrorsSchedule = 0;
		for (final Double errorSchedule : errorsSchedule) {
			sumErrorsSchedule += errorSchedule;
		}

		final double errorSchedule = Math.sqrt(sumErrorsSchedule
				/ errorsSchedule.size());
		// System.out.println("Schedule Error: " + errorSchedule);

		// Collections.sort(errorsSchedule);
		// for (final Double error : errorsSchedule) {
		// System.out.println(error);
		// }

	}

	@Override
	@Deprecated
	public void calculateObaAndModeError(final int tripId, final int k,
			final edu.uw.modelab.service.Error error) {
		Trip trip = null;
		if (error == edu.uw.modelab.service.Error.TRAINING) {
			trip = tripDao.getTripByIdAndServiceDateLessThan(tripId,
					1378191600000L);
		} else {
			trip = tripDao
					.getTripByIdAndServiceDateFrom(tripId, 1378191600000L);
		}
		distanceAlongTripCalculator.addDistancesAlongTrip(trip);
		final Set<TripInstance> tripInstances = trip.getInstances();
		final Iterator<TripInstance> tripInstancesIt = tripInstances.iterator();
		final List<Segment> segments = new ArrayList<>(trip.getSegments());
		final int numberOfSegments = segments.size();

		assert k < numberOfSegments;
		final List<Double> errorsOba = new ArrayList<>();
		final List<Double> errorsMode = new ArrayList<>();
		final List<Double> diffOba = new ArrayList<>();
		final List<Double> diffMode = new ArrayList<>();
		while (tripInstancesIt.hasNext()) {
			final TripInstance tripInstance = tripInstancesIt.next();
			int j = 0;
			int i = k;
			while (i <= numberOfSegments) {
				final long scheduledDiff = getScheduledDiff(
						segments.get(i - 1), segments.get(j));
				long actual_I = 0;
				if (i == numberOfSegments) {
					actual_I = getActualLast(segments.get(i - 1), tripInstance);
				} else {
					actual_I = getActual(segments.get(i), tripInstance);
				}

				final long t_true_I = actual_I;
				final long actual_J = getActual(segments.get(j), tripInstance);
				final long t_hat_oba_I = actual_J + (scheduledDiff * 1000);
				final String key_J = Utils.label(tripInstance, segments.get(j));
				final double y_hat_J_sec = yHatPerSegmentPerTripInstancePerTrip
						.get(key_J);
				final long y_hat_J = Math.round(y_hat_J_sec) * -1000;
				final long t_hat_mode_I = t_hat_oba_I + y_hat_J;

				final double diffObaI = (t_true_I - t_hat_oba_I) / 1000;
				final double diffModeI = (t_true_I - t_hat_mode_I) / 1000;
				final double errObaI = Math.pow(diffObaI, 2);
				final double errModeI = Math.pow(diffModeI, 2);
				errorsOba.add(errObaI);
				errorsMode.add(errModeI);
				diffOba.add(Math.abs(diffObaI));
				diffMode.add(Math.abs(diffModeI));
				i++;
				j++;
			}
		}

		double sumErrorsOba = 0;
		for (final Double errorOba : errorsOba) {
			sumErrorsOba += errorOba;
		}
		double sumErrorsMode = 0;
		for (final Double errorMode : errorsMode) {
			sumErrorsMode += errorMode;
		}

		final double errorObaK = Math.sqrt(sumErrorsOba / errorsOba.size());
		final double errorModeK = Math.sqrt(sumErrorsMode / errorsMode.size());

		Map<Integer, Double> obaErrorMap = errorObaPerKPerTripId.get(tripId);
		if (obaErrorMap == null) {
			obaErrorMap = new LinkedHashMap<>();
			obaErrorMap.put(k, errorObaK);
			errorObaPerKPerTripId.put(tripId, obaErrorMap);
		} else {
			obaErrorMap.put(k, errorObaK);
		}

		Map<Integer, Double> modeErrorMap = errorModePerKPerTripId.get(tripId);
		if (modeErrorMap == null) {
			modeErrorMap = new LinkedHashMap<>();
			modeErrorMap.put(k, errorModeK);
			errorModePerKPerTripId.put(tripId, modeErrorMap);
		} else {
			modeErrorMap.put(k, errorModeK);
		}

		System.out.println(errorObaPerKPerTripId.get(tripId).get(k) + "\t"
				+ errorModePerKPerTripId.get(tripId).get(k));

		// System.out.println("Diff OBA");
		// for (final Double diffO : diffOba) {
		// System.out.println(diffO);
		// }
		// System.out.println("Diff MODE");
		// for (final Double diffM : diffMode) {
		// System.out.println(diffM);
		// }
	}

	private long getActual(final Segment segment,
			final TripInstance tripInstance) {
		long result = 0;
		final Stop stop = segment.getFrom();
		if (segment.isFirst()) {
			// assume from actual is equal to scheduled one
			final String actual = stop.getStopTime().getSchedArrivalTime();
			result = Utils.time(tripInstance.getServiceDate(), actual);
		} else {
			result = timeEstimator.actual(tripInstance, stop);
		}
		return result;
	}

	private long getActualLast(final Segment segment,
			final TripInstance tripInstance) {
		final Stop stop = segment.getTo();
		final long result = timeEstimator.actual(tripInstance, stop);
		return result;
	}

	private long getScheduled(final Segment segment,
			final TripInstance tripInstance) {
		final Stop stop = segment.getFrom();
		final String scheduled = stop.getStopTime().getSchedArrivalTime();
		return Utils.time(tripInstance.getServiceDate(), scheduled);
	}

	private long getScheduledLast(final Segment segment,
			final TripInstance tripInstance) {
		final Stop stop = segment.getTo();
		final String scheduled = stop.getStopTime().getSchedArrivalTime();
		return Utils.time(tripInstance.getServiceDate(), scheduled);
	}

	private long getScheduledDiff(final Segment segmentI, final Segment segmentJ) {
		final String segmentISched = segmentI.getTo().getStopTime()
				.getSchedArrivalTime();
		final String segmentJSched = segmentJ.getFrom().getStopTime()
				.getSchedArrivalTime();
		return Utils.diff(segmentISched, segmentJSched);
	}

	@Override
	public long[] getObaAndModeErrors(final TripInstance tripInstance,
			final Segment segment) {
		final long[] result = new long[2];
		try {
			final String segmentISched = segment.getTo().getStopTime()
					.getSchedArrivalTime();
			final String segmentJSched = segment.getFrom().getStopTime()
					.getSchedArrivalTime();
			final long scheduledDiff = Utils.diff(segmentISched, segmentJSched);
			final long actual_I = getActualLast(segment, tripInstance);

			final long t_true_I = actual_I;
			final long actual_J = getActual(segment, tripInstance);
			final long t_hat_oba_I = actual_J + (scheduledDiff * 1000);
			final String key_J = Utils.label(tripInstance, segment);
			final double y_hat_J_sec = yHatPerSegmentPerTripInstancePerTrip
					.get(key_J);
			final long y_hat_J = Math.round(y_hat_J_sec) * -1000;
			final long t_hat_mode_I = t_hat_oba_I + y_hat_J;

			final long errorObaI = (t_true_I - t_hat_oba_I) / 1000;
			final long errorModeI = (t_true_I - t_hat_mode_I) / 1000;
			result[0] = errorObaI;
			result[1] = errorModeI;
		} catch (final Exception exc) {
			LOG.error("Don't have info on this segment and tripInstance");
		}
		return result;
	}

	@Override
	public void calculateObaAndModeError(final List<Integer> tripIds,
			final int k) {

		final Set<Trip> trips = tripDao.getTripsIn(tripIds);
		final Set<Trip> tripsTrain = new LinkedHashSet<>(trips.size());
		final Set<Trip> tripsTest = new LinkedHashSet<>(trips.size());
		splitDataset(trips, tripsTrain, tripsTest);

		final List<Double> errorsObaTrain = new ArrayList<>();
		final List<Double> errorsModeTrain = new ArrayList<>();
		final List<Double> diffObaTrain = new ArrayList<>();
		final List<Double> diffModeTrain = new ArrayList<>();
		for (final Trip tripTrain : tripsTrain) {
			doCalculations(tripTrain, k, errorsObaTrain, errorsModeTrain,
					diffObaTrain, diffModeTrain);
		}

		double sumErrorsObaTrain = 0;
		for (final Double errorObaTrain : errorsObaTrain) {
			sumErrorsObaTrain += errorObaTrain;
		}
		double sumErrorsModeTrain = 0;
		for (final Double errorModeTrain : errorsModeTrain) {
			sumErrorsModeTrain += errorModeTrain;
		}

		final double errorObaTrainK = Math.sqrt(sumErrorsObaTrain
				/ errorsObaTrain.size());
		final double errorModeTrainK = Math.sqrt(sumErrorsModeTrain
				/ errorsModeTrain.size());

		// ----------------

		final List<Double> errorsObaTest = new ArrayList<>();
		final List<Double> errorsModeTest = new ArrayList<>();
		final List<Double> diffObaTest = new ArrayList<>();
		final List<Double> diffModeTest = new ArrayList<>();
		for (final Trip tripTest : tripsTest) {
			doCalculations(tripTest, k, errorsObaTest, errorsModeTest,
					diffObaTest, diffModeTest);
		}

		double sumErrorsObaTest = 0;
		for (final Double errorObaTest : errorsObaTest) {
			sumErrorsObaTest += errorObaTest;
		}
		double sumErrorsModeTest = 0;
		for (final Double errorModeTest : errorsModeTest) {
			sumErrorsModeTest += errorModeTest;
		}

		final double errorObaTestK = Math.sqrt(sumErrorsObaTest
				/ errorsObaTest.size());
		final double errorModeTestK = Math.sqrt(sumErrorsModeTest
				/ errorsModeTest.size());

		System.out.println(errorObaTestK + "\t" + errorModeTestK);

		// System.out.println("Diff OBA");
		// for (final Double diffOTest : diffObaTest) {
		// System.out.println(diffOTest);
		// }
		// System.out.println("Diff MODE");
		// for (final Double diffMTest : diffModeTest) {
		// System.out.println(diffMTest);
		// }
	}

	private void doCalculations(final Trip trip, final int k,
			final List<Double> errorsOba, final List<Double> errorsMode,
			final List<Double> diffOba, final List<Double> diffMode) {
		distanceAlongTripCalculator.addDistancesAlongTrip(trip);
		final Set<TripInstance> tripInstances = trip.getInstances();
		final Iterator<TripInstance> tripInstancesIt = tripInstances.iterator();
		final List<Segment> segments = new ArrayList<>(trip.getSegments());
		final int numberOfSegments = segments.size();

		assert k < numberOfSegments;
		while (tripInstancesIt.hasNext()) {
			final TripInstance tripInstance = tripInstancesIt.next();
			int j = 0;
			int i = k;
			while (i <= numberOfSegments) {
				final long scheduledDiff = getScheduledDiff(
						segments.get(i - 1), segments.get(j));
				long actual_I = 0;
				if (i == numberOfSegments) {
					actual_I = getActualLast(segments.get(i - 1), tripInstance);
				} else {
					actual_I = getActual(segments.get(i), tripInstance);
				}

				final long t_true_I = actual_I;
				final long actual_J = getActual(segments.get(j), tripInstance);
				final long t_hat_oba_I = actual_J + (scheduledDiff * 1000);
				final String key_J = Utils.label(tripInstance, segments.get(j));
				final double y_hat_J_sec = yHatPerSegmentPerTripInstancePerTrip
						.get(key_J);
				final long y_hat_J = Math.round(y_hat_J_sec) * -1000;
				final long t_hat_mode_I = t_hat_oba_I + y_hat_J;

				final double diffObaI = (t_true_I - t_hat_oba_I) / 1000;
				final double diffModeI = (t_true_I - t_hat_mode_I) / 1000;
				final double errObaI = Math.pow(diffObaI, 2);
				final double errModeI = Math.pow(diffModeI, 2);
				errorsOba.add(errObaI);
				errorsMode.add(errModeI);
				diffOba.add(Math.abs(diffObaI));
				diffMode.add(Math.abs(diffModeI));
				i++;
				j++;
			}
		}
	}

	private void splitDataset(final Set<Trip> trips,
			final Set<Trip> tripsTrain, final Set<Trip> tripsTest) {
		for (final Trip trip : trips) {
			final Trip trainTrip = new Trip(trip.getId(), trip.getHeadSign());
			trainTrip.setSegments(trip.getSegments());
			final Trip testTrip = new Trip(trip.getId(), trip.getHeadSign());
			testTrip.setSegments(trip.getSegments());
			final Set<TripInstance> instances = trip.getInstances();
			for (final TripInstance tripInstance : instances) {
				final long serviceDate = tripInstance.getServiceDate();
				final int monthOfYear = Utils.monthOfYear(serviceDate);
				final int year = Utils.year(serviceDate);
				// october september 2013 for testing
				if ((year == 2013)
						&& ((monthOfYear == 10) || (monthOfYear == 9))) {
					testTrip.addInstance(tripInstance);
					tripsTest.add(testTrip);
				} else {
					trainTrip.addInstance(tripInstance);
					tripsTrain.add(trainTrip);
				}
			}
		}
	}
}
