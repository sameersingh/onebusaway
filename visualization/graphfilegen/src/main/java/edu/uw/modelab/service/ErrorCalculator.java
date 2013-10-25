package edu.uw.modelab.service;

public interface ErrorCalculator {

	// move to timeService
	void calculateTimeBetweenStops(int tripId);

	void calculateObaAndModeError(final int tripId, int k, Error error);

	void calculateScheduledError(int tripId);

}
