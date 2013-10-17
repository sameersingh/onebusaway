package edu.uw.modelab.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.uw.modelab.dao.TripInstanceDao;
import edu.uw.modelab.pojo.TripInstance;

public class JdbcTripInstanceDao implements TripInstanceDao {

	private static final String SELECT_TRIP_INSTANCES_BY_TRIP_ID = "select timestamp, service_date, trip_id, lat, lon, y, x, distance_trip"
			+ " from trip_instance"
			+ " where trip_id = ?"
			+ " order by timestamp";

	private static final String SELECT_TRIP_INSTANCES = "select timestamp, service_date, trip_id, lat, lon, y, x, distance_trip"
			+ " from trip_instance order by timestamp limit 30000";

	private final JdbcTemplate template;

	private static final TripInstanceRowMapper TRIP_INSTANCE_ROW_MAPPER = new TripInstanceRowMapper();

	public JdbcTripInstanceDao(final DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public List<TripInstance> getTripInstancesForTripId(final int tripId) {
		return template.query(SELECT_TRIP_INSTANCES_BY_TRIP_ID,
				new Object[] { tripId }, TRIP_INSTANCE_ROW_MAPPER);
	}

	private static final class TripInstanceRowMapper implements
			RowMapper<TripInstance> {

		@Override
		public TripInstance mapRow(final ResultSet rs, final int idx)
				throws SQLException {
			final TripInstance ti = new TripInstance();
			ti.setTimeStamp(rs.getLong(1));
			ti.setServiceDate(rs.getLong(2));
			ti.setTripId(rs.getInt(3));
			ti.setLat(rs.getDouble(4));
			ti.setLon(rs.getDouble(5));
			ti.setY(rs.getDouble(6));
			ti.setX(rs.getDouble(7));
			ti.setDistanceAlongTrip(rs.getDouble(8));
			return ti;
		}
	}

	@Override
	public List<TripInstance> getTripInstances() {
		return template.query(SELECT_TRIP_INSTANCES, TRIP_INSTANCE_ROW_MAPPER);
	}

}
