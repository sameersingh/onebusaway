package edu.uw.modelab.service.impl;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.uw.modelab.dao.Dao;
import edu.uw.modelab.pojo.Stop;

public class DotCreator extends AbstractFileCreator {

	private final Dao dao;

	public DotCreator(final String filename, final Dao dao) {
		super(filename);
		this.dao = dao;
	}

	@Override
	protected void beginning(final PrintWriter writer) {
		writer.println("digraph oba {");
	}

	@Override
	protected void addNodes(final PrintWriter pw) {
		final List<Stop> stops = dao.getStops();
		for (final Stop stop : stops) {
			pw.println(stop.getId() + " [label=\"" + stop.getName() + "\"];");
		}
	}

	@Override
	protected void addEdges(final PrintWriter pw) {
		final Map<String, List<Integer>> stopIdsPerRoute = dao
				.getStopIdsPerRoute();
		for (final Map.Entry<String, List<Integer>> entry : stopIdsPerRoute
				.entrySet()) {
			final List<Integer> stopIds = entry.getValue();
			final Iterator<Integer> it = stopIds.iterator();
			while (it.hasNext()) {
				pw.print(it.next());
				if (it.hasNext()) {
					pw.print(" -> ");
				}
			}
			pw.println(" [label=\"" + entry.getKey() + "\"];");
		}
	}

	@Override
	protected void end(final PrintWriter writer) {
		writer.println("}");
	}
}
