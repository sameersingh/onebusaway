package edu.uw.modelab.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

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
			+ " from trip_instance order by service_date, timestamp limit 60000";

	private static final String SELECT_TRIP_INSTANCES_IN = "select timestamp, service_date, trip_id, lat, lon, y, x, distance_trip, sched_deviation"
			+ " from trip_instance where trip_id in (:tripIds) order by service_date, timestamp";

	private static final String SELECT_TRIP_INSTANCES_BY_TRIP_ID_AND_SERVICE_DATE_LESS = "select timestamp, service_date, trip_id, lat, lon, y, x, distance_trip, sched_deviation"
			+ " from trip_instance"
			+ " where trip_id = ? and service_date < ?"
			+ " order by service_date, timestamp";

	private static final String SELECT_TRIP_INSTANCES_BY_TRIP_ID_AND_SERVICE_FROM = "select timestamp, service_date, trip_id, lat, lon, y, x, distance_trip, sched_deviation"
			+ " from trip_instance"
			+ " where trip_id = ? and service_date >= ?"
			+ " order by service_date, timestamp";

	private final JdbcTemplate template;
	private final NamedParameterJdbcTemplate namedTemplate;

	public JdbcTripInstanceDao(final DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
		this.namedTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public List<TripInstance> getTripInstancesForTripId(final int tripId) {
		final Map<String, TripInstance> tripInstances = new LinkedHashMap<>();
		template.query(SELECT_TRIP_INSTANCES_BY_TRIP_ID,
				new Object[] { tripId }, new TripInstanceRowMapper(
						tripInstances));
		final List<TripInstance> result = new ArrayList<>();
		final Iterator<Entry<String, TripInstance>> it = tripInstances
				.entrySet().iterator();
		while (it.hasNext()) {
			result.add(it.next().getValue());
		}
		return result;
	}

	@Override
	public TripInstance getTripInstance(final int tripId, final long serviceDate) {
		final Map<String, TripInstance> tripInstances = new LinkedHashMap<>();
		template.query(SELECT_TRIP_INSTANCE_BY_TRIP_ID_AND_SERVICE_DATE,
				new Object[] { tripId, serviceDate },
				new TripInstanceRowMapper(tripInstances));
		return tripInstances.entrySet().iterator().next().getValue();
	}

	private static final class TripInstanceRowMapper implements
			RowMapper<TripInstance> {

		private final Map<String, TripInstance> tripInstances;

		public TripInstanceRowMapper(
				final Map<String, TripInstance> tripInstances) {
			this.tripInstances = tripInstances;
		}

		@Override
		public TripInstance mapRow(final ResultSet rs, final int idx)
				throws SQLException {
			final long serviceDate = rs.getLong(2);
			final int tripId = rs.getInt(3);
			final String key = new TripInstance(serviceDate, tripId).getId();
			TripInstance tripInstance = tripInstances.get(key);
			if (tripInstance == null) {
				tripInstance = new TripInstance(serviceDate, tripId);
				tripInstances.put(key, tripInstance);
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
		final Map<String, TripInstance> tripInstances = new LinkedHashMap<>();
		template.query(SELECT_TRIP_INSTANCES, new TripInstanceRowMapper(
				tripInstances));
		final List<TripInstance> result = new ArrayList<>();
		final Iterator<Entry<String, TripInstance>> it = tripInstances
				.entrySet().iterator();
		while (it.hasNext()) {
			result.add(it.next().getValue());
		}
		return result;
	}

	@Override
	public List<TripInstance> getTripInstancesForTripIdAndServiceDateLessThan(
			final int tripId, final long serviceDate) {
		final Map<String, TripInstance> tripInstances = new LinkedHashMap<>();
		template.query(SELECT_TRIP_INSTANCES_BY_TRIP_ID_AND_SERVICE_DATE_LESS,
				new Object[] { tripId, serviceDate },
				new TripInstanceRowMapper(tripInstances));
		final List<TripInstance> result = new ArrayList<>();
		final Iterator<Entry<String, TripInstance>> it = tripInstances
				.entrySet().iterator();
		while (it.hasNext()) {
			result.add(it.next().getValue());
		}
		return result;
	}

	@Override
	public List<TripInstance> getTripInstancesForTripIdAndServiceDateFrom(
			final int tripId, final long serviceDate) {
		final Map<String, TripInstance> tripInstances = new LinkedHashMap<>();
		template.query(SELECT_TRIP_INSTANCES_BY_TRIP_ID_AND_SERVICE_FROM,
				new Object[] { tripId, serviceDate },
				new TripInstanceRowMapper(tripInstances));
		final List<TripInstance> result = new ArrayList<>();
		final Iterator<Entry<String, TripInstance>> it = tripInstances
				.entrySet().iterator();
		while (it.hasNext()) {
			result.add(it.next().getValue());
		}
		return result;
	}

	@Override
	public List<TripInstance> getTripInstancesForTripIds(
			final List<Integer> tripIds) {
		final MapSqlParameterSource parameters = new MapSqlParameterSource(
				"tripIds", tripIds);
		final Map<String, TripInstance> tripInstances = new LinkedHashMap<>();
		namedTemplate.query(SELECT_TRIP_INSTANCES_IN, parameters,
				new TripInstanceRowMapper(tripInstances));
		final List<TripInstance> result = new ArrayList<>();
		final Iterator<Entry<String, TripInstance>> it = tripInstances
				.entrySet().iterator();
		while (it.hasNext()) {
			result.add(it.next().getValue());
		}
		return result;
	}

	private static final class TripInstanceLimitedRowMapper implements
			RowMapper<TripInstance> {

		private final Map<Integer, Map<Long, TripInstance>> tripInstances;
		private final int max;

		public TripInstanceLimitedRowMapper(
				final Map<Integer, Map<Long, TripInstance>> tripInstances,
				final int max) {
			this.tripInstances = tripInstances;
			this.max = max;
		}

		@Override
		public TripInstance mapRow(final ResultSet rs, final int idx)
				throws SQLException {
			final long serviceDate = rs.getLong(2);
			final int tripId = rs.getInt(3);
			final RealtimePosition rp = new RealtimePosition(rs.getLong(1),
					rs.getDouble(8), rs.getDouble(9), rs.getDouble(4),
					rs.getDouble(5), rs.getDouble(6), rs.getDouble(7));
			Map<Long, TripInstance> tripInstanceMap = tripInstances.get(tripId);
			if (tripInstanceMap == null) {
				tripInstanceMap = new HashMap<>();
				final TripInstance tripInstance = new TripInstance(serviceDate,
						tripId);
				tripInstance.addRealtime(rp);
				tripInstanceMap.put(serviceDate, tripInstance);
				tripInstances.put(tripId, tripInstanceMap);
			} else {
				TripInstance tripInstance = tripInstanceMap.get(serviceDate);
				if (tripInstance == null) {
					if (tripInstanceMap.size() < max) {
						tripInstance = new TripInstance(serviceDate, tripId);
						tripInstance.addRealtime(rp);
						tripInstanceMap.put(serviceDate, tripInstance);
					}
				} else {
					tripInstance.addRealtime(rp);
				}
			}
			return null;
		}
	}

	@Override
	public List<TripInstance> getTripInstancesForTripIds(
			final List<Integer> tripIds, final int max) {
		final MapSqlParameterSource parameters = new MapSqlParameterSource(
				"tripIds", tripIds);
		final Map<Integer, Map<Long, TripInstance>> tripInstances = new LinkedHashMap<>();
		namedTemplate.query(SELECT_TRIP_INSTANCES_IN, parameters,
				new TripInstanceLimitedRowMapper(tripInstances, max));
		final List<TripInstance> result = new ArrayList<>();
		final Iterator<Entry<Integer, Map<Long, TripInstance>>> it = tripInstances
				.entrySet().iterator();
		while (it.hasNext()) {
			final Map<Long, TripInstance> map = it.next().getValue();
			result.addAll(map.values());
		}
		return result;
	}
}
