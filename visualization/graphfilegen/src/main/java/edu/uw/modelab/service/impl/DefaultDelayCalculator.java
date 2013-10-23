package edu.uw.modelab.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.TripInstance;
import edu.uw.modelab.service.DelayCalculator;
import edu.uw.modelab.service.TimeEstimator;
import edu.uw.modelab.utils.Utils;

public class DefaultDelayCalculator implements DelayCalculator {

	private static final Logger LOG = LoggerFactory
			.getLogger(DefaultDelayCalculator.class);

	private final TimeEstimator timeEstimator;

	public DefaultDelayCalculator(final TimeEstimator timeEstimator) {
		this.timeEstimator = timeEstimator;
	}

	@Override
	public long calculateDelay(final Segment segment,
			final TripInstance tripInstance) {
		final Stop from = segment.getFrom();
		final String scheduledFrom = from.getStopTime().getSchedArrivalTime();
		final Stop to = segment.getTo();
		final String scheduledTo = to.getStopTime().getSchedArrivalTime();
		final long scheduled = Utils.diff(scheduledTo, scheduledFrom);
		final long actual = timeEstimator.actualDiff(tripInstance, segment);
		final long delay = scheduled - actual;
		// LOG.info("Segment {} arrived " + (delay < 0 ? "late" : "before")
		// + " {} seconds", segment.name(), delay);
		return delay;
	}

}
