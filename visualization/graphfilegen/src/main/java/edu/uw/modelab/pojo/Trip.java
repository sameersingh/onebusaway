package edu.uw.modelab.pojo;


public class Trip {

	private final long id;
	private final long routeId;

	public Trip(final long id, final long routeId) {
		this.id = id;
		this.routeId = routeId;
	}

	public long getId() {
		return id;
	}

	public long getRouteId() {
		return routeId;
	}

}
