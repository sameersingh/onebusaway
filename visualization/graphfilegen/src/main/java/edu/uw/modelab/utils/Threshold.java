package edu.uw.modelab.utils;

public class Threshold {

	private final int max;
	private final int min;

	public Threshold(final int min, final int max) {
		this.max = max;
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public int getMin() {
		return min;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + max;
		result = (prime * result) + min;
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
		final Threshold other = (Threshold) obj;
		if (max != other.max) {
			return false;
		}
		if (min != other.min) {
			return false;
		}
		return true;
	}

}
