package edu.uw.modelab.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.pojo.RealtimePosition;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.pojo.TripInstance;
import edu.uw.modelab.service.TimeEstimator;
import edu.uw.modelab.utils.Utils;

public class DefaultTimeEstimator implements TimeEstimator {

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultTimeEstimator.class);

	@Override
	public long actualDiff(final TripInstance tripInstance,
			final Segment segment) {
		long result = 0;
		final long toActual = getActualArrivalTime(segment.getTo(),
				tripInstance);
		if (segment.isFirst()) {
			// assume from actual is equal to scheduled one
			final String fromActual = segment.getFrom().getStopTime()
					.getSchedArrivalTime();
			result = Utils.diff(fromActual, toActual);
		} else {
			final long fromActual = getActualArrivalTime(segment.getFrom(),
					tripInstance);
			result = (toActual - fromActual) / 1000;
		}
		return result;
	}

	@Override
	public void estimateArrivalTimes(final Trip trip) {
		final Set<Segment> segments = trip.getSegments();
		final Set<TripInstance> tripInstances = trip.getInstances();
		for (final Segment segment : segments) {
			final Stop from = segment.getFrom();
			final Stop to = segment.getTo();
			final long actualArrivalTime = estimatedTime(to, tripInstances);
			to.getStopTime().setActualArrivalTime(actualArrivalTime);
			LOG.info("From: " + from);
			LOG.info("To: " + to);
			LOG.info("Scheduled arrival time: "
					+ to.getStopTime().getSchedArrivalTime());
			LOG.info("Actual arrival time: "
					+ Utils.toHHMMssPST(actualArrivalTime));
		}
	}

	private long getActualArrivalTime(final Stop stop,
			final TripInstance tripInstance) {
		final double toX = stop.getX();
		final double toY = stop.getY();

		final List<RealtimePosition> rtps = tripInstance.getRealtimes();
		final List<IndexedDistance> distances = new ArrayList<>();
		int i = 0;
		for (final RealtimePosition rtp : rtps) {
			distances.add(new IndexedDistance(i++, Utils.euclideanDistance(
					rtp.getX(), toX, rtp.getY(), toY)));
		}
		Collections.sort(distances);
		final RealtimePosition closest = rtps.get(distances.get(0).index);
		final RealtimePosition secondClosest = rtps.get(distances.get(1).index);

		long t1;
		long t2;
		double weightNumerator;
		final double weightDenominator;
		if (closest.getTimeStamp() < secondClosest.getTimeStamp()) {
			t1 = closest.getTimeStamp();
			t2 = secondClosest.getTimeStamp();
			weightNumerator = distances.get(0).distance;
		} else {
			t1 = secondClosest.getTimeStamp();
			t2 = closest.getTimeStamp();
			weightNumerator = distances.get(1).distance;
		}
		weightDenominator = distances.get(0).distance
				+ distances.get(1).distance;
		final long tHat = (long) (t1 + (((t2 - t1) * weightNumerator) / weightDenominator));
		return tHat;
	}

	private long estimatedTime(final Stop to,
			final Set<TripInstance> tripInstances) {
		final double toX = to.getX();
		final double toY = to.getY();
		final List<Long> estimatedTimePerInstance = new ArrayList<>(
				tripInstances.size());
		for (final TripInstance tripInstance : tripInstances) {
			final List<RealtimePosition> rtps = tripInstance.getRealtimes();
			final List<IndexedDistance> distances = new ArrayList<>();
			int i = 0;
			for (final RealtimePosition rtp : rtps) {
				distances.add(new IndexedDistance(i++, Utils.euclideanDistance(
						rtp.getX(), toX, rtp.getY(), toY)));
			}
			Collections.sort(distances);
			final RealtimePosition closest = rtps.get(distances.get(0).index);
			final RealtimePosition secondClosest = rtps
					.get(distances.get(1).index);

			long t1;
			long t2;
			double weightNumerator;
			final double weightDenominator;
			if (closest.getTimeStamp() < secondClosest.getTimeStamp()) {
				t1 = closest.getTimeStamp();
				t2 = secondClosest.getTimeStamp();
				weightNumerator = distances.get(0).distance;
			} else {
				t1 = secondClosest.getTimeStamp();
				t2 = closest.getTimeStamp();
				weightNumerator = distances.get(1).distance;
			}
			weightDenominator = distances.get(0).distance
					+ distances.get(1).distance;
			final long tHat = (long) (t1 + (((t2 - t1) * weightNumerator) / weightDenominator));
			estimatedTimePerInstance.add(tHat);
		}
		LOG.info("Actual Times {}",
				Arrays.toString(estimatedTimePerInstance.toArray()));
		// TODO must do the average here
		return estimatedTimePerInstance.get(0);
	}

	private class IndexedDistance implements Comparable<IndexedDistance> {
		private final int index;
		private final double distance;

		IndexedDistance(final int index, final double distance) {
			this.index = index;
			this.distance = distance;
		}

		@Override
		public int compareTo(final IndexedDistance other) {
			return (int) (distance - other.distance);
		}

		@Override
		public String toString() {
			return "{index=" + index + ", distance=" + distance + "}";
		}
	}

}
