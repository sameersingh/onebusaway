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
import edu.uw.modelab.service.FeatureFileCreator;
import edu.uw.modelab.utils.Utils;

public class PerSegmentFeatureFileCreator implements FeatureFileCreator {

	private static final Logger LOG = LoggerFactory
			.getLogger(PerSegmentFeatureFileCreator.class);

	private final String featureFile;
	private final String featureLabelsFile;
	private final TripDao tripDao;

	public PerSegmentFeatureFileCreator(final String featureFile,
			final String featureLabelsFile, final TripDao tripDao) {
		this.featureFile = featureFile;
		this.featureLabelsFile = featureLabelsFile;
		this.tripDao = tripDao;

	}

	@Override
	public void createFeatureLabels(final int tripId) {
		final Trip trip = tripDao.getTripById(tripId);
		final Set<TripInstance> tripInstances = trip.getInstances();
		final Iterator<TripInstance> tripInstancesIt = tripInstances.iterator();
		final Set<Segment> segments = trip.getSegments();
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(featureLabelsFile));
			while (tripInstancesIt.hasNext()) {
				final TripInstance tripInstance = tripInstancesIt.next();
				for (final Segment segment : segments) {
					pw.println(tripInstance.getTripId() + "_"
							+ tripInstance.getServiceDate() + "_"
							+ segment.name());
				}
			}
		} catch (final FileNotFoundException exc) {
			LOG.error("Error creating feature labels file. Msg {}",
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
		final StringBuilder sb = new StringBuilder();
		while (tripInstancesIt.hasNext()) {
			final TripInstance tripInstance = tripInstancesIt.next();
			final long serviceDate = tripInstance.getServiceDate();
			final int dayOfWeek = Utils.dayOfWeek(serviceDate);
			final int monthOfYear = Utils.monthOfYear(serviceDate);
			for (int i = 0; i < segments.size(); i++) {
				sb.append("1" + "\t" + segments.get(i).getDistance() + "\t");
				appendDayOfWeek(sb, dayOfWeek);
				appendMonthOfYear(sb, monthOfYear);
				appendSegments(sb, i, segments.size());
				sb.append("\n");
			}
		}

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(featureFile));
			pw.print(sb.toString());
			pw.flush();
		} catch (final FileNotFoundException exc) {
			LOG.error("Error creating feature file. Msg {}", exc.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	private void appendSegments(final StringBuilder sb, final int i,
			final int size) {
		final int[] segments = new int[size];
		segments[i] = 1;
		for (int j = 0; j < segments.length; j++) {
			sb.append(segments[j]);
			if (j < size) {
				sb.append("\t");
			}
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

}
