package edu.uw.modelab.service;

import java.util.List;

public interface FileCreator {

	void create();

	void createForTrip(int tripId);

	void createForTrips(List<Integer> tripIds);

	void createForTripInstance(int tripId, long serviceDate);

}
