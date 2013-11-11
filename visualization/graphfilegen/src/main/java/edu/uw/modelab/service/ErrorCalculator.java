package edu.uw.modelab.service;

import java.util.List;

import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.TripInstance;

public interface ErrorCalculator {

	void calculateObaAndModeError(final List<Integer> tripIds, int k);

	void calculateScheduledError(int tripId);

	// visualization purposes... need to update this
	long[] getObaAndModeErrors(TripInstance tripInstance, Segment segment);

}
