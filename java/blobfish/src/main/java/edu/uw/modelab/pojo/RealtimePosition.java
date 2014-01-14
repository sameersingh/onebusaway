package edu.uw.modelab.pojo;

public class RealtimePosition {

	private final long timeStamp;
	private final double distanceAlongTrip;
	private final double schedDev;
	private final double lat;
	private final double lon;
	private final double y;
	private final double x;

	public RealtimePosition(final long timeStamp,
			final double distanceAlongTrip, final double schedDev,
			final double lat, final double lon, final double y, final double x) {
		this.timeStamp = timeStamp;
		this.distanceAlongTrip = distanceAlongTrip;
		this.schedDev = schedDev;
		this.lat = lat;
		this.lon = lon;
		this.y = y;
		this.x = x;
	}

	public RealtimePosition(final RealtimePosition rt) {
		this(rt.timeStamp, rt.distanceAlongTrip, rt.schedDev, rt.lat, rt.lon,
				rt.y, rt.x);
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public double getDistanceAlongTrip() {
		return distanceAlongTrip;
	}

	public double getSchedDev() {
		return schedDev;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public double getY() {
		return y;
	}

	public double getX() {
		return x;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (int) (timeStamp ^ (timeStamp >>> 32));
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
		final RealtimePosition other = (RealtimePosition) obj;
		if (timeStamp != other.timeStamp) {
			return false;
		}
		return true;
	}

}
