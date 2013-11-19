package edu.uw.modelab.dao.impl;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.uw.modelab.dao.StopTimeDao;

public class JdbcStopTimeDao implements StopTimeDao {

	private final JdbcTemplate template;

	public JdbcStopTimeDao(final DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

}
