package edu.uw.modelab.feature.pojo;

import java.util.ArrayList;
import java.util.List;

import edu.uw.modelab.utils.Utils;

public class AutoregressiveOutputRow {

	private int xt;
	private final List<Integer> previousXts;
	private long serviceDate;
	private double distanceAlongTrip;
	private String timeOfDay;
	private int routeId;

	public AutoregressiveOutputRow() {
		previousXts = new ArrayList<>();
	}

	public int getXt() {
		return xt;
	}

	public void setXt(final int xt) {
		this.xt = xt;
	}

	public List<Integer> getPreviousXts() {
		return previousXts;
	}

	public void addPreviousXts(final Integer previousXt) {
		this.previousXts.add(previousXt);
	}

	public long getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(final long serviceDate) {
		this.serviceDate = serviceDate;
	}

	public double getDistanceAlongTrip() {
		return distanceAlongTrip;
	}

	public void setDistanceAlongTrip(final double distanceAlongTrip) {
		this.distanceAlongTrip = distanceAlongTrip;
	}

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(final int routeId) {
		this.routeId = routeId;
	}

	public void setTimeOfDay(final long timestamp) {
		final String time = Utils.toHHMMssPST(timestamp);
		this.timeOfDay = Utils.getTimeOfDayVector(time, time);
	}

	public String getTimeOfDay() {
		return timeOfDay;
	}

	@Override
	// shoud be done with a visitor
	public String toString() {
		final StringBuilder sb = new StringBuilder().append(xt).append("\t");
		for (final Integer previousT : previousXts) {
			sb.append(previousT).append("\t");
		}
		sb.append(distanceAlongTrip).append("\t");
		// already has the \t
		sb.append(timeOfDay);
		// sb.append(routeId);
		return sb.toString();
	}

}
