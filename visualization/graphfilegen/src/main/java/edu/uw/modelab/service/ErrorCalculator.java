package edu.uw.modelab.service;

public interface ErrorCalculator {

	void calculateError(int tripId);

	void calculateError(int tripId, int poi, int from);

}
