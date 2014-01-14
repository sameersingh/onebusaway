package edu.uw.modelab.filter.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.filter.Filter;
import edu.uw.modelab.pojo.TripInstance;

public class RealtimePositionsSizeFilterImpl implements
		Filter<Set<TripInstance>> {

	private static final Logger LOG = LoggerFactory
			.getLogger(RealtimePositionsSizeFilterImpl.class);

	private static final int DEFAULT_MIN_SIZE = 10; // positions

	private final int minSize;

	public RealtimePositionsSizeFilterImpl() {
		this(DEFAULT_MIN_SIZE);
	}

	public RealtimePositionsSizeFilterImpl(final int minSize) {
		this.minSize = minSize;
	}

	@Override
	public Set<TripInstance> filter(final Set<TripInstance> instances) {
		final Set<TripInstance> newInstances = new LinkedHashSet<>();
		if (!instances.isEmpty()) {
			for (final TripInstance instance : instances) {
				if (instance.getRealtimes().size() >= minSize) {
					newInstances.add(instance);
				}
			}
			LOG.debug("removed {} instances",
					instances.size() - newInstances.size());
		}
		return newInstances;
	}

}
