package edu.uw.modelab.utils;

public class Utils {

	private Utils() {
	}

	public static String unquote(final String str) {
		return str.replace("\"", "");
	}

}
