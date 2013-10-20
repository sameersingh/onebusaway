package edu.uw.modelab.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.uw.modelab.dao.TripDao;
import edu.uw.modelab.dao.TripInstanceDao;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.StopTime;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.pojo.TripInstance;

public class JdbcTripDao implements TripDao {

	private final JdbcTemplate template;
	private final TripInstanceDao tripInstanceDao;

	private static final String SELECT_TRIP_BY_ID = "select t.headsign, s.id, s.name, s.lat, s.lon, s.y, s.x, st.arrival_time,"
			+ " st.departure_time, st.stop_sequence from trip as t "
			+ "join stop_time as st on t.id = st.trip_id "
			+ "join stop as s on st.stop_id = s.id "
			+ "where t.id =? "
			+ "order by st.stop_sequence";

	private static final String SELECT_NUMBER_TRIPS_PER_STOP = "select s.id, count(distinct t.id) as trip_counts from trip as t"
			+ " join stop_time as st on st.trip_id = t.id"
			+ " join stop as s on st.stop_id = s.id"
			+ " group by s.id"
			+ " order by trip_counts";

	public JdbcTripDao(final DataSource dataSource,
			final TripInstanceDao tripInstanceDao) {
		this.template = new JdbcTemplate(dataSource);
		this.tripInstanceDao = tripInstanceDao;
	}

	@Override
	public Trip getTripById(final int tripId) {
		final Trip trip = new Trip(tripId);
		final List<Stop> stops = new ArrayList<>();
		template.query(SELECT_TRIP_BY_ID, new Object[] { tripId },
				new TripRowMapper(trip, stops));
		for (int i = 0; i < (stops.size() - 1); i++) {
			final Segment segment = new Segment(stops.get(i), stops.get(i + 1));
			if (i == 0) {
				segment.setFirst(true);
			}
			trip.addSegment(segment);
		}
		final List<TripInstance> tripInstances = tripInstanceDao
				.getTripInstancesForTripId(tripId);
		for (final TripInstance tripInstance : tripInstances) {
			trip.addInstance(tripInstance);
		}
		return trip;
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
					rs.getDouble(4), rs.getDouble(5), rs.getDouble(6),
					rs.getDouble(7));
			final StopTime st = new StopTime(rs.getString(8), rs.getString(9),
					rs.getInt(10));
			stop.setStopTime(st);
			stops.add(stop);
			return null;
		}
	}

	@Override
	public Map<Integer, Integer> getNumberOfTripsPerStop() {
		final Map<Integer, Integer> result = new LinkedHashMap<>();
		template.query(SELECT_NUMBER_TRIPS_PER_STOP,
				new NumberTripsPerStopRowMapper(result));
		return result;
	}

	private static final class NumberTripsPerStopRowMapper implements
			RowMapper<Object> {

		private final Map<Integer, Integer> result;

		public NumberTripsPerStopRowMapper(final Map<Integer, Integer> result) {
			this.result = result;
		}

		@Override
		public Object mapRow(final ResultSet rs, final int idx)
				throws SQLException {
			result.put(rs.getInt(1), rs.getInt(2));
			return null;
		}
	}

	@Override
	public Trip getTripByIdAndServiceDateLessThan(final int tripId,
			final long serviceDate) {
		final Trip trip = new Trip(tripId);
		final List<Stop> stops = new ArrayList<>();
		template.query(SELECT_TRIP_BY_ID, new Object[] { tripId },
				new TripRowMapper(trip, stops));
		for (int i = 0; i < (stops.size() - 1); i++) {
			final Segment segment = new Segment(stops.get(i), stops.get(i + 1));
			if (i == 0) {
				segment.setFirst(true);
			}
			trip.addSegment(segment);
		}
		final List<TripInstance> tripInstances = tripInstanceDao
				.getTripInstancesForTripIdAndServiceDateLessThan(tripId,
						serviceDate);
		for (final TripInstance tripInstance : tripInstances) {
			trip.addInstance(tripInstance);
		}
		return trip;
	}

	@Override
	public Trip getTripByIdAndServiceDateFrom(final int tripId,
			final long serviceDate) {
		final Trip trip = new Trip(tripId);
		final List<Stop> stops = new ArrayList<>();
		template.query(SELECT_TRIP_BY_ID, new Object[] { tripId },
				new TripRowMapper(trip, stops));
		for (int i = 0; i < (stops.size() - 1); i++) {
			final Segment segment = new Segment(stops.get(i), stops.get(i + 1));
			if (i == 0) {
				segment.setFirst(true);
			}
			trip.addSegment(segment);
		}
		final List<TripInstance> tripInstances = tripInstanceDao
				.getTripInstancesForTripIdAndServiceDateFrom(tripId,
						serviceDate);
		for (final TripInstance tripInstance : tripInstances) {
			trip.addInstance(tripInstance);
		}
		return trip;
	}

}
