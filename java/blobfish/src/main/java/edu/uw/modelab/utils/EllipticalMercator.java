package edu.uw.modelab.utils;

/**
 * 
 * Taken from http://wiki.openstreetmap.org/wiki/Mercator#Java
 * 
 */
public class EllipticalMercator {

	private EllipticalMercator() {
		// avoid instantiation
	}

	final private static double R_MAJOR = 6378137.0;
	final private static double R_MINOR = 6356752.3142;

	public static double[] merc(final double x, final double y) {
		return new double[] { mercX(x), mercY(y) };
	}

	public static double mercX(final double lon) {
		return R_MAJOR * Math.toRadians(lon);
	}

	public static double mercY(double lat) {
		if (lat > 89.5) {
			lat = 89.5;
		}
		if (lat < -89.5) {
			lat = -89.5;
		}
		final double temp = R_MINOR / R_MAJOR;
		final double es = 1.0 - (temp * temp);
		final double eccent = Math.sqrt(es);
		final double phi = Math.toRadians(lat);
		final double sinphi = Math.sin(phi);
		double con = eccent * sinphi;
		final double com = 0.5 * eccent;
		con = Math.pow(((1.0 - con) / (1.0 + con)), com);
		final double ts = Math.tan(0.5 * ((Math.PI * 0.5) - phi)) / con;
		final double y = 0 - (R_MAJOR * Math.log(ts));
		return y;
	}

	public static void main(final String[] args) {
		final double test1 = mercY(47.6183014);
		final double test2 = mercX(-122.342506);
		System.out.println(test1);
		System.out.println(test2);
		final double test3 = mercY(47.6218643188477);
		final double test4 = mercX(-122.342376708984);
		System.out.println(test3);
		System.out.println(test4);
		System.out.println();
		final double y0 = mercY(47.6345634);
		final double x0 = mercX(-122.370468);
		final double y1 = mercY(47.6323738);
		final double x1 = mercX(-122.370506);
		final double y2 = mercY(47.6308479);
		final double x2 = mercX(-122.369949);
		System.out.println(y0);
		System.out.println(x0);
		System.out.println(y1);
		System.out.println(x1);
		System.out.println(y2);
		System.out.println(x2);
		System.out.println(Utils.euclideanDistance(x0, x1, y0, y1));
		System.out.println(Utils.euclideanDistance(x0, x2, y0, y2));
		System.out.println(Utils.euclideanDistance(x1, x2, y1, y2));

	}
}