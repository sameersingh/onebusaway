package edu.uw.modelab.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.uw.modelab.dao.TripInstanceDao;
import edu.uw.modelab.pojo.RealtimePosition;
import edu.uw.modelab.pojo.TripInstance;

public class JdbcTripInstanceDao implements TripInstanceDao {

	private static final String SELECT_TRIP_INSTANCES_BY_TRIP_ID = "select timestamp, service_date, trip_id, lat, lon, y, x, distance_trip, sched_deviation"
			+ " from trip_instance"
			+ " where trip_id = ?"
			+ " order by service_date, timestamp";

	private static final String SELECT_TRIP_INSTANCE_BY_TRIP_ID_AND_SERVICE_DATE = "select timestamp, service_date, trip_id, lat, lon, y, x, distance_trip, sched_deviation"
			+ " from trip_instance"
			+ " where trip_id = ?"
			+ " and service_date = ? order by service_date, timestamp";

	private static final String SELECT_TRIP_INSTANCES = "select timestamp, service_date, trip_id, lat, lon, y, x, distance_trip, sched_deviation"
			+ " from trip_instance order by service_date, timestamp limit 30000";

	private final JdbcTemplate template;

	public JdbcTripInstanceDao(final DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public List<TripInstance> getTripInstancesForTripId(final int tripId) {
		final Map<Long, TripInstance> tripInstances = new LinkedHashMap<>();
		template.query(SELECT_TRIP_INSTANCES_BY_TRIP_ID,
				new Object[] { tripId }, new TripInstanceRowMapper(
						tripInstances));
		final List<TripInstance> result = new ArrayList<>();
		final Iterator<Entry<Long, TripInstance>> it = tripInstances.entrySet()
				.iterator();
		while (it.hasNext()) {
			result.add(it.next().getValue());
		}
		return result;
	}

	@Override
	public TripInstance getTripInstance(final int tripId, final long serviceDate) {
		final Map<Long, TripInstance> tripInstances = new LinkedHashMap<>();
		template.query(SELECT_TRIP_INSTANCE_BY_TRIP_ID_AND_SERVICE_DATE,
				new Object[] { tripId, serviceDate },
				new TripInstanceRowMapper(tripInstances));
		return tripInstances.entrySet().iterator().next().getValue();
	}

	private static final class TripInstanceRowMapper implements
			RowMapper<TripInstance> {

		private final Map<Long, TripInstance> tripInstances;

		public TripInstanceRowMapper(final Map<Long, TripInstance> tripInstances) {
			this.tripInstances = tripInstances;
		}

		@Override
		public TripInstance mapRow(final ResultSet rs, final int idx)
				throws SQLException {
			final long serviceDate = rs.getLong(2);
			TripInstance tripInstance = tripInstances.get(serviceDate);
			if (tripInstance == null) {
				tripInstance = new TripInstance(serviceDate, rs.getInt(3));
				tripInstances.put(serviceDate, tripInstance);
			}
			final RealtimePosition rp = new RealtimePosition(rs.getLong(1),
					rs.getDouble(8), rs.getDouble(9), rs.getDouble(4),
					rs.getDouble(5), rs.getDouble(6), rs.getDouble(7));
			tripInstance.addRealtime(rp);
			return null;
		}
	}

	@Override
	public List<TripInstance> getTripInstances() {
		final Map<Long, TripInstance> tripInstances = new LinkedHashMap<>();
		template.query(SELECT_TRIP_INSTANCES, new TripInstanceRowMapper(
				tripInstances));
		final List<TripInstance> result = new ArrayList<>();
		final Iterator<Entry<Long, TripInstance>> it = tripInstances.entrySet()
				.iterator();
		while (it.hasNext()) {
			result.add(it.next().getValue());
		}
		return result;
	}

}
