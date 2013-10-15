package edu.uw.modelab.dao;

import java.util.List;
import java.util.Map;

import edu.uw.modelab.pojo.Stop;

public interface StopDao {

	List<Stop> getStops();

	Stop getStopById(int stopId);

	Map<String, Integer> getNumberOfStopsPerRoute();

}
