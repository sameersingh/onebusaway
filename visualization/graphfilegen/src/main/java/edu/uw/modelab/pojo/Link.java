package edu.uw.modelab.pojo;

public class Link {

	private final int from;
	private final int to;

	public Link(final int from, final int to) {
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + from;
		result = (prime * result) + to;
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
		final Link other = (Link) obj;
		if (from != other.from) {
			return false;
		}
		if (to != other.to) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "{from:" + from + ",to:" + to + "}";
	}

}
