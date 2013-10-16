package edu.uw.modelab.dao;

import java.util.Set;

import edu.uw.modelab.pojo.Route;

public interface RouteDao {

	Route getRouteById(int routeId);

	Set<Route> getRoutes();

}
