package edu.uw.modelab.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import edu.uw.modelab.dao.BusPositionDao;
import edu.uw.modelab.pojo.BusPosition;

public class JdbcBusPositionDao implements BusPositionDao {

	private static final String SELECT_BUS_POSITION_BY_TRIP_ID = "select timestamp, service_date, trip_id, lat, lon, distance_trip"
			+ " from bus_position"
			+ " where trip_id = ?"
			+ " order by timestamp";

	private static final String SELECT_BUS_POSITIONS = "select timestamp, service_date, trip_id, lat, lon, distance_trip"
			+ " from bus_position order by timestamp limit 30000";

	private final JdbcTemplate template;

	private final BusPositionRowMapper BUS_POSITION_ROW_MAPPER = new BusPositionRowMapper();

	public JdbcBusPositionDao(final DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public List<BusPosition> getBusPositionsByTripId(final int tripId) {
		return template.query(SELECT_BUS_POSITION_BY_TRIP_ID,
				new Object[] { tripId }, BUS_POSITION_ROW_MAPPER);
	}

	private static final class BusPositionRowMapper implements
			RowMapper<BusPosition> {

		@Override
		public BusPosition mapRow(final ResultSet rs, final int idx)
				throws SQLException {
			final BusPosition bp = new BusPosition();
			bp.setTimeStamp(rs.getLong(1));
			bp.setServiceDate(rs.getLong(2));
			bp.setTripId(rs.getInt(3));
			bp.setLat(rs.getDouble(4));
			bp.setLon(rs.getDouble(5));
			bp.setDistanceAlongTrip(rs.getDouble(6));
			return bp;
		}
	}

	@Override
	public List<BusPosition> getBusPositions() {
		return template.query(SELECT_BUS_POSITIONS, BUS_POSITION_ROW_MAPPER);
	}

}
