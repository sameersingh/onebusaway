package edu.uw.modelab.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.dao.TripDao;
import edu.uw.modelab.error.pojo.Dataset;
import edu.uw.modelab.error.pojo.RootMeanSquareError;
import edu.uw.modelab.feature.DatasetSplitCondition;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.pojo.TripInstance;
import edu.uw.modelab.service.DistanceAlongTripPopulator;
import edu.uw.modelab.service.ErrorService;
import edu.uw.modelab.service.TimeService;
import edu.uw.modelab.utils.Utils;

/**
 * Calculates OBA and OUR errors
 * 
 */
public class ErrorServiceImpl implements ErrorService {

	private static final Logger LOG = LoggerFactory
			.getLogger(ErrorServiceImpl.class);

	private final Map<String, Double> yHatTrainValues;
	private final Map<String, Double> yHatTestValues;
	private final Map<String, Double> yTrainValues;
	private final Map<String, Double> yTestValues;
	private final TripDao tripDao;
	private final TimeService timeService;
	private final DatasetSplitCondition datasetSplitCondition;
	private final String yhatTrainFileName;
	private final String yHatTestFileName;
	private final String yTrainFileName;
	private final String yTestFileName;

	private final DistanceAlongTripPopulator distanceAlongTripPopulator;

	public ErrorServiceImpl(final TripDao tripDao,
			final TimeService timeService,
			final DistanceAlongTripPopulator distanceAlongTripPopulator,
			final DatasetSplitCondition datasetSplitCondition,
			final String yhatTrainFileName, final String yHatTestFileName,
			final String yTrainFileName, final String yTestFileName) {
		this.tripDao = tripDao;
		this.timeService = timeService;
		this.distanceAlongTripPopulator = distanceAlongTripPopulator;
		this.datasetSplitCondition = datasetSplitCondition;
		this.yhatTrainFileName = yhatTrainFileName;
		this.yHatTestFileName = yHatTestFileName;
		this.yTrainFileName = yTrainFileName;
		this.yTestFileName = yTestFileName;
		this.yHatTrainValues = new HashMap<String, Double>();
		this.yHatTestValues = new HashMap<String, Double>();
		this.yTrainValues = new HashMap<String, Double>();
		this.yTestValues = new HashMap<String, Double>();
	}

	public void init() {
		init(yhatTrainFileName, yHatTrainValues);
		init(yHatTestFileName, yHatTestValues);
		init(yTestFileName, yTestValues);
		init(yTrainFileName, yTrainValues);
	}

	private void init(final String fileName, final Map<String, Double> map) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getClass()
					.getResourceAsStream(fileName)));
			String line = null;
			while ((line = br.readLine()) != null) {
				final String[] tokens = line.split(",");
				map.put(tokens[0], Double.valueOf(tokens[1]));
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
	public Map<Dataset, RootMeanSquareError> getErrors(final Set<Trip> trips,
			final int k) {

		final Map<Dataset, RootMeanSquareError> errors = new HashMap<>();
		final Set<Trip> clonedTrips = new LinkedHashSet<>(trips.size());
		for (final Trip trip : trips) {
			clonedTrips.add(distanceAlongTripPopulator
					.getTripWithDistancesAlongTrip(trip));
		}
		final Set<Trip> tripsTrain = new LinkedHashSet<>(clonedTrips.size());
		final Set<Trip> tripsTest = new LinkedHashSet<>(clonedTrips.size());
		splitDataset(clonedTrips, tripsTrain, tripsTest);

		final RootMeanSquareError training = getError(tripsTrain, k,
				yTrainValues, yHatTrainValues);
		final RootMeanSquareError test = getError(tripsTest, k, yTestValues,
				yHatTestValues);

		errors.put(Dataset.TRAIN, training);
		errors.put(Dataset.TEST, test);
		return errors;
	}

	@Override
	// does not depend on k... constant. Error if there was no delay
	public Map<Dataset, Double> getScheduledError(final int tripId) {
		final Trip trip = tripDao.getTripById(tripId);
		final Trip clonedTrip = distanceAlongTripPopulator
				.getTripWithDistancesAlongTrip(trip);
		final Set<Trip> trips = new LinkedHashSet<>();
		trips.add(clonedTrip);
		final Set<Trip> tripsTrain = new LinkedHashSet<>(trips.size());
		final Set<Trip> tripsTest = new LinkedHashSet<>(trips.size());
		splitDataset(trips, tripsTrain, tripsTest);

		// done just for the test set now
		final Trip tripTest = tripsTest.iterator().next();
		final Set<TripInstance> tripInstances = tripTest.getInstances();
		final Iterator<TripInstance> tripInstancesIt = tripInstances.iterator();
		final List<Segment> segments = new ArrayList<>(tripTest.getSegments());
		final int numberOfSegments = segments.size();
		int i = 0;
		final List<Double> errorsSchedule = new ArrayList<>();
		while (tripInstancesIt.hasNext()) {
			final TripInstance tripInstance = tripInstancesIt.next();
			for (final Segment segment : segments) {
				double actual_I = 0;
				double sched_I = 0;
				if (segment.isFirst()) {
					sched_I = timeService.getScheduledTime(tripInstance,
							segment.getFrom());
					actual_I = sched_I;
				} else if (i == (numberOfSegments - 1)) {
					sched_I = timeService.getScheduledTime(tripInstance,
							segment.getTo());
					actual_I = timeService.getActualTime(tripInstance,
							segment.getTo());
				} else {
					actual_I = timeService.getActualTime(tripInstance, segment);
					sched_I = timeService.getScheduledTime(tripInstance,
							segment.getFrom());
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
		LOG.info("schedule error: " + errorSchedule);
		return Collections.singletonMap(Dataset.TEST, errorSchedule);
	}

	@Override
	// quick thing for visualization, repeated code, refactor, try to use
	// calculateObaAndModeError
	public long[] getObaAndModeErrors(final TripInstance tripInstance,
			final Segment segment) {
		final long[] result = new long[2];
		try {
			final String segmentISched = segment.getTo().getStopTime()
					.getSchedArrivalTime();
			final String segmentJSched = segment.getFrom().getStopTime()
					.getSchedArrivalTime();
			final long scheduledDiff = Utils.diff(segmentISched, segmentJSched);
			final long actual_I = timeService.getActualTime(tripInstance,
					segment.getTo());

			final long t_true_I = actual_I;
			final long actual_J = timeService.getActualTime(tripInstance,
					segment);
			final long t_hat_oba_I = actual_J + (scheduledDiff * 1000);
			final String key_J = Utils.label(tripInstance, segment);
			final double y_hat_J_sec = yHatTestValues.get(key_J);
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
	public Map<Dataset, RootMeanSquareError> getErrors(
			final List<Integer> tripIds, final int k) {
		final Set<Trip> trips = tripDao.getTripsIn(tripIds);
		return getErrors(trips, k);
	}

	private void doCalculations(final Trip trip, final int k,
			final List<Double> errorsOba, final List<Double> errorsMode,
			final List<Double> diffOba, final List<Double> diffMode,
			final Map<String, Double> yHatMap, final Map<String, Double> yMap) {
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
				final long scheduledDiff = timeService.getScheduledTimeDiff(
						segments.get(j).getFrom(), segments.get(i - 1).getTo());
				// old stuff, seems that I wasn't considering the s segments
				// between the stops
				// long actual_I = 0;
				// if (i == numberOfSegments) {
				// actual_I = timeService.getActualTime(tripInstance, segments
				// .get(i - 1).getTo());
				// } else {
				// actual_I = timeService.getActualTime(tripInstance, segments
				// .get(i).getFrom());
				// }
				// final long t_true_I = actual_I;
				final long actual_J = timeService.getActualTime(tripInstance,
						segments.get(j));
				final long t_hat_oba_I = actual_J + (scheduledDiff * 1000);
				int s = j;
				double y_hat_S = 0;
				double y_S = 0;
				while (s < i) {
					final String key_S = Utils.label(tripInstance,
							segments.get(s));
					final double y_hat_S_sec = yHatMap.get(key_S);
					final double y_S_sec = yMap.get(key_S);
					y_hat_S += (y_hat_S_sec * -1000);
					y_S += (y_S_sec * -1000);
					s++;
				}
				final double t_true_I = t_hat_oba_I + y_S;
				final double t_hat_mode_I = t_hat_oba_I + y_hat_S;
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
				if (datasetSplitCondition.isForTest(serviceDate)) {
					testTrip.addInstance(tripInstance);
					tripsTest.add(testTrip);
				} else {
					trainTrip.addInstance(tripInstance);
					tripsTrain.add(trainTrip);
				}
			}
		}
	}

	private void binErrors(final String name, final List<Double> errors) {
		final int bins[] = new int[6];
		for (final Double error : errors) {
			if (error <= 60) {
				bins[0] += 1;
			} else if ((error > 60) && (error <= 180)) {
				bins[1] += 1;
			} else if ((error > 180) && (error <= 300)) {
				bins[2] += 1;
			} else if ((error > 300) && (error <= 420)) {
				bins[3] += 1;
			} else if ((error > 420) && (error <= 600)) {
				bins[4] += 1;
			} else if (error > 600) {
				bins[5] += 1;
			}
		}
		System.out.println(name);
		for (final int element : bins) {
			System.out.println(element);
		}
	}

	private RootMeanSquareError getError(final Set<Trip> trips, final int k,
			final Map<String, Double> yValues,
			final Map<String, Double> yHatValues) {
		final List<Double> errorsOba = new ArrayList<>();
		final List<Double> errorsOur = new ArrayList<>();
		final List<Double> diffOba = new ArrayList<>();
		final List<Double> diffOur = new ArrayList<>();
		for (final Trip trip : trips) {
			doCalculations(trip, k, errorsOba, errorsOur, diffOba, diffOur,
					yHatValues, yValues);
		}

		double sumErrorsOba = 0;
		for (final Double errorOba : errorsOba) {
			sumErrorsOba += errorOba;
		}
		double sumErrorsOur = 0;
		for (final Double errorOur : errorsOur) {
			sumErrorsOur += errorOur;
		}

		final double errorObaK = Math.sqrt(sumErrorsOba / errorsOba.size());
		final double errorOurK = Math.sqrt(sumErrorsOur / errorsOur.size());
		return new RootMeanSquareError(errorObaK, errorOurK);
	}
}
