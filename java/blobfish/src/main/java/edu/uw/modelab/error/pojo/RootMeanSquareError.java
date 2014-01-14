package edu.uw.modelab.error.pojo;

public class RootMeanSquareError {

	private final double oba;
	private final double our;

	public RootMeanSquareError(final double oba, final double our) {
		this.oba = oba;
		this.our = our;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(oba);
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(our);
		result = (prime * result) + (int) (temp ^ (temp >>> 32));
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
		final RootMeanSquareError other = (RootMeanSquareError) obj;
		if (Double.doubleToLongBits(oba) != Double.doubleToLongBits(other.oba)) {
			return false;
		}
		if (Double.doubleToLongBits(our) != Double.doubleToLongBits(other.our)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "" + oba + "\t" + our;
	}

}
