package edu.uw.modelab.service;

import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.TripInstance;

public interface TimeEstimator {

	long getActualTimesDiff(TripInstance tripInstance, Segment segment);

	long getActualTime(final TripInstance tripInstance, final Stop stop);

	long getDelay(Segment segment, TripInstance tripInstance);

}
