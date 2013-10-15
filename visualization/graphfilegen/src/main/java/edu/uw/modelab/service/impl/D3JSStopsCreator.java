package edu.uw.modelab.service.impl;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.uw.modelab.dao.Dao;
import edu.uw.modelab.pojo.Link;
import edu.uw.modelab.pojo.Stop;

public class D3JSStopsCreator extends D3JSCreator {

	private final Map<Integer, Integer> stopIdsIndexes;

	public D3JSStopsCreator(final String filename, final Dao dao) {
		super(filename, dao);
		stopIdsIndexes = new HashMap<Integer, Integer>(8110);
	}

	@Override
	protected void addNodes(final PrintWriter writer) {
		writer.print("\"nodes\": [");
		final List<Stop> stops = getDao().getStops();
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
		final Map<String, Set<Link>> linksPerRoute = getDao()
				.getLinksPerRoute();
		writer.print("\"links\":[");
		final Iterator<Entry<String, Set<Link>>> it = linksPerRoute.entrySet()
				.iterator();
		while (it.hasNext()) {
			final Entry<String, Set<Link>> entry = it.next();
			final Iterator<Link> linksIt = entry.getValue().iterator();
			while (linksIt.hasNext()) {
				final Link link = linksIt.next();
				final Integer source = stopIdsIndexes.get(link.getFrom());
				final Integer target = stopIdsIndexes.get(link.getTo());
				final StringBuilder sb = new StringBuilder("{\"source\":")
						.append(source)
						.append(",\"target\":")
						.append(target)
						.append(",\"value\":3,\"group\":1,\"name\":\"")
						.append(entry.getKey())
						.append("\",\"details\":\"Long description of Segment\"}");
				if (linksIt.hasNext()) {
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

}
