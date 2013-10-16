package edu.uw.modelab.service;

import java.util.List;

import edu.uw.modelab.pojo.BusPosition;
import edu.uw.modelab.pojo.Trip;

public interface TimeEstimator {

	void estimateArrivalTime(List<BusPosition> busPositions, Trip trip);

}
