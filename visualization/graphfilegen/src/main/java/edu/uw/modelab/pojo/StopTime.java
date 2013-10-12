package edu.uw.modelab.pojo;

public class StopTime {

	private final int tripId;
	private final int stopSequence;
	private final int stopId;

	public StopTime(final int tripId, final int stopSequence, final int stopId) {
		this.tripId = tripId;
		this.stopSequence = stopSequence;
		this.stopId = stopId;
	}

	public int getTripId() {
		return tripId;
	}

	public int getStopSequence() {
		return stopSequence;
	}

	public int getStopId() {
		return stopId;
	}

}
