package edu.uw.modelab.pojo;

public class Stop {

	private final long id;
	private final String name;

	public Stop(final long id, final String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
