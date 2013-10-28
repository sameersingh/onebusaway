package edu.uw.modelab.service.impl;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.modelab.dao.RouteDao;
import edu.uw.modelab.dao.StopDao;
import edu.uw.modelab.pojo.Route;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.Trip;

public class DotCreator extends AbstractFileCreator {

	private static final Logger LOG = LoggerFactory.getLogger(DotCreator.class);

	private final RouteDao routeDao;
	private final StopDao stopDao;

	public DotCreator(final String filename, final StopDao stopDao,
			final RouteDao routeDao) {
		super(filename);
		this.stopDao = stopDao;
		this.routeDao = routeDao;
	}

	@Override
	protected void beginning(final PrintWriter writer) {
		writer.println("digraph buses {");
	}

	@Override
	protected void addNodes(final PrintWriter pw) {
		final List<Stop> stops = stopDao.getStops();
		for (final Stop stop : stops) {
			pw.println(stop.getId() + " [label=\"" + stop.getName() + "\"];");
		}
	}

	@Override
	protected void addEdges(final PrintWriter pw) {
		final Set<Segment> addedSegments = new HashSet<>();
		final Set<Route> routes = routeDao.getRoutesIncomplete();
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
					pw.println(segment.getFrom().getId() + " -> "
							+ segment.getTo().getId() + ";");
				}
			}
		}
	}

	@Override
	protected void end(final PrintWriter writer) {
		writer.println("}");
	}

	@Override
	protected void addNodes(final PrintWriter writer, final int tripId) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addEdges(final PrintWriter writer, final int tripId) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addNodes(final PrintWriter writer, final int tripId,
			final long serviceDate) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addEdges(final PrintWriter writer, final int tripId,
			final long serviceDate) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addNodes(final PrintWriter writer,
			final List<Integer> tripIds) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addEdges(final PrintWriter writer,
			final List<Integer> tripIds) {
		// TODO Auto-generated method stub

	}
}
