package edu.uw.modelab.service;

import java.util.List;

import edu.uw.modelab.pojo.TripInstance;
import edu.uw.modelab.pojo.Trip;

public interface TimeEstimator {

	void estimateArrivalTime(List<TripInstance> busPositions, Trip trip);

}
