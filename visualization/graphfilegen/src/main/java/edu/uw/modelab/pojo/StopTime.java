package edu.uw.modelab.pojo;

public class StopTime {

	private final String schedArrivalTime;
	private final String schedDepartureTime;
	private final int stopSequence;

	public StopTime(final String arrivalTime, final String departureTime,
			final int stopSequence) {
		this.schedArrivalTime = arrivalTime;
		this.schedDepartureTime = departureTime;
		this.stopSequence = stopSequence;
	}

	public int getStopSequence() {
		return stopSequence;
	}

	public String getSchedArrivalTime() {
		return schedArrivalTime;
	}

	public String getSchedDepartureTime() {
		return schedDepartureTime;
	}

	@Override
	public String toString() {
		return "{arrival=" + schedArrivalTime + ",departure:"
				+ schedDepartureTime + "}";
	}

}
