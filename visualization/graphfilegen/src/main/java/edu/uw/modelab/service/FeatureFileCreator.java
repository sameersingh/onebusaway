package edu.uw.modelab.service;

import java.util.List;

public interface FeatureFileCreator {

	void createFeatures(int tripId);

	void createFeatureLabels(final int tripId);

	void createFeatures(List<Integer> tripIds);

	void createFeatureLabels(final List<Integer> tripIds);

}
