package edu.uw.modelab.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.uw.modelab.dao.RouteDao;
import edu.uw.modelab.dao.TripInstanceDao;
import edu.uw.modelab.pojo.Route;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.StopTime;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.pojo.TripInstance;

public class JdbcRouteDao implements RouteDao {

	private final JdbcTemplate template;
	private final TripInstanceDao tripInstanceDao;

	private static final String SELECT_ROUTE_BY_ID = "select r.name, r.agency_id, t.id"
			+ " t.headsign, s.id, s.name, s.lat, s.lon, s.y, s.x, st.arrival_time,"
			+ " st.departure_time, st.stop_sequence from route as r"
			+ " join trip as t on r.id = t.route_id"
			+ " join stop_time as st on t.id = st.trip_id"
			+ " join stop as s on s.id = st.stop_id "
			+ " where r.id = ?"
			+ " order by r.name, t.id, st.stop_sequence";

	private static final String SELECT_ROUTES = "select r.id, r.name, r.agency_id, t.id,"
			+ " t.headsign, s.id, s.name, s.lat, s.lon, s.y, s.x, st.arrival_time,"
			+ " st.departure_time, st.stop_sequence from route as r"
			+ " join trip as t on r.id = t.route_id"
			+ " join stop_time as st on t.id = st.trip_id"
			+ " join stop as s on s.id = st.stop_id "
			+ " order by r.name, t.id, st.stop_sequence";

	public JdbcRouteDao(final DataSource dataSource,
			final TripInstanceDao tripInstanceDao) {
		this.template = new JdbcTemplate(dataSource);
		this.tripInstanceDao = tripInstanceDao;
	}

	@Override
	@Deprecated
	public Route getRouteById(final int routeId) {
		final Route route = new Route(routeId);
		final List<Trip> trips = new ArrayList<>();
		final Map<Integer, List<Stop>> stopsPerTrip = new LinkedHashMap<>();
		template.query(SELECT_ROUTE_BY_ID, new Object[] { routeId },
				new RouteRowsMapper(route, trips, stopsPerTrip));
		final Iterator<Entry<Integer, List<Stop>>> stopsPerTripIt = stopsPerTrip
				.entrySet().iterator();
		while (stopsPerTripIt.hasNext()) {
			final Entry<Integer, List<Stop>> stopsPerTripEntry = stopsPerTripIt
					.next();
			final Trip trip = trips.get(trips.indexOf(new Trip(
					stopsPerTripEntry.getKey())));
			final List<Stop> stops = stopsPerTripEntry.getValue();
			for (int i = 0; i < (stops.size() - 1); i++) {
				final Segment segment = new Segment(stops.get(i),
						stops.get(i + 1));
				if (i == 0) {
					segment.setFirst(true);
				}
				trip.addSegment(segment);
			}
			route.addTrip(trip);
		}
		return route;
	}

	@Override
	public Set<Route> getRoutes() {
		return getRoutes(true);

	}

	private Set<Route> getRoutes(final boolean full) {
		final Set<Route> routes = new HashSet<>();
		final Map<Integer, List<Trip>> tripsPerRoute = new LinkedHashMap<>();
		final Map<Integer, List<Stop>> stopsPerTrip = new LinkedHashMap<>();
		template.query(SELECT_ROUTES, new RoutesRowsMapper(routes,
				tripsPerRoute, stopsPerTrip));
		for (final Route route : routes) {
			final List<Trip> trips = tripsPerRoute.get(route.getId());
			for (final Trip trip : trips) {
				final List<Stop> stops = stopsPerTrip.get(trip.getId());
				for (int i = 0; i < (stops.size() - 1); i++) {
					final Segment segment = new Segment(stops.get(i),
							stops.get(i + 1));
					if (i == 0) {
						segment.setFirst(true);
					}
					trip.addSegment(segment);
				}
				if (full) {
					final List<TripInstance> tripInstances = tripInstanceDao
							.getTripInstancesForTripId(trip.getId());
					for (final TripInstance tripInstance : tripInstances) {
						trip.addInstance(tripInstance);
					}
				}
				route.addTrip(trip);
			}
		}
		return routes;
	}

	@Override
	public Set<Route> getRoutesIncomplete() {
		return getRoutes(false);
	}

	private static final class RouteRowsMapper implements RowMapper<Object> {

		private final List<Trip> trips;
		private final Route route;
		private final Map<Integer, List<Stop>> stopsPerTrip;

		public RouteRowsMapper(final Route route, final List<Trip> trips,
				final Map<Integer, List<Stop>> stopsPerTrip) {
			this.route = route;
			this.trips = trips;
			this.stopsPerTrip = stopsPerTrip;
		}

		@Override
		public Object mapRow(final ResultSet rs, final int index)
				throws SQLException {
			if (route.getName() == null) {
				route.setName(rs.getString(1));
			}
			if (route.getAgencyId() == null) {
				route.setAgencyId(rs.getString(2));
			}
			final int tripId = rs.getInt(3);
			final Trip trip = new Trip(tripId);
			if (!trips.contains(trip)) {
				final String headSign = rs.getString(4);
				trip.setHeadsign(headSign);
				trips.add(trip);
			}

			final int stopId = rs.getInt(5);
			final String stopName = rs.getString(6);
			final double lat = rs.getDouble(7);
			final double lon = rs.getDouble(8);
			final double y = rs.getDouble(9);
			final double x = rs.getDouble(10);
			final Stop stop = new Stop(stopId, stopName, lat, lon, y, x);
			final String arrivalTime = rs.getString(11);
			final String departureTime = rs.getString(12);
			final int stopSequence = rs.getInt(13);
			final StopTime stopTime = new StopTime(arrivalTime, departureTime,
					stopSequence);
			stop.setStopTime(stopTime);

			List<Stop> stops = stopsPerTrip.get(tripId);
			if (stops == null) {
				stops = new ArrayList<>();
				stops.add(stop);
				stopsPerTrip.put(tripId, stops);
			} else if (!stops.contains(stop)) {
				stops.add(stop);
			}
			return null;
		}
	}

	private static final class RoutesRowsMapper implements RowMapper<Object> {

		private final Map<Integer, List<Trip>> tripsPerRoute;
		private final Set<Route> routes;
		private final Map<Integer, List<Stop>> stopsPerTrip;

		public RoutesRowsMapper(final Set<Route> routes,
				final Map<Integer, List<Trip>> tripsPerRoute,
				final Map<Integer, List<Stop>> stopsPerTrip) {
			this.routes = routes;
			this.tripsPerRoute = tripsPerRoute;
			this.stopsPerTrip = stopsPerTrip;
		}

		@Override
		public Object mapRow(final ResultSet rs, final int index)
				throws SQLException {

			final int routeId = rs.getInt(1);
			final Route route = new Route(routeId);
			if (!routes.contains(route)) {
				route.setName(rs.getString(2));
				route.setAgencyId(rs.getString(3));
				routes.add(route);
			}

			final int tripId = rs.getInt(4);
			final String headSign = rs.getString(5);
			final Trip trip = new Trip(tripId);
			trip.setHeadsign(headSign);
			List<Trip> trips = tripsPerRoute.get(routeId);
			if (trips == null) {
				trips = new ArrayList<>();
				trips.add(trip);
				tripsPerRoute.put(routeId, trips);
			} else if (!trips.contains(trip)) {
				trips.add(trip);
			}

			final int stopId = rs.getInt(6);
			final String stopName = rs.getString(7);
			final double lat = rs.getDouble(8);
			final double lon = rs.getDouble(9);
			final double y = rs.getDouble(10);
			final double x = rs.getDouble(11);
			final Stop stop = new Stop(stopId, stopName, lat, lon, y, x);
			final String arrivalTime = rs.getString(12);
			final String departureTime = rs.getString(13);
			final int stopSequence = rs.getInt(14);
			final StopTime stopTime = new StopTime(arrivalTime, departureTime,
					stopSequence);
			stop.setStopTime(stopTime);

			List<Stop> stops = stopsPerTrip.get(tripId);
			if (stops == null) {
				stops = new ArrayList<>();
				stops.add(stop);
				stopsPerTrip.put(tripId, stops);
			} else if (!stops.contains(stop)) {
				stops.add(stop);
			}
			return null;
		}
	}

}
