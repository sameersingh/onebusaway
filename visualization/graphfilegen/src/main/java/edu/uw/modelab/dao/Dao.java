package edu.uw.modelab.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uw.modelab.pojo.BusPosition;
import edu.uw.modelab.pojo.Link;
import edu.uw.modelab.pojo.Stop;

public interface Dao {

	List<Stop> getStops();

	Map<String, Set<Link>> getLinksPerRoute();

	List<BusPosition> getBusPositions();

	Map<String, Integer> getStopsPerRoute();

}
