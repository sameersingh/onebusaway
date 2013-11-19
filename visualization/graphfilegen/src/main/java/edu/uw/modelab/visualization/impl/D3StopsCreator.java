package edu.uw.modelab.visualization.impl;

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
import edu.uw.modelab.service.DistanceAlongTripPopulator;
import edu.uw.modelab.service.ErrorService;
import edu.uw.modelab.service.TimeService;
import edu.uw.modelab.utils.Utils;
import edu.uw.modelab.visualization.pojo.TripInstanceData;

/**
 * Refactor when there's time... horrible class
 * 
 */
public class D3StopsCreator extends D3Creator {

	private static final Logger LOG = LoggerFactory
			.getLogger(D3StopsCreator.class);

	private final Map<Integer, Integer> stopIdsIndexes;
	private final TripDao tripDao;
	private final TimeService timeEstimator;
	private final DistanceAlongTripPopulator distanceAlongTripCalculator;
	private final ErrorService errorCalculator;

	// horrible, I'm in a hurry
	private Set<Trip> trips = null;
	private Trip trip = null;

	public D3StopsCreator(final String filename, final TripDao tripDao,
			final TimeService timeEstimator,
			final DistanceAlongTripPopulator distanceAlongTripCalculator,
			final ErrorService errorCalculator) {
		super(filename);
		this.tripDao = tripDao;
		this.distanceAlongTripCalculator = distanceAlongTripCalculator;
		this.timeEstimator = timeEstimator;
		this.errorCalculator = errorCalculator;
		stopIdsIndexes = new HashMap<Integer, Integer>(8110);
	}

	@Override
	protected void addNodes(final PrintWriter writer) {
		final List<Integer> tripIds = tripDao.getTripIds();
		addNodes(writer, tripIds);
	}

	@Override
	protected void addNodes(final PrintWriter writer, final int tripId) {
		writer.print("\"nodes\": [");
		final Map<Stop, Map<Trip, Map<TripInstance, TripInstanceData>>> stops = new LinkedHashMap<>();
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

	private String appendStops(
			final Map<Stop, Map<Trip, Map<TripInstance, TripInstanceData>>> stops) {
		final Set<Entry<Stop, Map<Trip, Map<TripInstance, TripInstanceData>>>> entrySet = stops
				.entrySet();
		int index = 0;
		final StringBuilder sb = new StringBuilder();
		for (final Entry<Stop, Map<Trip, Map<TripInstance, TripInstanceData>>> entry : entrySet) {
			final Stop stop = entry.getKey();
			stopIdsIndexes.put(stop.getId(), index++);
			sb.append("{\"id\":")
					.append(stop.getId())
					.append(",\"name\":\"")
					.append(stop.getName())
					.append("\",\"group\":2,\"coords\":{\"type\": \"Point\",\"coordinates\":[")
					.append(stop.getLon()).append(",").append(stop.getLat())
					.append("]},\"trips\":[");
			final Map<Trip, Map<TripInstance, TripInstanceData>> tripEntries = entry
					.getValue();
			if (tripEntries != null) {
				final Iterator<Entry<Trip, Map<TripInstance, TripInstanceData>>> tripsEntryIt = tripEntries
						.entrySet().iterator();
				while (tripsEntryIt.hasNext()) {
					final Entry<Trip, Map<TripInstance, TripInstanceData>> tripEntry = tripsEntryIt
							.next();
					final Trip trip = tripEntry.getKey();
					sb.append("{\"id\":").append(trip.getId());
					final Map<TripInstance, TripInstanceData> tripInstances = tripEntry
							.getValue();
					sb.append(",\"trip_instances\":[");
					if (tripInstances != null) {
						final Iterator<Entry<TripInstance, TripInstanceData>> tripInstancesIt = tripInstances
								.entrySet().iterator();
						while (tripInstancesIt.hasNext()) {
							final Entry<TripInstance, TripInstanceData> tripInstanceEntry = tripInstancesIt
									.next();
							final TripInstance tripInstance = tripInstanceEntry
									.getKey();
							final TripInstanceData tripInstanceValue = tripInstanceEntry
									.getValue();
							sb.append("{\"service_date\":")
									.append(tripInstance.getServiceDate())
									.append(",\"arrival\":\"")
									.append(tripInstanceValue.getArrivalTime())
									.append("\",\"scheduled\":\"")
									.append(tripInstanceValue
											.getScheduledTime())
									.append("\",\"sched_error\":")
									.append(tripInstanceValue
											.getScheduledError())
									.append(",\"oba_error\":")
									.append(tripInstanceValue.getObaError())
									.append(",\"mode_error\":")
									.append(tripInstanceValue.getOurError())
									.append("}");
							if (tripInstancesIt.hasNext()) {
								sb.append(",");
							}
						}
					}
					sb.append("]}");
					if (tripsEntryIt.hasNext()) {
						sb.append(",");
					}
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
			final Map<Stop, Map<Trip, Map<TripInstance, TripInstanceData>>> stops,
			final Trip trip) {
		distanceAlongTripCalculator.addDistancesAlongTrip(trip);
		final Set<Segment> segments = trip.getSegments();
		final Set<TripInstance> tripInstances = trip.getInstances();
		if (!tripInstances.isEmpty()) {
			for (final TripInstance tripInstance : tripInstances) {
				for (final Segment segment : segments) {
					if (segment.isFirst()) {
						final Stop from = segment.getFrom();
						final String fromArrivalTime = from.getStopTime()
								.getSchedDepartureTime();
						buildStopsAttributesPerTripInstance(stops,
								tripInstance, trip, from, fromArrivalTime, 0, 0);
						final Stop to = segment.getTo();
						final String toArrivalTime = Utils
								.toHHMMssPST(timeEstimator.getActualTime(tripInstance,
										to));
						final long[] obaAndMode = errorCalculator
								.getObaAndModeErrors(tripInstance, segment);
						buildStopsAttributesPerTripInstance(stops,
								tripInstance, trip, to, toArrivalTime,
								obaAndMode[0], obaAndMode[1]);
					} else {
						final Stop to = segment.getTo();
						final String toArrivalTime = Utils
								.toHHMMssPST(timeEstimator.getActualTime(tripInstance,
										to));
						final long[] obaAndMode = errorCalculator
								.getObaAndModeErrors(tripInstance, segment);
						buildStopsAttributesPerTripInstance(stops,
								tripInstance, trip, to, toArrivalTime,
								obaAndMode[0], obaAndMode[1]);
					}
				}
			}
		} else {
			// again, ugly stuff, repeated code... need to refactor this asap
			// cannot calculate actual because there are no instances
			for (final Segment segment : segments) {
				if (segment.isFirst()) {
					final Stop from = segment.getFrom();
					buildStopsWithEmptyAttributesPerTripInstance(stops, trip,
							from);
					final Stop to = segment.getTo();
					buildStopsWithEmptyAttributesPerTripInstance(stops, trip,
							to);
				} else {
					final Stop to = segment.getTo();
					buildStopsWithEmptyAttributesPerTripInstance(stops, trip,
							to);
				}
			}
		}
	}

	private void buildStopsWithEmptyAttributesPerTripInstance(
			final Map<Stop, Map<Trip, Map<TripInstance, TripInstanceData>>> stops,
			final Trip trip, final Stop stop) {
		Map<Trip, Map<TripInstance, TripInstanceData>> tripsPerStop = stops
				.get(stop);
		if (tripsPerStop == null) {
			tripsPerStop = new LinkedHashMap<>();
			tripsPerStop.put(trip, null);
			stops.put(stop, tripsPerStop);
		} else {
			tripsPerStop.put(trip, null);
		}
	}

	// find a way to add oba and mode stuff
	private void buildStopsAttributesPerTripInstance(
			final Map<Stop, Map<Trip, Map<TripInstance, TripInstanceData>>> stops,
			final TripInstance tripInstance, final Trip trip, final Stop stop,
			final String arrivalTime, final long obaError, final long modeError) {
		final String scheduled = stop.getStopTime().getSchedArrivalTime();
		Map<Trip, Map<TripInstance, TripInstanceData>> tripsPerStop = stops
				.get(stop);
		if (tripsPerStop == null) {
			tripsPerStop = new LinkedHashMap<>();
			final Map<TripInstance, TripInstanceData> tripInstancesPerStop = new LinkedHashMap<>();
			final TripInstanceData value = new TripInstanceData(arrivalTime,
					scheduled, obaError, modeError);
			tripInstancesPerStop.put(tripInstance, value);
			tripsPerStop.put(trip, tripInstancesPerStop);
			stops.put(stop, tripsPerStop);
		} else {
			Map<TripInstance, TripInstanceData> tripInstancesPerStop = tripsPerStop
					.get(trip);
			if (tripInstancesPerStop == null) {
				tripInstancesPerStop = new LinkedHashMap<>();
				final TripInstanceData value = new TripInstanceData(
						arrivalTime, scheduled, obaError, modeError);
				tripInstancesPerStop.put(tripInstance, value);
				tripsPerStop.put(trip, tripInstancesPerStop);
			} else {
				final TripInstanceData value = new TripInstanceData(
						arrivalTime, scheduled, obaError, modeError);
				tripInstancesPerStop.put(tripInstance, value);
			}
		}
	}

	@Override
	protected void addEdges(final PrintWriter writer) {
		addEdgesWithTrips(writer);
	}

	protected void addEdgesWithTrips(final PrintWriter writer) {
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
		final Map<Stop, Map<Trip, Map<TripInstance, TripInstanceData>>> stops = new LinkedHashMap<>();
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
		addEdgesWithTrips(writer);

	}
}
