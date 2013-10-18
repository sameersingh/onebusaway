package edu.uw.modelab.service;

import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.pojo.TripInstance;

public interface TimeEstimator {

	void estimateArrivalTimes(Trip trip);

	long actualDiff(TripInstance tripInstance, Segment segment);

}
