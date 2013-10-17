package edu.uw.modelab.pojo;

public class TripInstance {

	private long timeStamp;
	private long serviceDate;
	private int tripId;
	private double distanceAlongTrip;
	private double schedDev;
	private double lat;
	private double lon;
	private double y;
	private double x;

	public TripInstance(final long timeStamp, final long serviceDate,
			final int tripId, final double distanceAlongTrip,
			final double schedDev, final double lat, final double lon,
			final double y, final double x) {
		this.timeStamp = timeStamp;
		this.serviceDate = serviceDate;
		this.tripId = tripId;
		this.distanceAlongTrip = distanceAlongTrip;
		this.schedDev = schedDev;
		this.lat = lat;
		this.lon = lon;
		this.y = y;
		this.x = x;
	}

	public TripInstance() {
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public long getServiceDate() {
		return serviceDate;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
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

	public void setLat(final double lat) {
		this.lat = lat;
	}

	public void setLon(final double lon) {
		this.lon = lon;
	}

	public double getY() {
		return y;
	}

	public void setY(final double y) {
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(final double x) {
		this.x = x;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof TripInstance)) {
			return false;
		}
		final TripInstance bp = (TripInstance) obj;
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

	@Override
	public String toString() {
		return "{tripId=" + tripId + ",timestamp=" + timeStamp
				+ ",serviceDate=" + serviceDate + ",lat=" + lat + ",lon=" + lon
				+ "}";
	}
}
