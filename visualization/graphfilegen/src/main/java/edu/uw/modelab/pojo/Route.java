package edu.uw.modelab.pojo;

import java.util.HashSet;
import java.util.Set;

public class Route {

	private final int id;
	private String name;
	private String agencyId;
	private final Set<Trip> trips;

	public Route(final int id) {
		this(id, null, null);
	}

	public Route(final int id, final String name, final String agencyId) {
		this.id = id;
		this.name = name;
		this.agencyId = agencyId;
		this.trips = new HashSet<>();
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setAgencyId(final String agencyId) {
		this.agencyId = agencyId;
	}

	public void addTrip(final Trip trip) {
		this.trips.add(trip);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAgencyId() {
		return agencyId;
	}

	public Set<Trip> getTrips() {
		return trips;
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
		final Route other = (Route) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

	public boolean contains(final Trip trip) {
		return trips.contains(trip);
	}

}
