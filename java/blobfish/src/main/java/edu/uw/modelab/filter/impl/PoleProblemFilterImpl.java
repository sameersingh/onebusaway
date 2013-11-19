package edu.uw.modelab.filter.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.filter.Filter;
import edu.uw.modelab.pojo.RealtimePosition;

public class PoleProblemFilterImpl implements Filter<List<RealtimePosition>> {

	private static final Logger LOG = LoggerFactory
			.getLogger(PoleProblemFilterImpl.class);

	private static final int DEFAULT_MAX_OFFSET = 300; // meters
	private static final int DEFAULT_MIN_OFFSET = 100; // meters

	private final int maxOffset;
	private final int minOffset;

	public PoleProblemFilterImpl() {
		this(DEFAULT_MAX_OFFSET, DEFAULT_MIN_OFFSET);
	}

	public PoleProblemFilterImpl(final int maxOffset, final int minOffset) {
		this.maxOffset = maxOffset;
		this.minOffset = minOffset;
	}

	@Override
	public List<RealtimePosition> filter(final List<RealtimePosition> positions) {
		if (positions.isEmpty()) {
			return positions;
		}

		// assuming already ordered
		final List<RealtimePosition> filtered = new ArrayList<>();
		final RealtimePosition last = positions.get(positions.size() - 1);
		final double approxTripDistance = last.getDistanceAlongTrip();
		final double max = approxTripDistance - maxOffset;
		final double min = minOffset;
		for (final RealtimePosition position : positions) {
			final double distanceAlongTrip = position.getDistanceAlongTrip();
			if ((distanceAlongTrip < min) || (distanceAlongTrip > max)) {
				continue;
			}
			filtered.add(position);
		}
		LOG.debug("original positions {} - filtered positions {}",
				positions.size(), filtered.size());
		return filtered;
	}
}
