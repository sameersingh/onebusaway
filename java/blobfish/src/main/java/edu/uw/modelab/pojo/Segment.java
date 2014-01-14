package edu.uw.modelab.pojo;

import edu.uw.modelab.utils.Utils;

public class Segment {

	private final Stop from;
	private final Stop to;
	private final double distance;
	private boolean isFirst;

	public Segment(final Stop from, final Stop to) {
		this.from = from;
		this.to = to;
		this.isFirst = false;
		this.distance = Utils.euclideanDistance(from.getX(), to.getX(),
				from.getY(), to.getY());
	}

	public Segment(final Segment segment) {
		this(new Stop(segment.from), new Stop(segment.to));
		this.isFirst = segment.isFirst;
	}

	public Stop getFrom() {
		return from;
	}

	public Stop getTo() {
		return to;
	}

	public double getDistance() {
		return distance;
	}

	public void setFirst(final boolean isFirst) {
		this.isFirst = isFirst;
	}

	public boolean isFirst() {
		return isFirst;
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

	@Deprecated
	public String name() {
		return "seg[" + from.getId() + "-" + to.getId() + "]";
	}

	public String getId() {
		return from.getId() + "-" + to.getId();
	}
}
