package edu.uw.modelab.dao;

import java.util.List;

import edu.uw.modelab.pojo.TripInstance;

public interface TripInstanceDao {

	List<TripInstance> getTripInstancesForTripId(int tripId);

	List<TripInstance> getTripInstances();

	TripInstance getTripInstance(int tripId, long serviceDate);

	List<TripInstance> getTripInstancesForTripIdAndServiceDateLessThan(
			int tripId, long serviceDate);

}
