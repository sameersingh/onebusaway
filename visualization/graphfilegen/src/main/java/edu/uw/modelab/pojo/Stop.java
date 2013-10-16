package edu.uw.modelab.pojo;

public class Stop {

	private final int id;
	private final String name;
	private final String lat;
	private final String lon;
	private StopTime stopTime;

	public Stop(final int id, final String name, final String lat,
			final String lon) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}

	public void setStopTime(final StopTime stopTime) {
		this.stopTime = stopTime;
	}

	public StopTime getStopTime() {
		return this.stopTime;
	}

	public int getId() {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + id;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Stop other = (Stop) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "{id=" + id + ",name=" + name + ",lat=" + lat + ",lon=" + lon
				+ ",time=" + stopTime + "}";
	}
}
