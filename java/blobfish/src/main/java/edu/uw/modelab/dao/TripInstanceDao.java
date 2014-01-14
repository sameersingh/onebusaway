package edu.uw.modelab.dao;

import java.util.List;
import java.util.Map;

import edu.uw.modelab.pojo.TripInstance;

public interface TripInstanceDao {

	List<TripInstance> getTripInstancesForTripId(int tripId);

	List<TripInstance> getTripInstances();

	List<TripInstance> getTripInstancesForTripIds(List<Integer> tripIds);

	TripInstance getTripInstance(int tripId, long serviceDate);

	List<TripInstance> getTripInstancesForTripIdAndServiceDateLessThan(
			int tripId, long serviceDate);

	List<TripInstance> getTripInstancesForTripIdAndServiceDateFrom(int tripId,
			long serviceDate);

	List<TripInstance> getTripInstancesForTripIds(List<Integer> tripIds, int max);

	Map<Long, Double> getAvgScheduleErrorPerServiceDate();

	Map<Long, Integer> getTimestampAndSchedDevation();

}
