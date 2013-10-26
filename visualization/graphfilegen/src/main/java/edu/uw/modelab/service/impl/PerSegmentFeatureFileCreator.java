package edu.uw.modelab.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
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

	private final String featureFileTraining;
	private final String featureFileTest;
	private final String labelsFileTraining;
	private final String labelsFileTest;
	private final TripDao tripDao;
	private final DelayCalculator delayCalculator;

	public PerSegmentFeatureFileCreator(final String featureFileTraining,
			final String featureFileTest, final String labelsFileTraining,
			final String labelsFileTest, final TripDao tripDao,
			final DelayCalculator delayCalculator) {
		this.featureFileTraining = featureFileTraining;
		this.featureFileTest = featureFileTest;
		this.labelsFileTraining = labelsFileTraining;
		this.labelsFileTest = labelsFileTest;
		this.tripDao = tripDao;
		this.delayCalculator = delayCalculator;

	}

	@Override
	public void createFeatureLabels(final int tripId) {
		final Trip trip = tripDao.getTripById(tripId);
		final Set<TripInstance> tripInstances = trip.getInstances();
		final Iterator<TripInstance> tripInstancesIt = tripInstances.iterator();
		final Set<Segment> segments = trip.getSegments();

		final StringBuilder sbTraining = new StringBuilder();
		final StringBuilder sbTest = new StringBuilder();
		while (tripInstancesIt.hasNext()) {
			final TripInstance tripInstance = tripInstancesIt.next();
			final long serviceDate = tripInstance.getServiceDate();
			final int monthOfYear = Utils.monthOfYear(serviceDate);
			for (final Segment segment : segments) {
				// september for testing
				if (monthOfYear == 9) {
					sbTest.append(Utils.label(tripInstance, segment) + "\n");
				} else {
					sbTraining
							.append(Utils.label(tripInstance, segment) + "\n");
				}
			}
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(labelsFileTraining));
			pw.print(sbTraining.toString());
			pw.flush();
		} catch (final FileNotFoundException exc) {
			LOG.error("Error creating training labels file. Msg {}",
					exc.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

		try {
			pw = new PrintWriter(new File(labelsFileTest));
			pw.print(sbTest.toString());
			pw.flush();
		} catch (final FileNotFoundException exc) {
			LOG.error("Error creating test labels file. Msg {}",
					exc.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	@Override
	public void createFeatures(final int tripId) {
		final Trip trip = tripDao.getTripById(tripId);
		final Set<TripInstance> tripInstances = trip.getInstances();
		final Iterator<TripInstance> tripInstancesIt = tripInstances.iterator();
		final List<Segment> segments = new ArrayList<>(trip.getSegments());
		final StringBuilder sbTraining = new StringBuilder();
		final StringBuilder sbTest = new StringBuilder();
		while (tripInstancesIt.hasNext()) {
			final TripInstance tripInstance = tripInstancesIt.next();
			final long serviceDate = tripInstance.getServiceDate();
			final int dayOfWeek = Utils.dayOfWeek(serviceDate);
			final int monthOfYear = Utils.monthOfYear(serviceDate);
			// september for testing
			if (monthOfYear == 9) {
				appendLine(segments, sbTest, tripInstance, dayOfWeek,
						monthOfYear);
			} else {
				appendLine(segments, sbTraining, tripInstance, dayOfWeek,
						monthOfYear);
			}
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(featureFileTraining));
			pw.print(sbTraining.toString());
			pw.flush();
		} catch (final FileNotFoundException exc) {
			LOG.error("Error creating feature file for training. Msg {}",
					exc.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

		try {
			pw = new PrintWriter(new File(featureFileTest));
			pw.print(sbTest.toString());
			pw.flush();
		} catch (final FileNotFoundException exc) {
			LOG.error("Error creating feature file for test. Msg {}",
					exc.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	private void appendLine(final List<Segment> segments,
			final StringBuilder sb, final TripInstance tripInstance,
			final int dayOfWeek, final int monthOfYear) {
		for (int i = 0; i < segments.size(); i++) {
			sb.append("1" + "\t" + segments.get(i).getDistance() + "\t");
			appendDayOfWeek(sb, dayOfWeek);
			// appendMonthOfYear(sb, monthOfYear);
			appendTimeOfDay(sb, segments.get(i));
			appendSegments(sb, i, segments.size());
			sb.append(delayCalculator.calculateDelay(segments.get(i),
					tripInstance));
			sb.append("\n");
		}
	}

	private void appendTimeOfDay(final StringBuilder sb, final Segment segment) {
		final String result = Utils.getTimeOfDayVector(segment.getFrom()
				.getStopTime().getSchedArrivalTime(), segment.getTo()
				.getStopTime().getSchedArrivalTime());
		sb.append(result);
	}

	private void appendSegments(final StringBuilder sb, final int i,
			final int size) {
		final int[] segments = new int[size];
		segments[i] = 1;
		for (final int segment : segments) {
			sb.append(segment + "\t");
		}
	}

	private void appendDayOfWeek(final StringBuilder sb, final int dayOfWeek) {
		final int[] days = new int[7];
		days[dayOfWeek - 1] = 1;
		for (final int day : days) {
			sb.append(day + "\t");
		}
	}

	private void appendMonthOfYear(final StringBuilder sb, final int monthOfYear) {
		final int[] months = new int[12];
		months[monthOfYear - 1] = 1;
		for (final int month : months) {
			sb.append(month + "\t");
		}
	}

	@Override
	public void createFeatures(final List<Integer> tripIds) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createFeatureLabels(final List<Integer> tripIds) {
		// TODO Auto-generated method stub

	}

}
