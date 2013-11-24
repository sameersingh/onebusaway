package edu.uw.modelab.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uw.modelab.pojo.Trip;

public interface TripDao {

	Trip getTripById(int tripId);

	List<Integer> getTripIds();

	List<Integer> getTripIds(int amount);

	Trip getTripByIdAndServiceDateLessThan(int tripId, long serviceDate);

	Map<Integer, Integer> getNumberOfTripsPerStop();

	Trip getTripByIdAndServiceDateFrom(int tripId, long serviceDate);

	Set<Trip> getTrips();

	Set<Trip> getTripsIn(List<Integer> tripIds);

	Map<Integer, Integer> getTripRoutes(List<Integer> tripIds);
}
