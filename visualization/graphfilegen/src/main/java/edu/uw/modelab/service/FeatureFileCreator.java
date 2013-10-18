package edu.uw.modelab.service;

public interface FeatureFileCreator {

	void createFeatures(int tripId);

	void createFeatureLabels(final int tripId);

}
