package edu.uw.modelab.dao;

import java.util.List;

import edu.uw.modelab.pojo.Route;
import edu.uw.modelab.pojo.Trip;

public interface RouteDao {

	List<Trip> getTripsById(int routeId);

	List<Route> getAllRoutes();

}
