package edu.uw.modelab.visualization;

import java.util.List;

/*
 * TODO REFACTOR
 */
public interface VisualizationFileCreator {

	void create();

	void createForTrip(int tripId);

	void createForTrips(List<Integer> tripIds);

	void createForTripInstance(int tripId, long serviceDate);

}
