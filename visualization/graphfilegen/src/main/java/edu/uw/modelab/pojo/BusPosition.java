package edu.uw.modelab.pojo;

public class BusPosition {

	private long timeStamp;
	private long serviceDate;
	private int tripId;
	private double distanceAlongTrip;
	private double schedDev;
	private String lat;
	private String lon;

	public BusPosition(final long timeStamp, final long serviceDate,
			final int tripId, final double distanceAlongTrip,
			final double schedDev, final String lat, final String lon) {
		this.timeStamp = timeStamp;
		this.serviceDate = serviceDate;
		this.tripId = tripId;
		this.distanceAlongTrip = distanceAlongTrip;
		this.schedDev = schedDev;
		this.lat = lat;
		this.lon = lon;
	}

	public BusPosition() {
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

	public int getTripId() {
		return tripId;
	}

	public double getDistanceAlongTrip() {
		return distanceAlongTrip;
	}

	public void setTimeStamp(final long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public void setServiceDate(final long serviceDate) {
		this.serviceDate = serviceDate;
	}

	public void setTripId(final int tripId) {
		this.tripId = tripId;
	}

	public void setDistanceAlongTrip(final double distanceAlongTrip) {
		this.distanceAlongTrip = distanceAlongTrip;
	}

	public void setLat(final String lat) {
		this.lat = lat;
	}

	public void setLon(final String lon) {
		this.lon = lon;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof BusPosition)) {
			return false;
		}
		final BusPosition bp = (BusPosition) obj;
		return (this.timeStamp == bp.timeStamp)
				&& (this.serviceDate == bp.serviceDate)
				&& (this.tripId == bp.tripId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (int) (serviceDate ^ (serviceDate >>> 32));
		result = (prime * result) + (int) (timeStamp ^ (timeStamp >>> 32));
		result = (prime * result) + tripId;
		return result;
	}
}
