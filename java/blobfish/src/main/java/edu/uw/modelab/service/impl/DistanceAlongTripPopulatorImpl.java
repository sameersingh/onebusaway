package edu.uw.modelab.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.filter.Filter;
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

	private final Filter<List<RealtimePosition>> poleProblemFilter;
	private final Filter<Set<TripInstance>> rtpSizeFilter;
	private final boolean filter;

	public DistanceAlongTripPopulatorImpl(
			final Filter<List<RealtimePosition>> poleProblemFilter,
			final Filter<Set<TripInstance>> rtpSizeFilter, final boolean filter) {
		this.poleProblemFilter = poleProblemFilter;
		this.rtpSizeFilter = rtpSizeFilter;
		this.filter = filter;
	}

	@Override
	public Trip getTripWithDistancesAlongTrip(final Trip trip) {
		final Trip clone = new Trip(trip);
		if (filter) {
			doFilter(clone);
		}
		final Set<TripInstance> instances = clone.getInstances();
		if (!instances.isEmpty()) {
			final Set<Segment> segments = clone.getSegments();
			for (final Segment segment : segments) {
				addDistanceAlongTripToStopsInSegment(segment, instances);
			}
		}
		return clone;
	}

	private void doFilter(final Trip trip) {
		final Set<TripInstance> instances = trip.getInstances();
		for (final TripInstance tripInstance : instances) {
			tripInstance.setRealtimes(poleProblemFilter.filter(tripInstance
					.getRealtimes()));
		}
		trip.setInstances(rtpSizeFilter.filter(trip.getInstances()));
	}

	private void addDistanceAlongTripToStopsInSegment(final Segment segment,
			final Set<TripInstance> instances) {
		if (segment.isFirst()) {
			segment.getFrom().setDistanceAlongTrip(0);
			addBasedOnDistanceAlongTrip(segment.getTo(), instances);
		} else {
			addBasedOnDistanceAlongTrip(segment.getFrom(), instances);
			addBasedOnDistanceAlongTrip(segment.getTo(), instances);
		}
	}

	private void addBasedOnDistanceAlongTrip(final Stop stop,
			final Set<TripInstance> instances) {
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
