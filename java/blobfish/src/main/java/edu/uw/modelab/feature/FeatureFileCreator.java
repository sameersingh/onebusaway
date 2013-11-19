package edu.uw.modelab.feature;

import java.util.List;

public interface FeatureFileCreator {

	void createFeatures();

	void createFeatures(int tripId);

	void createFeatures(List<Integer> tripIds);

}
