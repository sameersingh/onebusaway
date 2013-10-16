package edu.uw.modelab.dao;

import java.util.List;
import java.util.Map;

import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Trip;

public interface TripDao {

	Trip getTripById(int tripId);

	List<Trip> getTrips();

	List<Segment> getTripSegmentsById(int tripId);

	Map<Integer, Integer> getNumberOfTripsPerStop();
}
