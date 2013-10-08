package edu.uw.modelab.service.impl;

import edu.uw.modelab.dao.Dao;
import edu.uw.modelab.service.FileCreator;

public class DotCreator implements FileCreator {

	private final Dao dao;

	public DotCreator(final Dao dao) {
		this.dao = dao;
	}

	@Override
	public void create() {
		// TODO Auto-generated method stub

	}

}
