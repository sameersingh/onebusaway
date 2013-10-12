package edu.uw.modelab.pojo;

public class BusPosition {

	private final long timeStamp;
	private final long serviceDate;
	private final long tripId;
	private final double distanceAlongTrip;
	private double schedDev;
	private final String lat;
	private final String lon;

	public BusPosition(final long timeStamp, final long serviceDate,
			final long tripId, final double distanceAlongTrip,
			final double schedDev, final String lat, final String lon) {
		this.timeStamp = timeStamp;
		this.serviceDate = serviceDate;
		this.tripId = tripId;
		this.distanceAlongTrip = distanceAlongTrip;
		this.schedDev = schedDev;
		this.lat = lat;
		this.lon = lon;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public long getServiceDate() {
		return serviceDate;
	}

	public String getLat() {
		return lat;
	}

	public String getLon() {
		return lon;
	}

	public double getSchedDev() {
		return schedDev;
	}

	public void setSchedDev(final double schedDev) {
		this.schedDev = schedDev;
	}

	public long getTripId() {
		return tripId;
	}

	public double getDistanceAlongTrip() {
		return distanceAlongTrip;
	}

}
