package edu.uw.modelab.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.uw.modelab.pojo.BusPosition;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.service.TimeEstimator;
import edu.uw.modelab.utils.Utils;

public class DefaultTimeEstimator implements TimeEstimator {

	@Override
	public void estimateArrivalTime(final List<BusPosition> busPositions,
			final Trip trip) {
		final Set<Segment> segments = trip.getSegments();
		for (final Segment segment : segments) {
			final Stop from = segment.getFrom();
			final Stop to = segment.getTo();
			final double estimatedTime = estimateTime(to, busPositions);
			System.out.println("From: " + from);
			System.out.println("To: " + to);
			System.out.println("ArrivalTime: "
					+ to.getStopTime().getArrivalTime());
			System.out.println("Estimated ArrivalTime: " + estimatedTime);
			System.out.println();
		}
	}

	private long estimateTime(final Stop to,
			final List<BusPosition> busPositions) {
		final double toX = to.getX();
		final double toY = to.getY();
		final List<IndexedDistance> distances = new ArrayList<>();
		int i = 0;
		for (final BusPosition bp : busPositions) {
			distances.add(new IndexedDistance(i++, Utils.euclideanDistance(
					bp.getX(), toX, bp.getY(), toY)));
		}
		Collections.sort(distances);
		final BusPosition closest = busPositions.get(distances.get(0).index);
		final BusPosition secondClosest = busPositions
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
		return tHat;
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
	}
}
