package edu.uw.modelab.service;

import java.util.List;

import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.TripInstance;

public interface ErrorCalculator {

	// move to timeService
	void calculateTimeBetweenStops(int tripId);

	void calculateObaAndModeError(int tripId, int k, Error error);

	void calculateObaAndModeError(final List<Integer> tripIds, int k);

	void calculateScheduledError(int tripId, Error error);

	long[] getObaAndModeErrors(TripInstance tripInstance, Segment segment);

}
