package edu.uw.modelab.utils;

import edu.uw.modelab.utils.Mercator;

public class Mercator {

	public static double y2lat(final double aY) {
		return Math.toDegrees((2 * Math.atan(Math.exp(Math.toRadians(aY))))
				- (Math.PI / 2));
	}

	public static double lat2y(final double aLat) {
		return Math.toDegrees(Math.log(Math.tan((Math.PI / 4)
				+ (Math.toRadians(aLat) / 2))));
	}

	public static double lon2x(final double aLon) {
		return 0;
	}

	public static double x2lon() {
		return 0;
	}

	public static void main(final String[] args) {
		System.out.println(Mercator.lat2y(-77.679863));
		System.out.println(Mercator.y2lat(Mercator.lat2y(Mercator
				.lat2y(47.6218643188477))));
	}
}
