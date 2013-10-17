package edu.uw.modelab.dao;

import java.util.Map;

import edu.uw.modelab.pojo.Trip;

public interface TripDao {

	Trip getTripById(int tripId);

	Map<Integer, Integer> getNumberOfTripsPerStop();
}
