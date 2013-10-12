package edu.uw.modelab.pojo;

public class TripInstance {
	private final int tripId;
	private final long serviceDate;

	public TripInstance(final int tripId, final long serviceDate) {
		this.tripId = tripId;
		this.serviceDate = serviceDate;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TripInstance)) {
			return false;
		}
		final TripInstance bpk = (TripInstance) obj;
		return (this.tripId == bpk.tripId)
				&& (this.serviceDate == bpk.serviceDate);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + (int) (serviceDate ^ (serviceDate >>> 32));
		result = (prime * result) + tripId;
		return result;
	}

}
