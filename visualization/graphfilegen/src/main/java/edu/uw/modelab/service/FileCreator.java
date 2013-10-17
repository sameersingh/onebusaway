package edu.uw.modelab.service;

public interface FileCreator {

	void create();

	void createForTrip(int tripId);

	void createForTripInstance(int tripId, long serviceDate);

}
