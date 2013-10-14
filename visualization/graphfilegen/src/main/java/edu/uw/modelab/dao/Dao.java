package edu.uw.modelab.dao;

import java.util.List;
import java.util.Map;

import edu.uw.modelab.pojo.BusPosition;
import edu.uw.modelab.pojo.Stop;

public interface Dao {

	List<Stop> getStops();

	Map<String, List<Integer>> getStopIdsPerRoute();

	List<BusPosition> getBusPositions();

	Map<String, Integer> getStopsPerRoute();

}
