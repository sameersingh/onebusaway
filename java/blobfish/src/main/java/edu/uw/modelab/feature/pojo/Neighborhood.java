package edu.uw.modelab.feature.pojo;

public class Neighborhood {

	private final String name;
	private final double latitude;
	private final double longitude;
	private final double radius; // km

	public Neighborhood(final String name, final double latitude,
			final double longitude, final double radius) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
	}

	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getRadius() {
		return radius;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
		temp = Double.doubleToLongBits(radius);
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
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
		final Neighborhood other = (Neighborhood) obj;
		if (Double.doubleToLongBits(latitude) != Double
				.doubleToLongBits(other.latitude)) {
			return false;
		}
		if (Double.doubleToLongBits(longitude) != Double
				.doubleToLongBits(other.longitude)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (Double.doubleToLongBits(radius) != Double
				.doubleToLongBits(other.radius)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

}
