package edu.uw.modelab.pojo;

public class Route {

	private final int id;
	private final String name;
	private final String agencyId;

	public Route(final int id, final String name, final String agencyId) {
		this.id = id;
		this.name = name;
		this.agencyId = agencyId;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAgencyId() {
		return agencyId;
	}

}
