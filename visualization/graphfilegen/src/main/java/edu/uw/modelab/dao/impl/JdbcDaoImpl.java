package edu.uw.modelab.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

import edu.uw.modelab.dao.Dao;
import edu.uw.modelab.dao.populators.AbstractPopulator;
import edu.uw.modelab.pojo.BusPosition;
import edu.uw.modelab.pojo.Link;
import edu.uw.modelab.pojo.Stop;

public class JdbcDaoImpl implements Dao {

	private static final String SELECT_ALL_STOPS = "select id, name, lat, lon from stop";
	private static final String SELECT_ALL_BUS_POSITIONS = "select trip_id, service_date, lat, lon from bus_position limit 20000";
	private static final String ORDER_BY_TIMESTAMP = " order by timestamp";
	private static final String SELECT_STOPS_PER_ROUTE = "select r.name, s.id, t.id from stop AS s "
			+ "join stop_time AS st on s.id = st.stop_id "
			+ "join trip AS t on t.id = st.trip_id "
			+ "join route AS r on r.id = t.route_id "
			// + "where r.name = '98' "
			+ "order by t.id, st.stop_sequence";

	private static final String SELECT_NUMBER_STOPS_PER_ROUTE = "select r.name, count(distinct s.id) as stop_counts from stop as s "
			+ "join stop_time as st on s.id = st.stop_id "
			+ "join trip as t on t.id = st.trip_id "
			+ "join route as r on r.id = t.route_id group by r.name order by stop_counts";

	// select count (distinct stop_id) from stop_time

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
	public Map<String, Set<Link>> getLinksPerRoute() {
		final Map<String, Set<Link>> linksPerRoute = new HashMap<>();
		final Map<String, Map<Integer, List<Integer>>> stopIdsPerTripPerRoute = new LinkedHashMap<>();
		template.query(SELECT_STOPS_PER_ROUTE, new RouteStopsMapper(
				stopIdsPerTripPerRoute));
		final Iterator<Entry<String, Map<Integer, List<Integer>>>> it = stopIdsPerTripPerRoute
				.entrySet().iterator();
		while (it.hasNext()) {
			final Entry<String, Map<Integer, List<Integer>>> entry = it.next();
			final Map<Integer, List<Integer>> stopIdsPerTrip = entry.getValue();
			final Iterator<Entry<Integer, List<Integer>>> innerIt = stopIdsPerTrip
					.entrySet().iterator();
			final String routeName = entry.getKey();
			Set<Link> links = linksPerRoute.get(routeName);
			if (links == null) {
				links = new HashSet<>();
				linksPerRoute.put(routeName, links);
			}
			while (innerIt.hasNext()) {
				final Entry<Integer, List<Integer>> stopIdsEntry = innerIt
						.next();
				final List<Integer> stopIds = stopIdsEntry.getValue();
				for (int i = 0; i < (stopIds.size() - 1); i++) {
					final Link link = new Link(stopIds.get(i),
							stopIds.get(i + 1));
					links.add(link);
				}
			}
		}
		return linksPerRoute;
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

		private final Map<String, Map<Integer, List<Integer>>> stopIdsPerTripPerRoute;

		public RouteStopsMapper(
				final Map<String, Map<Integer, List<Integer>>> map) {
			this.stopIdsPerTripPerRoute = map;
		}

		@Override
		public Object mapRow(final ResultSet rs, final int index)
				throws SQLException {
			final String routeName = rs.getString(1);
			final int stopId = rs.getInt(2);
			final int tripId = rs.getInt(3);
			if (!stopIdsPerTripPerRoute.containsKey(routeName)) {
				final Map<Integer, List<Integer>> stopIdsPerTrip = new HashMap<>();
				final List<Integer> stopIds = new ArrayList<>();
				stopIdsPerTrip.put(tripId, stopIds);
				stopIdsPerTripPerRoute.put(routeName, stopIdsPerTrip);
			} else {
				final Map<Integer, List<Integer>> stopIdsPerTrip = stopIdsPerTripPerRoute
						.get(routeName);
				List<Integer> stopIds = stopIdsPerTrip.get(tripId);
				if (stopIds == null) {
					stopIds = new ArrayList<>();
					stopIds.add(stopId);
					stopIdsPerTrip.put(tripId, stopIds);
					stopIdsPerTripPerRoute.put(routeName, stopIdsPerTrip);
				} else {
					stopIds.add(stopId);
					stopIdsPerTrip.put(tripId, stopIds);
				}
			}
			return null;
		}
	}

	@Override
	public Map<String, Integer> getStopsPerRoute() {
		final Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		template.query(SELECT_NUMBER_STOPS_PER_ROUTE,
				new NumberStopsPerRouteRowMapper(result));
		return result;
	}

	private static final class NumberStopsPerRouteRowMapper implements
			RowMapper<Object> {

		private final Map<String, Integer> result;

		public NumberStopsPerRouteRowMapper(final Map<String, Integer> result) {
			this.result = result;
		}

		@Override
		public Object mapRow(final ResultSet rs, final int idx)
				throws SQLException {
			result.put(rs.getString(1), rs.getInt(2));
			return null;
		}
	}
}
