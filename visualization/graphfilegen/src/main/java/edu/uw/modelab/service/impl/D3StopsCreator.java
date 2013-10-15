package edu.uw.modelab.service.impl;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.uw.modelab.dao.StopDao;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;

public class D3StopsCreator extends D3Creator {

	private final Map<Integer, Integer> stopIdsIndexes;
	private StopDao stopDao;

	public D3StopsCreator(final String filename, final StopDao stopDao) {
		super(filename);
		stopIdsIndexes = new HashMap<Integer, Integer>(8110);
	}

	@Override
	protected void addNodes(final PrintWriter writer) {
		writer.print("\"nodes\": [");
		final List<Stop> stops = stopDao.getStops();
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
		/*final Map<String, Set<Segment>> linksPerRoute = getDao()
				.getLinksPerRoute();
		writer.print("\"links\":[");
		final Iterator<Entry<String, Set<Segment>>> it = linksPerRoute
				.entrySet().iterator();
		while (it.hasNext()) {
			final Entry<String, Set<Segment>> entry = it.next();
			final Iterator<Segment> linksIt = entry.getValue().iterator();
			while (linksIt.hasNext()) {
				final Segment link = linksIt.next();
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
		writer.print("]"); */
	}

}
