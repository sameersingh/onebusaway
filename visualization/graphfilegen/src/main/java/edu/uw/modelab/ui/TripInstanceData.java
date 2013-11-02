package edu.uw.modelab.ui;

import edu.uw.modelab.utils.Utils;

public class TripInstanceData {

	private final String arrivalTime;
	private final String scheduledTime;
	private final long scheduledError;
	private long obaError;
	private long modeError;

	public TripInstanceData(final String arrivalTime,
			final String scheduledTime, final long obaError,
			final long modeError) {
		this.arrivalTime = arrivalTime;
		this.scheduledTime = scheduledTime;
		scheduledError = Utils.diff(arrivalTime, scheduledTime);
		this.obaError = obaError;
		this.modeError = modeError;
	}

	public long getScheduledError() {
		return scheduledError;
	}

	public long getObaError() {
		return obaError;
	}

	public void setObaError(final long obaError) {
		this.obaError = obaError;
	}

	public long getModeError() {
		return modeError;
	}

	public void setModeError(final long modeError) {
		this.modeError = modeError;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public String getScheduledTime() {
		return scheduledTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result)
				+ ((arrivalTime == null) ? 0 : arrivalTime.hashCode());
		result = (prime * result) + (int) (modeError ^ (modeError >>> 32));
		result = (prime * result) + (int) (obaError ^ (obaError >>> 32));
		result = (prime * result)
				+ (int) (scheduledError ^ (scheduledError >>> 32));
		result = (prime * result)
				+ ((scheduledTime == null) ? 0 : scheduledTime.hashCode());
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
		final TripInstanceData other = (TripInstanceData) obj;
		if (arrivalTime == null) {
			if (other.arrivalTime != null) {
				return false;
			}
		} else if (!arrivalTime.equals(other.arrivalTime)) {
			return false;
		}
		if (modeError != other.modeError) {
			return false;
		}
		if (obaError != other.obaError) {
			return false;
		}
		if (scheduledError != other.scheduledError) {
			return false;
		}
		if (scheduledTime == null) {
			if (other.scheduledTime != null) {
				return false;
			}
		} else if (!scheduledTime.equals(other.scheduledTime)) {
			return false;
		}
		return true;
	}

}
