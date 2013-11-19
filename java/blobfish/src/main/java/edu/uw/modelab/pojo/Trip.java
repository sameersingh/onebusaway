package edu.uw.modelab.pojo;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class Trip {

	private final int id;
	private String headSign;
	private Set<Segment> segments;
	private Set<TripInstance> instances;

	public Trip(final int id) {
		this(id, null);
	}

	public Trip(final int id, final String headSign) {
		this.id = id;
		this.headSign = headSign;
		segments = new LinkedHashSet<>();
		instances = new LinkedHashSet<>();
	}

	public Trip(final Trip trip) {
		this(trip.id, trip.headSign);
		final Set<Segment> segments = trip.getSegments();
		for (final Segment segment : segments) {
			this.addSegment(new Segment(segment));
		}
		final Set<TripInstance> instances = trip.getInstances();
		for (final TripInstance instance : instances) {
			this.addInstance(new TripInstance(instance));
		}
	}

	public void setHeadsign(final String headSign) {
		this.headSign = headSign;
	}

	public void addSegment(final Segment segment) {
		this.segments.add(segment);
	}

	public void setSegments(final Set<Segment> segments) {
		this.segments = segments;
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

	public void addInstance(final TripInstance instance) {
		this.instances.add(instance);
	}

	public void removeInstance(final TripInstance instance) {
		this.instances.remove(instance);
	}

	public void setInstances(final Set<TripInstance> instances) {
		this.instances = instances;
	}

	public Set<TripInstance> getInstances() {
		return instances;
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
