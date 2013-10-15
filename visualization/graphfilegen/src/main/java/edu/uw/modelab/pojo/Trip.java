package edu.uw.modelab.pojo;

import java.util.HashSet;
import java.util.Set;

public class Trip {

	private final int id;
	private final String headSign;
	private final Set<Segment> segments;

	public Trip(final int id, final String headSign) {
		this.id = id;
		this.headSign = headSign;
		segments = new HashSet<>();
	}

	public void addSegment(final Segment segment) {
		this.segments.add(segment);
	}

	public Set<Segment> getSegments() {
		return this.segments;
	}

	public int getId() {
		return id;
	}

	public String getHeadSign() {
		return headSign;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + id;
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
		final Trip other = (Trip) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

}
