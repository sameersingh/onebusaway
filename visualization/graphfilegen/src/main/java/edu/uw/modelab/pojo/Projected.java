package edu.uw.modelab.pojo;

public class Projected<T> {

	private final T base;
	private final double x;
	private final double y;

	public Projected(final T base, final double x, final double y) {
		this.base = base;
		this.x = x;
		this.y = y;
	}

	public T getBase() {
		return base;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}
