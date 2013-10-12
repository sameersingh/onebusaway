package edu.uw.modelab.service.impl;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.uw.modelab.dao.Dao;
import edu.uw.modelab.pojo.Stop;

public class D3JSCreator extends AbstractFileCreator {

	private final Dao dao;
	private final Map<Integer, Integer> stopIdsIndexes;

	public D3JSCreator(final String filename, final Dao dao) {
		super(filename);
		this.dao = dao;
		stopIdsIndexes = new HashMap<Integer, Integer>(8110);
	}

	@Override
	protected void beginning(final PrintWriter writer) {
		writer.print("{");
	}

	@Override
	protected void addNodes(final PrintWriter writer) {
		writer.print("\"nodes\": [");
		final List<Stop> stops = dao.getStops();
		final Iterator<Stop> it = stops.iterator();
		int index = 0;
		while (it.hasNext()) {
			final Stop stop = it.next();
			stopIdsIndexes.put(stop.getId(), index++);
			final StringBuilder sb = new StringBuilder("{\"name\":\"")
					.append(stop.getName())
					.append("\",\"group\":2,\"coords\":{\"type\": \"Point\",\"coordinates\":[")
					.append(stop.getLon()).append(",").append(stop.getLat())
					.append("]},\"details\":\"update long desc\"}");
			if (it.hasNext()) {
				sb.append(",");
			}
			writer.print(sb.toString());
		}
		writer.print("],");

	}

	@Override
	protected void addEdges(final PrintWriter writer) {
		writer.print("\"links\":[");
		final Map<String, List<Integer>> stopIdsPerRoute = dao
				.getStopIdsPerRoute();
		final Set<Entry<String, List<Integer>>> entrySet = stopIdsPerRoute
				.entrySet();
		final Iterator<Entry<String, List<Integer>>> it = entrySet.iterator();
		while (it.hasNext()) {
			final Entry<String, List<Integer>> entry = it.next();
			final List<Integer> stopIds = entry.getValue();
			for (int i = 0; i < (stopIds.size() - 1); i++) {
				final Integer source = stopIdsIndexes.get(stopIds.get(i));
				final Integer target = stopIdsIndexes.get(stopIds.get(i + 1));
				final StringBuilder sb = new StringBuilder("{\"source\":")
						.append(source)
						.append(",\"target\":")
						.append(target)
						.append(",\"value\":3,\"group\":1,\"name\":\"")
						.append(entry.getKey())
						.append("\",\"details\":\"Long description of Segment\"}");
				if ((i + 1) != (stopIds.size() - 1)) {
					sb.append(",");
				}
				writer.print(sb.toString());
			}
			if (it.hasNext()) {
				writer.print(",");
			}
		}
		writer.print("]");
	}

	@Override
	protected void end(final PrintWriter writer) {
		writer.print("}");

	}

}
