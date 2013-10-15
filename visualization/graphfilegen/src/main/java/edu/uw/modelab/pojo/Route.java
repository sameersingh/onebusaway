package edu.uw.modelab.pojo;

import java.util.HashSet;
import java.util.Set;

public class Route {

	private final int id;
	private final String name;
	private final String agencyId;
	private final Set<Trip> trips;

	public Route(final int id, final String name, final String agencyId) {
		this.id = id;
		this.name = name;
		this.agencyId = agencyId;
		this.trips = new HashSet<>();
	}

	public void addTrip(final Trip trip) {
		this.trips.add(trip);
	}

	public void removeTrip(final Trip trip) {
		this.trips.remove(trip);
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

}
