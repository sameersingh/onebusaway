package edu.uw.modelab.dao;

import java.util.List;

import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Trip;

public interface TripDao {

	Trip getTripById(int tripId);

	List<Trip> getAllTrips();

	List<Segment> getTripSegmentsById(int tripId);
}
