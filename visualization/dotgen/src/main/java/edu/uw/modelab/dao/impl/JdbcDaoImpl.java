package edu.uw.modelab.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import edu.uw.modelab.dao.Dao;
import edu.uw.modelab.dao.populators.AbstractPopulator;

public class JdbcDaoImpl implements Dao {

	private final JdbcTemplate template;
	private final List<AbstractPopulator> populators;

	public JdbcDaoImpl(final DataSource dataSource,
			final List<AbstractPopulator> populators) {
		this.template = new JdbcTemplate(dataSource);
		this.populators = populators;
	}

	public void init() {
		for (final AbstractPopulator populator : populators) {
			populator.populate();
		}
	}

}
