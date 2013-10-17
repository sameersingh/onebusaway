package edu.uw.modelab.service;

import edu.uw.modelab.pojo.Trip;

public interface TimeEstimator {

	void estimateArrivalTimes(Trip trip);

}
