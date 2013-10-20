package edu.uw.modelab.service;


public interface ErrorCalculator {

	void calculateError(int tripId);

	void calculateError(final int tripId, int k);

}
