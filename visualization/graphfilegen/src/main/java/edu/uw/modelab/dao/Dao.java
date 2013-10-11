package edu.uw.modelab.dao;

import java.util.List;
import java.util.Map;

import edu.uw.modelab.pojo.Stop;

public interface Dao {

	List<Stop> getStops();

	Map<String, List<Long>> getStopIdsPerRoute();

}
