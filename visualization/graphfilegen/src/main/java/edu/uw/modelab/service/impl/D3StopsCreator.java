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

import edu.uw.modelab.dao.TripDao;
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
	private final TripDao tripDao;
	private final TimeEstimator timeEstimator;
	private final DistanceAlongTripCalculator distanceAlongTripCalculator;

	// horrible, I'm in a hurry
	private Set<Trip> trips = null;
	private Trip trip = null;

	public D3StopsCreator(final String filename, final TripDao tripDao,
			final TimeEstimator timeEstimator,
			final DistanceAlongTripCalculator distanceAlongTripCalculator) {
		super(filename);
		this.tripDao = tripDao;
		this.distanceAlongTripCalculator = distanceAlongTripCalculator;
		this.timeEstimator = timeEstimator;
		stopIdsIndexes = new HashMap<Integer, Integer>(8110);
	}

	@Override
	@Deprecated
	protected void addNodes(final PrintWriter writer) {
		writer.print("\"nodes\": [");
		this.trips = tripDao.getTrips();
		final Map<Stop, Map<TripInstance, String>> stops = new LinkedHashMap<>();
		for (final Trip trip : trips) {
			nodesForEachTrip(stops, trip);
		}
		final String appended = appendStops(stops);
		writer.print(appended);
		writer.print("],");
	}

	@Override
	protected void addNodes(final PrintWriter writer, final int tripId) {
		writer.print("\"nodes\": [");
		final Map<Stop, Map<TripInstance, String>> stops = new LinkedHashMap<>();
		this.trip = tripDao.getTripById(tripId);
		nodesForEachTrip(stops, trip);
		final String appended = appendStops(stops);
		writer.print(appended);
		writer.print("],");
	}

	@Override
	protected void addNodes(final PrintWriter writer, final int tripId,
			final long serviceDate) {
		addNodes(writer, tripId);
	}

	private String appendStops(final Map<Stop, Map<TripInstance, String>> stops) {
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
		return sb.toString();
	}

	private void nodesForEachTrip(
			final Map<Stop, Map<TripInstance, String>> stops, final Trip trip) {
		distanceAlongTripCalculator.addDistancesAlongTrip(trip);
		final Set<Segment> segments = trip.getSegments();
		final Set<TripInstance> tripInstances = trip.getInstances();
		for (final TripInstance tripInstance : tripInstances) {
			for (final Segment segment : segments) {
				if (segment.isFirst()) {
					final Stop from = segment.getFrom();
					final String fromArrivalTime = from.getStopTime()
							.getSchedDepartureTime();
					buildStopsAttributesPerTripInstance(stops, tripInstance,
							from, fromArrivalTime);
					final Stop to = segment.getTo();
					final String toArrivalTime = Utils
							.toHHMMssPST(timeEstimator.actual(tripInstance, to));
					buildStopsAttributesPerTripInstance(stops, tripInstance,
							to, toArrivalTime);
				} else {
					final Stop to = segment.getTo();
					final String toArrivalTime = Utils
							.toHHMMssPST(timeEstimator.actual(tripInstance, to));
					buildStopsAttributesPerTripInstance(stops, tripInstance,
							to, toArrivalTime);
				}
			}
		}
	}

	private void buildStopsAttributesPerTripInstance(
			final Map<Stop, Map<TripInstance, String>> stops,
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
	protected void addEdges(final PrintWriter writer) {
		final Set<Segment> addedSegments = new HashSet<>();
		writer.print("\"links\":[");
		final StringBuilder sb = new StringBuilder();
		final Iterator<Trip> tripIt = this.trips.iterator();
		while (tripIt.hasNext()) {
			final Trip trip = tripIt.next();
			edgesForEachTrip(addedSegments, sb, trip);
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
			writer.print(sb.toString());
		}
		writer.print("]");
	}

	@Override
	protected void addEdges(final PrintWriter writer, final int tripId) {
		// already have the trip
		final Set<Segment> addedSegments = new HashSet<>();
		writer.print("\"links\":[");
		final StringBuilder sb = new StringBuilder();
		edgesForEachTrip(addedSegments, sb, this.trip);
		sb.deleteCharAt(sb.length() - 1);
		writer.print(sb.toString());
		writer.print("]");
	}

	@Override
	protected void addEdges(final PrintWriter writer, final int tripId,
			final long serviceDate) {
		addEdges(writer, tripId);
	}

	private void edgesForEachTrip(final Set<Segment> addedSegments,
			final StringBuilder sb, final Trip trip) {
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
				final int source = stopIdsIndexes
						.get(segment.getFrom().getId());
				final int target = stopIdsIndexes.get(segment.getTo().getId());
				sb.append("{\"source\":").append(source).append(",\"target\":")
						.append(target)
						.append(",\"value\":3,\"group\":1,\"name\":\"")
						.append(segment.getId()).append("\",\"distance\":")
						.append(segment.getDistance()).append("},");

			}
		}
	}

	@Override
	protected void addNodes(final PrintWriter writer,
			final List<Integer> tripIds) {
		writer.print("\"nodes\": [");
		this.trips = tripDao.getTripsIn(tripIds);
		final Map<Stop, Map<TripInstance, String>> stops = new LinkedHashMap<>();
		for (final Trip trip : trips) {
			nodesForEachTrip(stops, trip);
		}
		final String appended = appendStops(stops);
		writer.print(appended);
		writer.print("],");
	}

	@Override
	protected void addEdges(final PrintWriter writer,
			final List<Integer> tripIds) {
		addEdges(writer);

	}

}
