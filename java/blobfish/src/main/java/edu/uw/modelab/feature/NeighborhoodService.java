package edu.uw.modelab.feature;

import java.util.List;

import edu.uw.modelab.feature.pojo.Neighborhood;

public interface NeighborhoodService {

	List<Neighborhood> getNeighborhoods();

	Neighborhood getNeighborhoodByName(String name);

	List<String> getNeighborhoodNames();

}
