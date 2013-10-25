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
import edu.uw.modelab.service.TimeService;
import edu.uw.modelab.utils.Utils;

public class DefaultTimeService implements TimeService {

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultTimeService.class);

	@Override
	public long actualDiff(final TripInstance tripInstance,
			final Segment segment) {
		long result = 0;
		final long toActual = getActualArrivalTimeBasedOnClosestPositions(
				segment.getTo(), tripInstance);
		if (segment.isFirst()) {
			// assume from actual is equal to scheduled one
			final String fromActual = segment.getFrom().getStopTime()
					.getSchedArrivalTime();
			result = Utils.diff(fromActual, toActual);
		} else {
			final long fromActual = getActualArrivalTimeBasedOnClosestPositions(
					segment.getFrom(), tripInstance);
			result = (toActual - fromActual) / 1000;
		}
		return result;
	}

	@Override
	public long actual(final TripInstance tripInstance, final Stop stop) {
		// final long basedOnDistanceAlong =
		// getActualArrivalTimeBasedOnDistanceAlongTrip(
		// stop, tripInstance);
		// return basedOnDistanceAlong;
		final long basedOnClosestPositions = getActualArrivalTimeBasedOnClosestPositions(
				stop, tripInstance);
		return basedOnClosestPositions;
	}

	@Override
	public void estimateArrivalTimes(final Trip trip) {
		final Set<Segment> segments = trip.getSegments();
		final Set<TripInstance> tripInstances = trip.getInstances();
		for (final Segment segment : segments) {
			final Stop from = segment.getFrom();
			final Stop to = segment.getTo();
			final long actualArrivalTime = estimatedTime(to, tripInstances);
			LOG.info("From: " + from);
			LOG.info("To: " + to);
			LOG.info("Scheduled arrival time: "
					+ to.getStopTime().getSchedArrivalTime());
			LOG.info("Actual arrival time: "
					+ Utils.toHHMMssPST(actualArrivalTime));
		}
	}

	private long getActualArrivalTimeBasedOnDistanceAlongTrip(final Stop stop,
			final TripInstance tripInstance) {
		// ugly method, improve

		final List<RealtimePosition> rtps = tripInstance.getRealtimes();
		final double stopDistanceAlongTrip = stop.getDistanceAlongTrip();

		double before = 0;
		double after = Double.MAX_VALUE;
		RealtimePosition rtpBefore = null;
		RealtimePosition rtpAfter = null;
		final double[] diffs = new double[rtps.size()];
		int i = 0;
		for (final RealtimePosition rtp : rtps) {
			final double distanceAlongTrip = rtp.getDistanceAlongTrip();
			if ((distanceAlongTrip < stopDistanceAlongTrip)
					&& (distanceAlongTrip > before)) {
				before = distanceAlongTrip;
				rtpBefore = rtp;
			} else if ((distanceAlongTrip > stopDistanceAlongTrip)
					&& (distanceAlongTrip < after)) {
				after = distanceAlongTrip;
				rtpAfter = rtp;
			}
			diffs[i++] = Math.abs(distanceAlongTrip - stopDistanceAlongTrip);
		}

		// assume not before and after can be null at the same time
		if (rtpBefore == null) {
			final int closestIndex = getClosestPoint(diffs);
			rtpBefore = rtps.get(closestIndex);
			LOG.debug("taking the closest due to lack of before");
		} else if (rtpAfter == null) {
			final int closestIndex = getClosestPoint(diffs);
			rtpAfter = rtps.get(closestIndex);
			LOG.debug("taking the closest due to lack of after");
		}

		final double ds = stopDistanceAlongTrip;
		final double di = rtpBefore.getDistanceAlongTrip();
		final double dj = rtpAfter.getDistanceAlongTrip();
		final long ti = rtpBefore.getTimeStamp();
		final long tj = rtpAfter.getTimeStamp();
		final long ts = (long) (((ds - di) / (dj - di)) * (tj - ti)) + ti;
		// LOG.debug("di {} - ti {} - dj {} tj {} - ds {} ts {} - sched {}", di,
		// Utils.toHHMMssPST(ti), dj, Utils.toHHMMssPST(tj), ds,
		// Utils.toHHMMssPST(ts), stop.getStopTime().getSchedArrivalTime());
		final long diff = Utils.diff(stop.getStopTime().getSchedArrivalTime(),
				ts);
		if (diff > 1200) {
			LOG.debug(
					"ALERT - stopId {} tripId {} tripInstance {} ts {} scheduled {}",
					stop.getId(), tripInstance.getTripId(), tripInstance
							.getServiceDate(), Utils.toHHMMssPST(ts), stop
							.getStopTime().getSchedArrivalTime());
		}
		return ts;
	}

	private int getClosestPoint(final double[] diffs) {
		double minor = Double.MAX_VALUE;
		int closestIndex = 0;
		for (int s = 0; s < diffs.length; s++) {
			if (diffs[s] < minor) {
				minor = diffs[s];
				closestIndex = s;
			}
		}
		return closestIndex;
	}

	private long getActualArrivalTimeBasedOnClosestPositions(final Stop stop,
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
