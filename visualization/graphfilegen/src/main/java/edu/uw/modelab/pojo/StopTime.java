package edu.uw.modelab.pojo;

public class StopTime {

	private final String arrivalTime;
	private final String departureTime;
	private final int stopSequence;

	public StopTime(final String arrivalTime, final String departureTime,
			final int stopSequence) {
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.stopSequence = stopSequence;
	}

	public int getStopSequence() {
		return stopSequence;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result)
				+ ((arrivalTime == null) ? 0 : arrivalTime.hashCode());
		result = (prime * result)
				+ ((departureTime == null) ? 0 : departureTime.hashCode());
		result = (prime * result) + stopSequence;
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
		final StopTime other = (StopTime) obj;
		if (arrivalTime == null) {
			if (other.arrivalTime != null) {
				return false;
			}
		} else if (!arrivalTime.equals(other.arrivalTime)) {
			return false;
		}
		if (departureTime == null) {
			if (other.departureTime != null) {
				return false;
			}
		} else if (!departureTime.equals(other.departureTime)) {
			return false;
		}
		if (stopSequence != other.stopSequence) {
			return false;
		}
		return true;
	}

}
