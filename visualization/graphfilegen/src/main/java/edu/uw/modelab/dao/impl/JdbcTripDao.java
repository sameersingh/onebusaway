package edu.uw.modelab.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.uw.modelab.dao.TripDao;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Trip;

public class JdbcTripDao implements TripDao {

	private final JdbcTemplate template;

	public JdbcTripDao(final DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public Trip getTripById(final int tripId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Trip> getAllTrips() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Segment> getTripSegmentsById(final int tripId) {
		// TODO Auto-generated method stub
		return null;
	}

}
