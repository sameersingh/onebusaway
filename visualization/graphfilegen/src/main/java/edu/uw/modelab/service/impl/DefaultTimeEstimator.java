package edu.uw.modelab.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.uw.modelab.pojo.BusPosition;
import edu.uw.modelab.pojo.Projected;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.StopAggregate;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.service.TimeEstimator;
import edu.uw.modelab.utils.Mercator;
import edu.uw.modelab.utils.Utils;

public class DefaultTimeEstimator implements TimeEstimator {

	@Override
	public void estimateArrivalTime(final List<BusPosition> busPositions,
			final Trip trip) {
		final List<Projected<BusPosition>> projectedBusPositions = projectBusPositions(busPositions);
		final Set<Segment> segments = trip.getSegments();
		for (final Segment segment : segments) {
			final Stop to = segment.getTo();
			final Projected<Stop> projectedStop = projectStop(to);
			estimateTime(projectedStop, projectedBusPositions);
		}
		final List<StopAggregate> list = new ArrayList<>();
		for (final BusPosition busPosition : busPositions) {
		}

	}

	private Projected<Stop> projectStop(final Stop to) {
		final double y = Mercator.lat2y(to.getLat());
		final double x = Mercator.lon2x(to.getLon());
		return new Projected<Stop>(to, x, y);
	}

	private List<Projected<BusPosition>> projectBusPositions(
			final List<BusPosition> busPositions) {
		final List<Projected<BusPosition>> result = new ArrayList<>();
		for (final BusPosition bp : busPositions) {
			final double x = Mercator.lat2y(0);
			final double y = Mercator.lon2x(0);
			result.add(new Projected<BusPosition>(bp, x, y));
		}
		return result;
	}

	private long estimateTime(final Projected<Stop> to,
			final List<Projected<BusPosition>> busPositions) {
		final double toX = to.getX();
		final double toY = to.getY();
		final List<IndexedDistance> distances = new ArrayList<>();
		int i = 0;
		for (final Projected<BusPosition> bp : busPositions) {
			distances.add(new IndexedDistance(i++, Utils.euclideanDistance(
					bp.getX(), toX, bp.getY(), toY)));
		}
		Collections.sort(distances);
		final Projected<BusPosition> closest = busPositions.get(distances
				.get(0).index);
		final Projected<BusPosition> secondClosest = busPositions.get(distances
				.get(1).index);

		long t1;
		long t2;
		double weightNumerator;
		final double weightDenominator;
		if (closest.getBase().getTimeStamp() < secondClosest.getBase()
				.getTimeStamp()) {
			t1 = closest.getBase().getTimeStamp();
			t2 = secondClosest.getBase().getTimeStamp();
			weightNumerator = distances.get(0).distance;
		} else {
			t1 = secondClosest.getBase().getTimeStamp();
			t2 = closest.getBase().getTimeStamp();
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
