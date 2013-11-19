package edu.uw.modelab.pojo;

import java.util.ArrayList;
import java.util.List;

public class TripInstance {

	private final long serviceDate;
	private final int tripId;
	private List<RealtimePosition> realtimes;

	public TripInstance(final long serviceDate, final int tripId) {
		this.serviceDate = serviceDate;
		this.tripId = tripId;
		realtimes = new ArrayList<>();
	}

	public TripInstance(final TripInstance instance) {
		this(instance.serviceDate, instance.tripId);
		for (final RealtimePosition rt : instance.realtimes) {
			this.addRealtime(new RealtimePosition(rt));
		}
	}

	public void setRealtimes(final List<RealtimePosition> realtimes) {
		this.realtimes = realtimes;
	}

	public void addRealtime(final RealtimePosition realtime) {
		this.realtimes.add(realtime);
	}

	public List<RealtimePosition> getRealtimes() {
		return realtimes;
	}

	public RealtimePosition getRealtime(final int position) {
		return realtimes.get(position);
	}

	public long getServiceDate() {
		return serviceDate;
	}

	public int getTripId() {
		return tripId;
	}

	@Override
	public String toString() {
		return "{tripId=" + tripId + ",serviceDate=" + serviceDate + "}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (int) (serviceDate ^ (serviceDate >>> 32));
		result = (prime * result) + tripId;
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
		final TripInstance other = (TripInstance) obj;
		if (serviceDate != other.serviceDate) {
			return false;
		}
		if (tripId != other.tripId) {
			return false;
		}
		return true;
	}

	public String getId() {
		return tripId + "_" + serviceDate;
	}

}
