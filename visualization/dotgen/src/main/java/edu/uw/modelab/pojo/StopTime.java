package edu.uw.modelab.pojo;

public class StopTime {

	private final long tripId;
	private final long stopSequence;
	private final long stopId;

	public StopTime(final long tripId, final long stopSequence,
			final long stopId) {
		this.tripId = tripId;
		this.stopSequence = stopSequence;
		this.stopId = stopId;
	}

	public long getTripId() {
		return tripId;
	}

	public long getStopSequence() {
		return stopSequence;
	}

	public long getStopId() {
		return stopId;
	}

}
