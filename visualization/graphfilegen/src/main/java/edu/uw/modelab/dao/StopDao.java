package edu.uw.modelab.dao;

import java.util.List;
import java.util.Map;

import edu.uw.modelab.pojo.Stop;

public interface StopDao {

	List<Stop> getStops();

	Stop getStopById(int stopId);

	List<Stop> getStopsByTripId(int tripId);

	Map<String, Integer> getNumberOfStopsPerRoute();

}
