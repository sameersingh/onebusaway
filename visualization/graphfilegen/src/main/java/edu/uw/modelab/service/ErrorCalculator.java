package edu.uw.modelab.service;

import java.util.List;
import java.util.Set;

import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.pojo.TripInstance;

public interface ErrorCalculator {

	void calculateObaAndModeError(final List<Integer> tripIds, int k);

	void calculateObaAndModeError(final Set<Trip> tripIds, int k);

	void calculateScheduledError(int tripId);

	// visualization purposes... need to update this
	long[] getObaAndModeErrors(TripInstance tripInstance, Segment segment);

}
