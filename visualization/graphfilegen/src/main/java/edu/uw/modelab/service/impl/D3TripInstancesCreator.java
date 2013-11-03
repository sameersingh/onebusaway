package edu.uw.modelab.service.impl;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.uw.modelab.dao.TripInstanceDao;
import edu.uw.modelab.pojo.RealtimePosition;
import edu.uw.modelab.pojo.TripInstance;
import edu.uw.modelab.utils.Utils;

public class D3TripInstancesCreator extends D3Creator {

	private final TripInstanceDao tripInstanceDao;

	public D3TripInstancesCreator(final String filename,
			final TripInstanceDao dao) {
		super(filename);
		this.tripInstanceDao = dao;
	}

	@Override
	protected void addNodes(final PrintWriter writer) {
		writer.print("\"nodes\": [");
		final List<TripInstance> tripInstances = tripInstanceDao
				.getTripInstances();
		doAddNodes(writer, tripInstances);
	}

	@Override
	protected void addNodes(final PrintWriter writer, final int tripId) {
		writer.print("\"nodes\": [");
		final List<TripInstance> tripInstances = tripInstanceDao
				.getTripInstancesForTripId(tripId);
		doAddNodes(writer, tripInstances);
	}

	@Override
	protected void addNodes(final PrintWriter writer, final int tripId,
			final long serviceDate) {
		writer.print("\"nodes\": [");
		final TripInstance tripInstance = tripInstanceDao.getTripInstance(
				tripId, serviceDate);
		final List<TripInstance> tripInstances = new ArrayList<>();
		tripInstances.add(tripInstance);
		doAddNodes(writer, tripInstances);
	}

	private void doAddNodes(final PrintWriter writer,
			final List<TripInstance> tripInstances) {
		final Iterator<TripInstance> it = tripInstances.iterator();
		while (it.hasNext()) {
			final TripInstance tripInstance = it.next();
			final List<RealtimePosition> realtimes = tripInstance
					.getRealtimes();
			final Iterator<RealtimePosition> realtimesIt = realtimes.iterator();
			final StringBuilder sb = new StringBuilder();
			while (realtimesIt.hasNext()) {
				final RealtimePosition rtp = realtimesIt.next();
				sb.append("{\"name\":\"")
						.append(tripInstance.getId())
						.append("\",\"trip_id\":")
						.append(tripInstance.getTripId())
						.append(",\"service_date\":")
						.append(tripInstance.getServiceDate())
						.append(",\"timestamp\":\"")
						.append(Utils.toHHMMssPST(rtp.getTimeStamp()))
						.append("\",\"group\":3,\"coords\":{\"type\": \"Point\",\"coordinates\":[")
						.append(rtp.getLon()).append(",").append(rtp.getLat())
						.append("]},\"distance\":")
						.append(rtp.getDistanceAlongTrip())
						.append(",\"sched_dev\":").append(rtp.getSchedDev())
						.append("}");
				if (realtimesIt.hasNext()) {
					sb.append(",");
				}
			}
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
		writer.print("]");
	}

	@Override
	protected void addEdges(final PrintWriter writer, final int tripId) {
		addEdges(writer);
	}

	@Override
	protected void addEdges(final PrintWriter writer, final int tripId,
			final long serviceDate) {
		addEdges(writer);
	}

	@Override
	protected void addNodes(final PrintWriter writer,
			final List<Integer> tripIds) {
		writer.print("\"nodes\": [");
		final List<TripInstance> tripInstances = tripInstanceDao
				.getTripInstancesForTripIds(tripIds, 20);
		doAddNodes(writer, tripInstances);
	}

	@Override
	protected void addEdges(final PrintWriter writer,
			final List<Integer> tripIds) {
		addEdges(writer);
	}

}
