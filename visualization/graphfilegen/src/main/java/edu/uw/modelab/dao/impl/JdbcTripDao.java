package edu.uw.modelab.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.uw.modelab.dao.TripDao;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.StopTime;
import edu.uw.modelab.pojo.Trip;

public class JdbcTripDao implements TripDao {

	private final JdbcTemplate template;

	private static final String SELECT_TRIP_BY_ID = "select t.headsign, s.id, s.name, s.lat, s.lon, st.arrival_time,"
			+ " st.departure_time, st.stop_sequence from trip as t "
			+ "join stop_time as st on t.id = st.trip_id "
			+ "join stop as s on st.stop_id = s.id "
			+ "where t.id =? "
			+ "order by st.stop_sequence";

	public JdbcTripDao(final DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public Trip getTripById(final int tripId) {
		final Trip trip = new Trip(tripId);
		final List<Stop> stops = new ArrayList<>();
		template.query(SELECT_TRIP_BY_ID, new Object[] { tripId },
				new TripRowMapper(trip, stops));
		for (int i = 0; i < (stops.size() - 1); i++) {
			final Segment segment = new Segment(stops.get(i), stops.get(i + 1));
			trip.addSegment(segment);
		}
		return trip;
	}

	@Override
	public List<Trip> getTrips() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Segment> getTripSegmentsById(final int tripId) {
		// TODO Auto-generated method stub
		return null;
	}

	private static class TripRowMapper implements RowMapper<Object> {

		private final List<Stop> stops;
		private final Trip trip;

		public TripRowMapper(final Trip trip, final List<Stop> stops) {
			this.trip = trip;
			this.stops = stops;
		}

		@Override
		public Object mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			if (trip.getHeadSign() == null) {
				trip.setHeadsign(rs.getString(1));
			}
			final Stop stop = new Stop(rs.getInt(2), rs.getString(3),
					rs.getDouble(4), rs.getDouble(5));
			final StopTime st = new StopTime(rs.getString(6), rs.getString(7),
					rs.getInt(8));
			stop.setStopTime(st);
			stops.add(stop);
			return null;
		}
	}

}
