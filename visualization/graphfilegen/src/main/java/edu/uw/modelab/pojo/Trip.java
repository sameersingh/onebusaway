package edu.uw.modelab.pojo;

public class Trip {

	private final int id;
	private final int routeId;

	public Trip(final int id, final int routeId) {
		this.id = id;
		this.routeId = routeId;
	}

	public int getId() {
		return id;
	}

	public int getRouteId() {
		return routeId;
	}

}
