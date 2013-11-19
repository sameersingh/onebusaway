package edu.uw.modelab.service.impl;

import java.util.ArrayList;
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
import edu.uw.modelab.service.DistanceAlongTripPopulator;
import edu.uw.modelab.utils.Utils;

public class DistanceAlongTripPopulatorImpl implements
		DistanceAlongTripPopulator {

	private static final Logger LOG = LoggerFactory
			.getLogger(DistanceAlongTripPopulatorImpl.class);

	@Override
	public void addDistancesAlongTrip(final Trip trip) {
		final Set<TripInstance> instances = trip.getInstances();
		if (!instances.isEmpty()) {
			final Set<Segment> segments = trip.getSegments();
			for (final Segment segment : segments) {
				addDistanceAlongTripToStopsInSegment(segment, instances);
			}
		}
	}

	private void addDistanceAlongTripToStopsInSegment(final Segment segment,
			final Set<TripInstance> instances) {
		if (segment.isFirst()) {
			segment.getFrom().setDistanceAlongTrip(0);
			addBasedOnDistanceAlongTrip(segment, instances);
		} else {
			// only need to calculate the to, the from is the previous to
			addBasedOnDistanceAlongTrip(segment, instances);
		}
	}

	private void addBasedOnDistanceAlongTrip(final Segment segment,
			final Set<TripInstance> instances) {
		final Stop stop = segment.getTo();
		final double toX = stop.getX();
		final double toY = stop.getY();
		final List<IndexedEuclideanDistance> euclideanDistances = new ArrayList<>();
		int indexesTripInstances = 0;
		final List<TripInstance> tripInstances = new ArrayList<>(instances);
		for (final TripInstance tripInstance : tripInstances) {
			final List<RealtimePosition> rtps = tripInstance.getRealtimes();
			int indexesRealtimePositions = 0;
			for (final RealtimePosition rtp : rtps) {
				euclideanDistances.add(new IndexedEuclideanDistance(
						indexesTripInstances, indexesRealtimePositions++, Utils
								.euclideanDistance(rtp.getX(), toX, rtp.getY(),
										toY)));
			}
			indexesTripInstances += 1;
		}
		Collections.sort(euclideanDistances);
		final IndexedEuclideanDistance closestIndexed = euclideanDistances
				.get(0);
		final IndexedEuclideanDistance secondClosestIndexed = euclideanDistances
				.get(1);
		final RealtimePosition closest = tripInstances.get(
				closestIndexed.indexTripInstace).getRealtime(
				closestIndexed.indexRealtimePosition);
		final RealtimePosition secondClosest = tripInstances.get(
				secondClosestIndexed.indexTripInstace).getRealtime(
				secondClosestIndexed.indexRealtimePosition);

		double di = 0;
		double dj = 0;
		double weightNumerator = 0;
		if (closest.getDistanceAlongTrip() < secondClosest
				.getDistanceAlongTrip()) {
			di = closest.getDistanceAlongTrip();
			dj = secondClosest.getDistanceAlongTrip();
			weightNumerator = closestIndexed.distance;
		} else {
			di = secondClosest.getDistanceAlongTrip();
			dj = closest.getDistanceAlongTrip();
			weightNumerator = secondClosestIndexed.distance;
		}
		final double weightDenominator = closestIndexed.distance
				+ secondClosestIndexed.distance;
		final long ds = (long) (di + (((dj - di) * weightNumerator) / weightDenominator));
		stop.setDistanceAlongTrip(ds);
		LOG.debug("di {} - ds {} - dj {}", di, ds, dj);

		assert stop.getDistanceAlongTrip() > segment.getFrom()
				.getDistanceAlongTrip();
	}

	private class IndexedEuclideanDistance implements
			Comparable<IndexedEuclideanDistance> {
		private final int indexTripInstace;
		private final int indexRealtimePosition;
		private final Double distance;

		IndexedEuclideanDistance(final int indexTripInstace,
				final int indexRealtimePosition, final Double distance) {
			this.indexTripInstace = indexTripInstace;
			this.indexRealtimePosition = indexRealtimePosition;
			this.distance = distance;
		}

		@Override
		public int compareTo(final IndexedEuclideanDistance other) {
			return distance.compareTo(other.distance);
		}

		@Override
		public String toString() {
			return "{indexTripInstace=" + indexTripInstace
					+ ", indexRealtimePosition=" + indexRealtimePosition
					+ ", distance=" + distance + "}";
		}
	}
}
