package edu.uw.modelab.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.dao.Dao;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.service.FileCreator;

public class DotCreator implements FileCreator {

	private static final Logger LOG = LoggerFactory.getLogger(DotCreator.class);

	private final String filename;
	private final Dao dao;

	public DotCreator(final String filename, final Dao dao) {
		this.filename = filename;
		this.dao = dao;
	}

	@Override
	public void create() {
		PrintWriter pw = null;
		try {
			LOG.info("Creating dot file...");
			pw = new PrintWriter(filename, "UTF-8");
			pw.println("digraph oba {");
			addNodes(pw);
			addEdges(pw);
			pw.println("}");
			pw.flush();
			LOG.info("Dot file created ...");
		} catch (final IOException exc) {
			LOG.error("Error creating dot file. Msg {}", exc.getMessage());
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	private void addNodes(final PrintWriter pw) {
		final List<Stop> stops = dao.getStops();
		for (final Stop stop : stops) {
			pw.println(stop.getId() + " [label=\"" + stop.getName() + "\"];");
		}
	}

	private void addEdges(final PrintWriter pw) {
		final Map<String, List<Long>> stopIdsPerRoute = dao
				.getStopIdsPerRoute();
		for (final Map.Entry<String, List<Long>> entry : stopIdsPerRoute
				.entrySet()) {
			final List<Long> stopIds = entry.getValue();
			final Iterator<Long> it = stopIds.iterator();
			while (it.hasNext()) {
				pw.print(it.next());
				if (it.hasNext()) {
					pw.print(" -> ");
				}
			}
			pw.println(" [label=\"" + entry.getKey() + "\"];");
		}
	}
}
