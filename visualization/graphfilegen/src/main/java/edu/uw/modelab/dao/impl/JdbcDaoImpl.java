package edu.uw.modelab.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.uw.modelab.dao.Dao;
import edu.uw.modelab.dao.populators.AbstractPopulator;
import edu.uw.modelab.pojo.BusPosition;
import edu.uw.modelab.pojo.Stop;

public class JdbcDaoImpl implements Dao {

	private static final String SELECT_ALL_STOPS = "select id, name, lat, lon from stop";
	private static final String SELECT_ALL_BUS_POSITIONS = "select trip_id, service_date, lat, lon from bus_position";
	private static final String ORDER_BY_TIMESTAMP = " order by timestamp";
	private static final String SELECT_STOPS_PER_ROUTE = "select r.name, s.id, t.id from stop AS s "
			+ "join stop_time AS st on s.id = st.stop_id "
			+ "join trip AS t on t.id = st.trip_id "
			+ "join route AS r on r.id = t.route_id "
			+ "order by t.id, st.stop_sequence";

	private final JdbcTemplate template;
	private final List<AbstractPopulator> populators;

	private static final RowMapper<Stop> STOP_ROW_MAPPER = new StopRowMapper();
	private static final RowMapper<BusPosition> BUS_POSITION_ROW_MAPPER = new BusPositionRowMapper();

	public JdbcDaoImpl(final DataSource dataSource,
			final List<AbstractPopulator> populators) {
		this.template = new JdbcTemplate(dataSource);
		this.populators = populators;
	}

	public void init() {
		for (final AbstractPopulator populator : populators) {
			populator.populate();
		}
	}

	@Override
	public List<Stop> getStops() {
		return template.query(SELECT_ALL_STOPS, STOP_ROW_MAPPER);
	}

	@Override
	public Map<String, List<Integer>> getStopIdsPerRoute() {
		final Map<String, List<Integer>> result = new HashMap<String, List<Integer>>();
		template.query(SELECT_STOPS_PER_ROUTE, new RouteStopsMapper(result));
		return result;
	}

	@Override
	public List<BusPosition> getBusPositions() {
		return template
				.query(SELECT_ALL_BUS_POSITIONS, BUS_POSITION_ROW_MAPPER);
	}

	private static final class BusPositionRowMapper implements
			RowMapper<BusPosition> {

		@Override
		public BusPosition mapRow(final ResultSet rs, final int idx)
				throws SQLException {
			final BusPosition bp = new BusPosition();
			bp.setTripId(rs.getInt(1));
			bp.setServiceDate(rs.getLong(2));
			bp.setLat(rs.getString(3));
			bp.setLon(rs.getString(4));
			return bp;
		}

	}

	private static final class StopRowMapper implements RowMapper<Stop> {
		@Override
		public Stop mapRow(final ResultSet rs, final int idx)
				throws SQLException {
			return new Stop(rs.getInt(1), rs.getString(2), rs.getString(3),
					rs.getString(4));
		}
	}

	private static final class RouteStopsMapper implements RowMapper<Object> {

		private final Map<String, List<Integer>> stopIdsPerRoute;
		private final Map<String, Integer> routeTrip = new HashMap<String, Integer>();

		public RouteStopsMapper(final Map<String, List<Integer>> map) {
			this.stopIdsPerRoute = map;
		}

		@Override
		public Object mapRow(final ResultSet rs, final int index)
				throws SQLException {
			// ugly stuff, just because we are getting the results per all the
			// trips in the route
			// cutting that out just get the the results for one trip per route.
			// Assumption, the same trips use the same route, although I think
			// that might not happen
			final String routeName = rs.getString(1);
			final int stopId = rs.getInt(2);
			final int tripId = rs.getInt(3);
			if (!stopIdsPerRoute.containsKey(routeName)) {
				final List<Integer> stopIds = new ArrayList<Integer>();
				stopIds.add(stopId);
				stopIdsPerRoute.put(routeName, stopIds);
				routeTrip.put(routeName, tripId);
			} else if (stopIdsPerRoute.containsKey(routeName)
					&& (routeTrip.get(routeName) == tripId)) {
				stopIdsPerRoute.get(routeName).add(stopId);
			}
			return null;
		}

	}
}
