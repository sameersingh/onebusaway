package edu.uw.modelab.pojo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Trip {

	private final int id;
	private String headSign;
	private final Set<Segment> segments;

	public Trip(final int id) {
		this(id, null);
	}

	public Trip(final int id, final String headSign) {
		this.id = id;
		this.headSign = headSign;
		segments = new HashSet<>();
	}

	public void setHeadsign(final String headSign) {
		this.headSign = headSign;
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

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("{id=").append(id)
				.append(",headsign=").append(headSign).append(",segments=[");
		final Iterator<Segment> it = segments.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
