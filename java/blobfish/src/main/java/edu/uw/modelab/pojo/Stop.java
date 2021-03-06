package edu.uw.modelab.pojo;

public class Stop {

	private final int id;
	private final String name;
	private final double lat;
	private final double lon;
	private final double y;
	private final double x;
	private StopTime stopTime;
	private double distanceAlongTrip;

	public Stop(final int id, final String name, final double lat,
			final double lon, final double y, final double x) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.y = y;
		this.x = x;
	}

	public Stop(final Stop stop) {
		this(stop.id, stop.name, stop.lat, stop.lon, stop.y, stop.x);
		this.stopTime = new StopTime(stop.stopTime);
		this.distanceAlongTrip = stop.distanceAlongTrip;
	}

	public void setDistanceAlongTrip(final double distanceAlongTrip) {
		this.distanceAlongTrip = distanceAlongTrip;
	}

	public double getDistanceAlongTrip() {
		return this.distanceAlongTrip;
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

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
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
				+ ",time=" + stopTime + ", distanceAlongTrip= "
				+ distanceAlongTrip + "}";
	}
}
