package edu.uw.modelab.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.dao.TripDao;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.pojo.TripInstance;
import edu.uw.modelab.service.DelayCalculator;
import edu.uw.modelab.service.FeatureFileCreator;
import edu.uw.modelab.utils.Utils;

public class PerSegmentFeatureFileCreator implements FeatureFileCreator {

	private static final Logger LOG = LoggerFactory
			.getLogger(PerSegmentFeatureFileCreator.class);

	private static final String SEPARATOR = "\t";
	private static final String END_LINE = "\n";

	private final String featureFileTraining;
	private final String featureFileTest;
	private final String featureNames;
	private final String labelsFileTraining;
	private final String labelsFileTest;
	private final TripDao tripDao;
	private final DelayCalculator delayCalculator;

	public PerSegmentFeatureFileCreator(final String featureFileTraining,
			final String featureFileTest, final String labelsFileTraining,
			final String labelsFileTest, final String featureNames,
			final TripDao tripDao, final DelayCalculator delayCalculator) {
		this.featureFileTraining = featureFileTraining;
		this.featureFileTest = featureFileTest;
		this.labelsFileTraining = labelsFileTraining;
		this.labelsFileTest = labelsFileTest;
		this.featureNames = featureNames;
		this.tripDao = tripDao;
		this.delayCalculator = delayCalculator;
	}

	@Override
	public void createFeatures(final int tripId) {
		final List<Integer> tripIds = Collections.singletonList(tripId);
		createFeatures(tripIds);
	}

	@Override
	public void createFeatures(final List<Integer> tripIds) {
		final Set<Trip> trips = tripDao.getTripsIn(tripIds);

		PrintWriter pwTrain = null;
		PrintWriter pwTest = null;
		PrintWriter pwTrainLabels = null;
		PrintWriter pwTestLabels = null;
		PrintWriter pwFeatureNames = null;

		try {
			pwTrain = new PrintWriter(new File(featureFileTraining));
			pwTest = new PrintWriter(new File(featureFileTest));
			pwTrainLabels = new PrintWriter(new File(labelsFileTraining));
			pwTestLabels = new PrintWriter(new File(labelsFileTest));
			pwFeatureNames = new PrintWriter(new File(featureNames));

			final Set<String> uniqueSegments = new LinkedHashSet<>();
			final Set<Integer> uniqueTripIds = new LinkedHashSet<>();
			for (final Trip trip : trips) {
				uniqueTripIds.add(trip.getId());
				final Set<Segment> segments = trip.getSegments();
				for (final Segment segment : segments) {
					uniqueSegments.add(segment.getId());
				}
			}

			// totally coupled with the order, refactor when there's some time
			// available
			pwFeatureNames
					.print(getFeatureNames(uniqueSegments, uniqueTripIds));

			final List<String> uniqueSegmentsAsList = new ArrayList<>(
					uniqueSegments);
			final List<Integer> uniqueTripIdsAsList = new ArrayList<>(
					uniqueTripIds);
			for (final Trip trip : trips) {
				final Set<TripInstance> tripInstances = trip.getInstances();
				final Iterator<TripInstance> tripInstancesIt = tripInstances
						.iterator();
				final Set<Segment> segments = trip.getSegments();
				while (tripInstancesIt.hasNext()) {
					final TripInstance tripInstance = tripInstancesIt.next();
					final long serviceDate = tripInstance.getServiceDate();
					final int dayOfWeek = Utils.dayOfWeek(serviceDate);
					final int monthOfYear = Utils.monthOfYear(serviceDate);
					for (final Segment segment : segments) {
						// correct this, september for testing
						if (monthOfYear == 9) {
							pwTest.println(appendLine(segment, tripInstance,
									monthOfYear, dayOfWeek,
									uniqueSegmentsAsList, uniqueTripIdsAsList));
							pwTestLabels.println(Utils.label(tripInstance,
									segment));
						} else {
							pwTrain.println(appendLine(segment, tripInstance,
									monthOfYear, dayOfWeek,
									uniqueSegmentsAsList, uniqueTripIdsAsList));
							pwTrainLabels.println(Utils.label(tripInstance,
									segment));
						}
					}
				}
			}

		} catch (final FileNotFoundException exc) {
			LOG.error("Error creating training labels file. Msg {}",
					exc.getMessage());
		} finally {
			if (pwTrain != null) {
				pwTrain.close();
			}
			if (pwTest != null) {
				pwTest.close();
			}
			if (pwTrainLabels != null) {
				pwTrainLabels.close();
			}
			if (pwTestLabels != null) {
				pwTestLabels.close();
			}
			if (pwFeatureNames != null) {
				pwFeatureNames.close();
			}
		}
	}

	private String getFeatureNames(final Set<String> uniqueSegments,
			final Set<Integer> uniqueTripIds) {
		final StringBuilder sb = new StringBuilder();
		sb.append("bias").append(END_LINE);
		sb.append("distance").append(END_LINE);
		sb.append("0-2").append(END_LINE);
		sb.append("2-4").append(END_LINE);
		sb.append("4-6").append(END_LINE);
		sb.append("6-8").append(END_LINE);
		sb.append("8-10").append(END_LINE);
		sb.append("10-12").append(END_LINE);
		sb.append("12-14").append(END_LINE);
		sb.append("14-16").append(END_LINE);
		sb.append("16-18").append(END_LINE);
		sb.append("18-20").append(END_LINE);
		sb.append("20-22").append(END_LINE);
		sb.append("22-24").append(END_LINE);
		sb.append("monday").append(END_LINE);
		sb.append("tuesday").append(END_LINE);
		sb.append("wednesday").append(END_LINE);
		sb.append("thursday").append(END_LINE);
		sb.append("friday").append(END_LINE);
		sb.append("saturday").append(END_LINE);
		sb.append("sunday").append(END_LINE);
		sb.append("january").append(END_LINE);
		sb.append("february").append(END_LINE);
		sb.append("march").append(END_LINE);
		sb.append("april").append(END_LINE);
		sb.append("may").append(END_LINE);
		sb.append("june").append(END_LINE);
		sb.append("july").append(END_LINE);
		sb.append("august").append(END_LINE);
		sb.append("september").append(END_LINE);
		sb.append("october").append(END_LINE);
		sb.append("november").append(END_LINE);
		sb.append("december").append(END_LINE);
		for (final String segment : uniqueSegments) {
			sb.append(segment).append(END_LINE);
		}
		for (final Integer trip : uniqueTripIds) {
			sb.append(trip).append(END_LINE);
		}
		return sb.toString();
	}

	private String appendLine(final Segment segment,
			final TripInstance tripInstance, final int monthOfYear,
			final int dayOfWeek, final List<String> uniqueSegments,
			final List<Integer> uniqueTripIds) {
		final StringBuilder sb = new StringBuilder();
		sb.append(1).append(SEPARATOR);
		sb.append(segment.getDistance()).append(SEPARATOR);
		sb.append(getTimeOfDay(segment)).append(SEPARATOR);
		sb.append(getDayOfWeek(dayOfWeek)).append(SEPARATOR);
		sb.append(getMonthOfYear(monthOfYear)).append(SEPARATOR);
		sb.append(getSegment(segment.getId(), uniqueSegments))
				.append(SEPARATOR);
		sb.append(getTrip(tripInstance.getTripId(), uniqueTripIds)).append(
				SEPARATOR);
		sb.append(delayCalculator.calculateDelay(segment, tripInstance));
		return sb.toString();
	}

	private String getSegment(final String id, final List<String> uniqueSegments) {
		final int[] segments = new int[uniqueSegments.size()];
		final int index = uniqueSegments.indexOf(id);
		segments[index] = 1;
		final StringBuilder sb = new StringBuilder();
		for (final int segment : segments) {
			sb.append(segment + "\t");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	private String getTrip(final int id, final List<Integer> uniqueTrips) {
		final int[] trips = new int[uniqueTrips.size()];
		final int index = uniqueTrips.indexOf(id);
		trips[index] = 1;
		final StringBuilder sb = new StringBuilder();
		for (final int trip : trips) {
			sb.append(trip + "\t");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	private String getDayOfWeek(final int dayOfWeek) {
		final int[] days = new int[7];
		days[dayOfWeek - 1] = 1;
		final StringBuilder sb = new StringBuilder();
		for (final int day : days) {
			sb.append(day + "\t");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	private String getMonthOfYear(final int monthOfYear) {
		final int[] months = new int[12];
		months[monthOfYear - 1] = 1;
		final StringBuilder sb = new StringBuilder();
		for (final int month : months) {
			sb.append(month + "\t");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	private String getTimeOfDay(final Segment segment) {
		return Utils.getTimeOfDayVector(segment.getFrom().getStopTime()
				.getSchedArrivalTime(), segment.getTo().getStopTime()
				.getSchedArrivalTime());
	}

}
