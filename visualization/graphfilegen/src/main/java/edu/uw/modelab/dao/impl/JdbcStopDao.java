package edu.uw.modelab.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.uw.modelab.dao.StopDao;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.StopTime;

public class JdbcStopDao implements StopDao {

	private static Logger LOG = LoggerFactory.getLogger(JdbcStopDao.class);

	private static final String SELECT_ALL_STOPS = "select id, name, lat, lon from stop";
	private static final String SELECT_STOP_BY_STOP_ID = "select id, name, lat, lon from stop where id=?";
	private static final String SELECT_STOPS_BY_TRIP_ID = "select s.id, s.name, s.lat, s.lon, st.arrival_time,"
			+ " st.departure_time, st.stop_sequence from stop as s "
			+ "join stop_time as st on s.id = st.stop_id "
			+ "join trip as t on t.id = st.trip_id where t.id = ?";
	private static final String SELECT_NUMBER_STOPS_PER_ROUTE = "select r.name, count(distinct s.id) as stop_counts from stop as s "
			+ "join stop_time as st on s.id = st.stop_id "
			+ "join trip as t on t.id = st.trip_id "
			+ "join route as r on r.id = t.route_id group by r.name order by stop_counts";

	private static final String SELECT_STOPS_PER_ROUTE = "select r.name, s.id, t.id from stop AS s "
			+ "join stop_time AS st on s.id = st.stop_id "
			+ "join trip AS t on t.id = st.trip_id "
			+ "join route AS r on r.id = t.route_id "
			+ "order by t.id, st.stop_sequence";

	private final JdbcTemplate template;

	private static final RowMapper<Stop> STOP_INCOMPLETE_ROW_MAPPER = new StopIncompleteRowMapper();
	private static final RowMapper<Stop> STOP_COMPLETE_ROW_MAPPER = new StopCompleteRowMapper();

	public JdbcStopDao(final DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Stop> getStops() {
		return template.query(SELECT_ALL_STOPS, STOP_INCOMPLETE_ROW_MAPPER);
	}

	@Override
	public Stop getStopById(final int stopId) {
		return template.queryForObject(SELECT_STOP_BY_STOP_ID,
				new Object[] { stopId }, STOP_INCOMPLETE_ROW_MAPPER);
	}

	@Override
	public Map<String, Integer> getNumberOfStopsPerRoute() {
		final Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		template.query(SELECT_NUMBER_STOPS_PER_ROUTE,
				new NumberStopsPerRouteRowMapper(result));
		return result;
	}

	@Override
	public List<Stop> getStopsByTripId(final int tripId) {
		return template.query(SELECT_STOPS_BY_TRIP_ID, new Object[] { tripId },
				STOP_COMPLETE_ROW_MAPPER);
	}

	private static final class StopIncompleteRowMapper implements
			RowMapper<Stop> {
		@Override
		public Stop mapRow(final ResultSet rs, final int idx)
				throws SQLException {
			return new Stop(rs.getInt(1), rs.getString(2), rs.getDouble(3),
					rs.getDouble(4));
		}
	}

	private static final class StopCompleteRowMapper implements RowMapper<Stop> {

		@Override
		public Stop mapRow(final ResultSet rs, final int idx)
				throws SQLException {
			final StopTime st = new StopTime(rs.getString(5), rs.getString(6),
					rs.getInt(7));
			final Stop stop = new Stop(rs.getInt(1), rs.getString(2),
					rs.getDouble(3), rs.getDouble(4));
			stop.setStopTime(st);
			return stop;
		}
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

	/*
	 * @Override public Map<String, Set<Segment>> getLinksPerRoute() { final
	 * Map<String, Set<Segment>> linksPerRoute = new HashMap<>(); final
	 * Map<String, Map<Integer, List<Integer>>> stopIdsPerTripPerRoute = new
	 * LinkedHashMap<>(); template.query(SELECT_STOPS_PER_ROUTE, new
	 * RouteStopsMapper( stopIdsPerTripPerRoute)); final Iterator<Entry<String,
	 * Map<Integer, List<Integer>>>> it = stopIdsPerTripPerRoute
	 * .entrySet().iterator(); while (it.hasNext()) { final Entry<String,
	 * Map<Integer, List<Integer>>> entry = it.next(); final Map<Integer,
	 * List<Integer>> stopIdsPerTrip = entry.getValue(); final
	 * Iterator<Entry<Integer, List<Integer>>> innerIt = stopIdsPerTrip
	 * .entrySet().iterator(); final String routeName = entry.getKey();
	 * Set<Segment> links = linksPerRoute.get(routeName); if (links == null) {
	 * links = new HashSet<>(); linksPerRoute.put(routeName, links); } while
	 * (innerIt.hasNext()) { final Entry<Integer, List<Integer>> stopIdsEntry =
	 * innerIt .next(); final List<Integer> stopIds = stopIdsEntry.getValue();
	 * for (int i = 0; i < (stopIds.size() - 1); i++) {
	 * 
	 * final Segment link = new Segment(stopIds.get(i), stopIds.get(i + 1));
	 * 
	 * links.add(null); } } } return linksPerRoute; }
	 */

	/*
	 * private static final class RouteStopsMapper implements RowMapper<Object>
	 * {
	 * 
	 * private final Map<String, Map<Integer, List<Integer>>>
	 * stopIdsPerTripPerRoute;
	 * 
	 * public RouteStopsMapper( final Map<String, Map<Integer, List<Integer>>>
	 * map) { this.stopIdsPerTripPerRoute = map; }
	 * 
	 * @Override public Object mapRow(final ResultSet rs, final int index)
	 * throws SQLException { final String routeName = rs.getString(1); final int
	 * stopId = rs.getInt(2); final int tripId = rs.getInt(3); if
	 * (!stopIdsPerTripPerRoute.containsKey(routeName)) { final Map<Integer,
	 * List<Integer>> stopIdsPerTrip = new HashMap<>(); final List<Integer>
	 * stopIds = new ArrayList<>(); stopIdsPerTrip.put(tripId, stopIds);
	 * stopIdsPerTripPerRoute.put(routeName, stopIdsPerTrip); } else { final
	 * Map<Integer, List<Integer>> stopIdsPerTrip = stopIdsPerTripPerRoute
	 * .get(routeName); List<Integer> stopIds = stopIdsPerTrip.get(tripId); if
	 * (stopIds == null) { stopIds = new ArrayList<>(); stopIds.add(stopId);
	 * stopIdsPerTrip.put(tripId, stopIds);
	 * stopIdsPerTripPerRoute.put(routeName, stopIdsPerTrip); } else {
	 * stopIds.add(stopId); stopIdsPerTrip.put(tripId, stopIds); } } return
	 * null; } }
	 */

}
