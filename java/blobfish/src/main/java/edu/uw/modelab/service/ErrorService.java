package edu.uw.modelab.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uw.modelab.error.pojo.Dataset;
import edu.uw.modelab.error.pojo.RootMeanSquareError;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.pojo.TripInstance;

public interface ErrorService {

	Map<Dataset, RootMeanSquareError> getErrors(final List<Integer> tripIds,
			int k);

	Map<Dataset, RootMeanSquareError> getErrors(final Set<Trip> tripIds, int k);

	Map<Dataset, Double> getScheduledError(int tripId);

	// visualization purposes... need to update this
	long[] getObaAndModeErrors(TripInstance tripInstance, Segment segment);

}
