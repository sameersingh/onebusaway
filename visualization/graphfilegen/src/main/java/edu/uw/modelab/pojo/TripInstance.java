package edu.uw.modelab.pojo;

import java.util.ArrayList;
import java.util.List;

public class TripInstance {

	private final long serviceDate;
	private final int tripId;
	List<RealtimePosition> realtimes;

	public TripInstance(final long serviceDate, final int tripId) {
		this.serviceDate = serviceDate;
		this.tripId = tripId;
		realtimes = new ArrayList<>();
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
}
