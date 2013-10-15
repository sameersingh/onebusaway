package edu.uw.modelab.pojo;

public class Segment {

	private final Stop from;
	private final Stop to;

	public Segment(final Stop from, final Stop to) {
		this.from = from;
		this.to = to;
	}

	public Stop getFrom() {
		return from;
	}

	public Stop getTo() {
		return to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + from.hashCode();
		result = (prime * result) + to.hashCode();
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
		final Segment other = (Segment) obj;
		if (!from.equals(other.from)) {
			return false;
		}
		if (!to.equals(other.to)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "{from:" + from + ",to:" + to + "}";
	}

}
