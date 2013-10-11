package edu.uw.modelab.pojo;

public class Stop {

	private final long id;
	private final String name;
	private final String lat;
	private final String lon;

	public Stop(final long id, final String name, final String lat,
			final String lon) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getLat() {
		return lat;
	}

	public String getLon() {
		return lon;
	}

}
