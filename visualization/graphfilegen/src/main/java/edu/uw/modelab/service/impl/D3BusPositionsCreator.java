package edu.uw.modelab.service.impl;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import edu.uw.modelab.dao.BusPositionDao;
import edu.uw.modelab.pojo.BusPosition;
import edu.uw.modelab.utils.Utils;

public class D3BusPositionsCreator extends D3Creator {

	private final BusPositionDao busPositionDao;

	public D3BusPositionsCreator(final String filename,
			final BusPositionDao dao) {
		super(filename);
		this.busPositionDao = dao;
	}

	@Override
	protected void addNodes(final PrintWriter writer) {
		writer.print("\"nodes\": [");
		final List<BusPosition> busPositions = busPositionDao.getBusPositions();
		final Iterator<BusPosition> it = busPositions.iterator();
		while (it.hasNext()) {
			final BusPosition busPosition = it.next();
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
