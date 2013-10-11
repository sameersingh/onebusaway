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
	private final Map<Long, Integer> stopIdsIndexes;

	public D3JSCreator(final String filename, final Dao dao) {
		super(filename);
		this.dao = dao;
		stopIdsIndexes = new HashMap<Long, Integer>(8110);
	}

	@Override
	protected void beginning(final PrintWriter writer) {
		writer.print("{\"config\": {\"bounds\": { \"minx\": 100.0, \"maxx\": 105.0, \"miny\": 0.0, \"maxy\": 2.0 }, \"routes\": { \"min\": 1, \"max\": 10 }},");
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
		final Map<String, List<Long>> stopIdsPerRoute = dao
				.getStopIdsPerRoute();
		final Set<Entry<String, List<Long>>> entrySet = stopIdsPerRoute
				.entrySet();
		final Iterator<Entry<String, List<Long>>> it = entrySet.iterator();
		while (it.hasNext()) {
			final Entry<String, List<Long>> entry = it.next();
			final List<Long> stopIds = entry.getValue();
			for (int i = 0; i < (stopIds.size() - 1); i++) {
				final Integer source = stopIdsIndexes.get(stopIds.get(i));
				final Integer target = stopIdsIndexes.get(stopIds.get(++i));
				final StringBuilder sb = new StringBuilder("{\"source\":")
						.append(source)
						.append(",\"target\":")
						.append(target)
						.append(",\"value\":3,\"group\":1,\"name\":\"")
						.append(entry.getKey())
						.append("\",\"details\":\"Long description of Segment\"}");
				if (i != (stopIds.size() - 1)) {
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
