package edu.uw.modelab.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.uw.modelab.dao.RouteDao;
import edu.uw.modelab.pojo.Route;
import edu.uw.modelab.pojo.Trip;

public class JdbcRouteDao implements RouteDao {

	private final JdbcTemplate template;

	public JdbcRouteDao(final DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Trip> getTripsById(final int routeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Route> getAllRoutes() {
		// TODO Auto-generated method stub
		return null;
	}

}
