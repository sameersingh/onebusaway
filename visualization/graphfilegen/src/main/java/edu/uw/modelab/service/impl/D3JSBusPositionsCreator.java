package edu.uw.modelab.service.impl;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.uw.modelab.dao.Dao;
import edu.uw.modelab.pojo.BusPosition;
import edu.uw.modelab.pojo.TripInstance;
import edu.uw.modelab.utils.Utils;

public class D3JSBusPositionsCreator extends D3JSCreator {

	private final Map<TripInstance, Integer> tripInstanceIndexes;

	public D3JSBusPositionsCreator(final String filename, final Dao dao) {
		super(filename, dao);
		this.tripInstanceIndexes = new HashMap<TripInstance, Integer>();
	}

	@Override
	protected void addNodes(final PrintWriter writer) {
		writer.print("\"nodes\": [");
		final List<BusPosition> busPositions = getDao().getBusPositions();
		final Iterator<BusPosition> it = busPositions.iterator();
		int index = 0;
		while (it.hasNext()) {
			final BusPosition busPosition = it.next();
			tripInstanceIndexes.put(new TripInstance(busPosition.getTripId(),
					busPosition.getServiceDate()), index++);
			final StringBuilder sb = new StringBuilder("{\"name\":\"")
					.append(busPosition.getTripId())
					.append("_")
					.append(Utils.toDate(busPosition.getServiceDate()))
					.append("\",\"group\":3,\"coords\":{\"type\": \"Point\",\"coordinates\":[")
					.append(busPosition.getLon()).append(",")
					.append(busPosition.getLat())
					.append("]},\"details\":\"bus long desc\"}");
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

}
