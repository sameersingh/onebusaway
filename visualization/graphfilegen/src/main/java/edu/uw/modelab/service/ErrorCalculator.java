package edu.uw.modelab.service;

import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.TripInstance;

public interface ErrorCalculator {

	// move to timeService
	void calculateTimeBetweenStops(int tripId);

	void calculateObaAndModeError(final int tripId, int k, Error error);

	void calculateScheduledError(int tripId, Error error);

	long[] getObaAndModeErrors(TripInstance tripInstance, Segment segment);

}
