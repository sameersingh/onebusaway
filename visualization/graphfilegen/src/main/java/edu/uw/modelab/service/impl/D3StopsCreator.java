package edu.uw.modelab.service.impl;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import edu.uw.modelab.pojo.TripInstance;
import edu.uw.modelab.service.DistanceAlongTripCalculator;
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
	private final DistanceAlongTripCalculator distanceAlongTripCalculator;

	// horrible, I'm in a hurry
	private Set<Route> routes = null;

	public D3StopsCreator(final String filename, final StopDao stopDao,
			final RouteDao routeDao, final TripDao tripDao,
			final TimeEstimator timeEstimator,
			final DistanceAlongTripCalculator distanceAlongTripCalculator) {
		super(filename);
		this.stopDao = stopDao;
		this.routeDao = routeDao;
		this.tripDao = tripDao;
		this.distanceAlongTripCalculator = distanceAlongTripCalculator;
		this.timeEstimator = timeEstimator;
		stopIdsIndexes = new HashMap<Integer, Integer>(8110);
	}

	@Override
	protected void addNodes(final PrintWriter writer) {
		writer.print("\"nodes\": [");
		final Map<Stop, Map<TripInstance, String>> stops = buildStops();
		final Set<Entry<Stop, Map<TripInstance, String>>> entrySet = stops
				.entrySet();
		int index = 0;
		final StringBuilder sb = new StringBuilder();
		for (final Entry<Stop, Map<TripInstance, String>> entry : entrySet) {
			final Stop stop = entry.getKey();
			stopIdsIndexes.put(stop.getId(), index++);
			sb.append("{\"name\":\"")
					.append(stop.getName())
					.append("\",\"group\":2,\"coords\":{\"type\": \"Point\",\"coordinates\":[")
					.append(stop.getLon()).append(",").append(stop.getLat())
					.append("]},\"trip_instances\":[");
			final Iterator<Entry<TripInstance, String>> tripInstancesEntryIt = entry
					.getValue().entrySet().iterator();
			while (tripInstancesEntryIt.hasNext()) {
				final Entry<TripInstance, String> tripInstanceEntry = tripInstancesEntryIt
						.next();
				final TripInstance tripInstance = tripInstanceEntry.getKey();
				final String[] tokens = tripInstanceEntry.getValue().split("-");
				sb.append("{\"id\":\"").append(tripInstance.getId())
						.append("\",\"arrival\":\"").append(tokens[0])
						.append("\",\"scheduled\":\"").append(tokens[1])
						.append("\"}");
				if (tripInstancesEntryIt.hasNext()) {
					sb.append(",");
				}
			}
			sb.append("]},");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		writer.print(sb.toString());
		writer.print("],");
	}

	private Map<Stop, Map<TripInstance, String>> buildStops() {
		routes = routeDao.getRoutes();
		final Map<Stop, Map<TripInstance, String>> stops = new LinkedHashMap<>();
		for (final Route route : routes) {
			final Set<Trip> trips = route.getTrips();
			for (final Trip trip : trips) {
				distanceAlongTripCalculator.addDistancesAlongTrip(trip);
				final Set<Segment> segments = trip.getSegments();
				final Set<TripInstance> tripInstances = trip.getInstances();
				for (final TripInstance tripInstance : tripInstances) {
					for (final Segment segment : segments) {
						if (segment.isFirst()) {
							final Stop from = segment.getFrom();
							final String fromArrivalTime = from.getStopTime()
									.getSchedDepartureTime();
							addStop(stops, tripInstance, from, fromArrivalTime);
							final Stop to = segment.getTo();
							final String toArrivalTime = Utils
									.toHHMMssPST(timeEstimator.actual(
											tripInstance, to));
							addStop(stops, tripInstance, to, toArrivalTime);
						} else {
							final Stop to = segment.getTo();
							final String toArrivalTime = Utils
									.toHHMMssPST(timeEstimator.actual(
											tripInstance, to));
							addStop(stops, tripInstance, to, toArrivalTime);
						}
					}
				}
			}
		}
		return stops;
	}

	private void addStop(final Map<Stop, Map<TripInstance, String>> stops,
			final TripInstance tripInstance, final Stop stop,
			final String arrivalTime) {
		final String scheduled = stop.getStopTime().getSchedArrivalTime();
		Map<TripInstance, String> tripInstancesPerStop = stops.get(stop);

		if (tripInstancesPerStop == null) {
			tripInstancesPerStop = new HashMap<>();
			tripInstancesPerStop.put(tripInstance, arrivalTime + "-"
					+ scheduled);
			stops.put(stop, tripInstancesPerStop);
		} else {
			tripInstancesPerStop.put(tripInstance, arrivalTime + "-"
					+ scheduled);
		}
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
					if (!stopIdsIndexes.isEmpty()) {
						final int source = stopIdsIndexes.get(segment.getFrom()
								.getId());
						final int target = stopIdsIndexes.get(segment.getTo()
								.getId());
						sb.append("{\"source\":").append(source)
								.append(",\"target\":").append(target)
								.append(",\"value\":3,\"group\":1,\"name\":\"")
								.append(segment.getId())
								.append("\",\"details\":\"\"},");
					}
				}
			}
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
			writer.print(sb.toString());
		}
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
