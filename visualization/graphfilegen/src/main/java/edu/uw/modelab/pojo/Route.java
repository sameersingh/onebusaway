package edu.uw.modelab.pojo;

public class Route {

	private final long id;
	private final String name;
	private final String agencyId;

	public Route(final long id, final String name, final String agencyId) {
		this.id = id;
		this.name = name;
		this.agencyId = agencyId;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAgencyId() {
		return agencyId;
	}

}
