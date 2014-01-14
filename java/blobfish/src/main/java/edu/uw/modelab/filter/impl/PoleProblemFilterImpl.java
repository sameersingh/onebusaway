package edu.uw.modelab.filter.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.filter.Filter;
import edu.uw.modelab.pojo.RealtimePosition;

public class PoleProblemFilterImpl implements Filter<List<RealtimePosition>> {

	private static final Logger LOG = LoggerFactory
			.getLogger(PoleProblemFilterImpl.class);

	private static final int DEFAULT_MAX_DISTANCE_OFFSET = 300; // meters
	private static final int DEFAULT_MIN_DISTANCE_OFFSET = 100; // meters
	private static final int DEFAULT_MAX_SCHEDULE_DEVIATION = 2000; // seconds
	private static final int DEFAULT_MIN_SCHEDULE_DEVIATION = -2000; // seconds

	private final int maxDistanceOffset;
	private final int minDistanceOffset;
	private final int maxScheduleDeviation;
	private final int minScheduleDeviation;

	public PoleProblemFilterImpl() {
		this(DEFAULT_MAX_DISTANCE_OFFSET, DEFAULT_MIN_DISTANCE_OFFSET,
				DEFAULT_MAX_SCHEDULE_DEVIATION, DEFAULT_MIN_SCHEDULE_DEVIATION);
	}

	public PoleProblemFilterImpl(final int maxDistanceOffset,
			final int minDistanceOffset, final int maxSchedDeviation,
			final int minSchedDeviation) {
		this.maxDistanceOffset = maxDistanceOffset;
		this.minDistanceOffset = minDistanceOffset;
		this.maxScheduleDeviation = maxSchedDeviation;
		this.minScheduleDeviation = minSchedDeviation;
	}

	@Override
	public List<RealtimePosition> filter(final List<RealtimePosition> positions) {
		if (positions.isEmpty()) {
			return positions;
		}

		// already ordered by timestamp, but there's some noise in the data...
		// last timestamps can contain low distance along trips, which doesn't
		// make sense.
		final List<RealtimePosition> filtered = new ArrayList<>();
		final double maxDistanceAlongTrip = getMaxDistanceAlongTrip(positions);
		final double max = maxDistanceAlongTrip - maxDistanceOffset;
		final double min = minDistanceOffset;
		for (final RealtimePosition position : positions) {
			final double distanceAlongTrip = position.getDistanceAlongTrip();
			final double scheduleDeviation = position.getSchedDev();
			if ((distanceAlongTrip < min) || (distanceAlongTrip > max)
					|| (scheduleDeviation > maxScheduleDeviation)
					|| (scheduleDeviation < minScheduleDeviation)) {
				continue;
			}
			filtered.add(position);
		}
		LOG.debug("original positions {} - filtered positions {}",
				positions.size(), filtered.size());
		return filtered;
	}

	private double getMaxDistanceAlongTrip(
			final List<RealtimePosition> positions) {
		final List<RealtimePosition> filtered = new ArrayList<>(positions);
		Collections.sort(filtered, new Comparator<RealtimePosition>() {

			@Override
			public int compare(final RealtimePosition o1,
					final RealtimePosition o2) {
				return Double.valueOf(o1.getDistanceAlongTrip()).compareTo(
						o2.getDistanceAlongTrip());
			}
		});
		return filtered.get(filtered.size() - 1).getDistanceAlongTrip();
	}
}
