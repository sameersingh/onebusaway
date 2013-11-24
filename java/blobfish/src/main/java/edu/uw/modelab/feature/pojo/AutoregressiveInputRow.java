package edu.uw.modelab.feature.pojo;

public class AutoregressiveInputRow implements
		Comparable<AutoregressiveInputRow> {

	private final long timestamp;
	private final long serviceDate;
	private final double distance;
	private final int schedDeviation;

	public AutoregressiveInputRow(final long timestamp, final long serviceDate,
			final double distance, final int schedDeviation) {
		this.timestamp = timestamp;
		this.serviceDate = serviceDate;
		this.distance = distance;
		this.schedDeviation = schedDeviation;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public long getServiceDate() {
		return serviceDate;
	}

	public double getDistance() {
		return distance;
	}

	public int getSchedDeviation() {
		return schedDeviation;
	}

	@Override
	public int compareTo(final AutoregressiveInputRow o) {
		return (int) (this.timestamp - o.timestamp);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(distance);
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		result = (prime * result) + schedDeviation;
		result = (prime * result) + (int) (serviceDate ^ (serviceDate >>> 32));
		result = (prime * result) + (int) (timestamp ^ (timestamp >>> 32));
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
		final AutoregressiveInputRow other = (AutoregressiveInputRow) obj;
		if (Double.doubleToLongBits(distance) != Double
				.doubleToLongBits(other.distance)) {
			return false;
		}
		if (schedDeviation != other.schedDeviation) {
			return false;
		}
		if (serviceDate != other.serviceDate) {
			return false;
		}
		if (timestamp != other.timestamp) {
			return false;
		}
		return true;
	}

}
