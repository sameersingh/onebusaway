package edu.uw.modelab.service;

import java.util.List;

public interface FeatureFileCreator {

	void createFeatures();

	void createFeatures(int tripId);

	void createFeatures(List<Integer> tripIds);

}
