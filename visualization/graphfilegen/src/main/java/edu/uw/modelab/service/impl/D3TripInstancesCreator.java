package edu.uw.modelab.service.impl;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import edu.uw.modelab.dao.TripInstanceDao;
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
		final List<TripInstance> busPositions = tripInstanceDao
				.getTripInstances();
		addNodes(writer, busPositions);
	}

	@Override
	protected void addNodes(final PrintWriter writer, final int tripId) {
		writer.print("\"nodes\": [");
		final List<TripInstance> tripInstances = tripInstanceDao
				.getTripInstancesForTripId(tripId);
		addNodes(writer, tripInstances);
	}

	private void addNodes(final PrintWriter writer,
			final List<TripInstance> tripInstances) {
		final Iterator<TripInstance> it = tripInstances.iterator();
		while (it.hasNext()) {
			final TripInstance tripInstance = it.next();
			final StringBuilder sb = new StringBuilder("{\"name\":\"")
					.append(tripInstance.getTripId())
					.append("_")
					.append(Utils.toDate(tripInstance.getTimeStamp()))
					.append("\",\"group\":3,\"coords\":{\"type\": \"Point\",\"coordinates\":[")
					.append(tripInstance.getLon()).append(",")
					.append(tripInstance.getLat())
					.append("]},\"details\":\"bus long desc\",\"distance\":")
					.append(tripInstance.getDistanceAlongTrip())
					.append(",\"sched_dev\":")
					.append(tripInstance.getSchedDev()).append("}");
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

}
