package edu.uw.modelab.service.impl;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.dao.RouteDao;
import edu.uw.modelab.dao.StopDao;
import edu.uw.modelab.pojo.Route;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.Trip;

public class D3StopsCreator extends D3Creator {

	private static final Logger LOG = LoggerFactory
			.getLogger(D3StopsCreator.class);

	private final Map<Integer, Integer> stopIdsIndexes;
	private final StopDao stopDao;
	private final RouteDao routeDao;

	public D3StopsCreator(final String filename, final StopDao stopDao,
			final RouteDao routeDao) {
		super(filename);
		this.stopDao = stopDao;
		this.routeDao = routeDao;
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
		final Set<Segment> addedSegments = new HashSet<>();
		final Set<Route> routes = routeDao.getRoutes();
		writer.print("\"links\":[");
		final Iterator<Route> routeIt = routes.iterator();
		while (routeIt.hasNext()) {
			final Route route = routeIt.next();
			final Set<Trip> trips = route.getTrips();
			final Iterator<Trip> tripIt = trips.iterator();
			while (tripIt.hasNext()) {
				final Trip trip = tripIt.next();
				final Set<Segment> segments = trip.getSegments();
				final Iterator<Segment> segmentIt = segments.iterator();
				while (segmentIt.hasNext()) {
					final Segment segment = segmentIt.next();
					if (addedSegments.contains(segment)) {
						LOG.info("Skipping segment, already added");
						continue;
					}
					addedSegments.add(segment);
					final int source = stopIdsIndexes.get(segment.getFrom()
							.getId());
					final int target = stopIdsIndexes.get(segment.getTo()
							.getId());
					final StringBuilder sb = new StringBuilder("{\"source\":")
							.append(source)
							.append(",\"target\":")
							.append(target)
							.append(",\"value\":3,\"group\":1,\"name\":\"")
							.append(route.getName())
							.append("\",\"details\":\"Long description of Segment\"}");
					if (segmentIt.hasNext()) {
						sb.append(",");
					}
					writer.print(sb.toString());
				}
				if (tripIt.hasNext()) {
					writer.print(",");
				}
			}
			if (routeIt.hasNext()) {
				writer.print(",");
			}
		}
		writer.print("]");
	}

}
