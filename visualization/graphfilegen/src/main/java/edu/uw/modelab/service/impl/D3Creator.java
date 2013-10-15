package edu.uw.modelab.service.impl;

import java.io.PrintWriter;

public abstract class D3Creator extends AbstractFileCreator {

	public D3Creator(final String filename) {
		super(filename);
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
