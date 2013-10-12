package edu.uw.modelab.service.impl;

import java.io.PrintWriter;

import edu.uw.modelab.dao.Dao;

public abstract class D3JSCreator extends AbstractFileCreator {

	private final Dao dao;

	public D3JSCreator(final String filename, final Dao dao) {
		super(filename);
		this.dao = dao;
	}

	protected Dao getDao() {
		return dao;
	}

	@Override
	protected void beginning(final PrintWriter writer) {
		writer.print("{");
	}

	@Override
	protected void end(final PrintWriter writer) {
		writer.print("}");

	}

}
