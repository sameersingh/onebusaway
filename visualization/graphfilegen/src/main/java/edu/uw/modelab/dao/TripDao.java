package edu.uw.modelab.dao;

import java.util.Map;

import edu.uw.modelab.pojo.Trip;

public interface TripDao {

	Trip getTripById(int tripId);

	Trip getTripByIdAndServiceDateLessThan(int tripId, long serviceDate);

	Map<Integer, Integer> getNumberOfTripsPerStop();

	Trip getTripByIdAndServiceDateFrom(int tripId, long serviceDate);
}
