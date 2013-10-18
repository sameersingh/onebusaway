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
import edu.uw.modelab.dao.TripDao;
import edu.uw.modelab.pojo.Route;
import edu.uw.modelab.pojo.Segment;
import edu.uw.modelab.pojo.Stop;
import edu.uw.modelab.pojo.Trip;
import edu.uw.modelab.service.TimeEstimator;
import edu.uw.modelab.utils.Utils;

public class D3StopsCreator extends D3Creator {

	private static final Logger LOG = LoggerFactory
			.getLogger(D3StopsCreator.class);

	private final Map<Integer, Integer> stopIdsIndexes;
	private final StopDao stopDao;
	private final RouteDao routeDao;
	private final TripDao tripDao;
	private final TimeEstimator timeEstimator;

	public D3StopsCreator(final String filename, final StopDao stopDao,
			final RouteDao routeDao, final TripDao tripDao,
			final TimeEstimator timeEstimator) {
		super(filename);
		this.stopDao = stopDao;
		this.routeDao = routeDao;
		this.tripDao = tripDao;
		this.timeEstimator = timeEstimator;
		stopIdsIndexes = new HashMap<Integer, Integer>(8110);
	}

	@Override
	protected void addNodes(final PrintWriter writer) {
		writer.print("\"nodes\": [");
		final List<Stop> stops = stopDao.getStops();
		addNodes(writer, stops);
	}

	@Override
	protected void addNodes(final PrintWriter writer, final int tripId) {
		writer.print("\"nodes\": [");
		final List<Stop> stops = stopDao.getStopsByTripId(tripId);
		addNodes(writer, stops);
	}

	@Override
	protected void addNodes(final PrintWriter writer, final int tripId,
			final long serviceDate) {
		writer.print("\"nodes\": [");
		final List<Stop> stops = stopDao.getStopsByTripId(tripId);
		addNodes(writer, stops);
	}

	@Override
	protected void addEdges(final PrintWriter writer) {
		final Set<Segment> addedSegments = new HashSet<>();
		final Set<Route> routes = routeDao.getRoutes();
		writer.print("\"links\":[");
		final Iterator<Route> routeIt = routes.iterator();
		final StringBuilder sb = new StringBuilder();
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
					sb.append("{\"source\":").append(source)
							.append(",\"target\":").append(target)
							.append(",\"value\":3,\"group\":1,\"name\":\"")
							.append(route.getName())
							.append("\",\"details\":\"\"},");

				}
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		writer.print(sb.toString());
		writer.print("]");
	}

	private void addNodes(final PrintWriter writer, final List<Stop> stops) {
		final Map<Integer, Integer> numberOfTripsPerStop = tripDao
				.getNumberOfTripsPerStop();
		final Iterator<Stop> it = stops.iterator();
		int index = 0;
		while (it.hasNext()) {
			final Stop stop = it.next();
			final int numberOfTripForStop = numberOfTripsPerStop.get(stop
					.getId());
			stopIdsIndexes.put(stop.getId(), index++);
			final StringBuilder sb = new StringBuilder("{\"name\":\"")
					.append(stop.getName())
					.append("\",\"group\":2,\"coords\":{\"type\": \"Point\",\"coordinates\":[")
					.append(stop.getLon()).append(",").append(stop.getLat())
					.append("]},\"details\":\"\",").append("\"num_trips\":")
					.append(numberOfTripForStop).append("}");
			if (it.hasNext()) {
				sb.append(",");
			}
			writer.print(sb.toString());
		}
		writer.print("],");
	}

	@Override
	protected void addEdges(final PrintWriter writer, final int tripId) {
		final Set<Segment> addedSegments = new HashSet<>();
		final Trip trip = tripDao.getTripById(tripId);
		writer.print("\"links\":[");
		timeEstimator.estimateArrivalTimes(trip);
		final Set<Segment> segments = trip.getSegments();
		final Iterator<Segment> segmentIt = segments.iterator();
		final StringBuilder sb = new StringBuilder();
		while (segmentIt.hasNext()) {
			final Segment segment = segmentIt.next();
			if (addedSegments.contains(segment)) {
				LOG.info("Skipping segment, already added");
				continue;
			}
			addedSegments.add(segment);
			final int source = stopIdsIndexes.get(segment.getFrom().getId());
			final int target = stopIdsIndexes.get(segment.getTo().getId());
			sb.append("{\"source\":")
					.append(source)
					.append(",\"target\":")
					.append(target)
					.append(",\"value\":3,\"group\":1,\"name\":\"")
					.append(trip.getHeadSign())
					.append("\",\"details\":\"\"")
					.append(",\"from_sched\":\"")
					.append(segment.getFrom().getStopTime()
							.getSchedArrivalTime())
					.append("\",\"from_actual\":\"")
					.append(Utils.toHHMMssPST(segment.getFrom().getStopTime()
							.getActualArrivalTime()))
					.append("\",\"to_sched\":\"")
					.append(segment.getTo().getStopTime().getSchedArrivalTime())
					.append("\",\"to_actual\":\"")
					.append(Utils.toHHMMssPST(segment.getTo().getStopTime()
							.getActualArrivalTime()))
					.append("\",\"distance\":").append(segment.getDistance())
					.append("},");

		}
		sb.deleteCharAt(sb.length() - 1);
		writer.print(sb.toString());
		writer.print("]");
	}

	@Override
	protected void addEdges(final PrintWriter writer, final int tripId,
			final long serviceDate) {
		addEdges(writer, tripId);
	}

}
