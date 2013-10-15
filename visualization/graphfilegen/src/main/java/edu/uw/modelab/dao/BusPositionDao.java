package edu.uw.modelab.dao;

import java.util.List;

import edu.uw.modelab.pojo.BusPosition;

public interface BusPositionDao {

	List<BusPosition> getBusPositionsByTripId(int tripId);

	List<BusPosition> getBusPositions();

}
