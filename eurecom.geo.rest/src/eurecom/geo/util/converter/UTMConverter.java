package eurecom.geo.util.converter;

import java.util.HashMap;
//import java.util.List;
import java.util.Map;
//import java.util.ArrayList;

public class UTMConverter {
	
	// The UTM coordinate structure
	public static final class UTMCoord {
		public double x;
		public double y;
		public int zone;
		public String hem; // hemisphere
		
		public UTMCoord() {
			x = -1;
			y = -1;
			zone = -1;
			hem = "";
		}
	}

	// Some constants
	public static class Params {
		@SuppressWarnings("serial")
		public static Map<String, Double> North = new HashMap<String, Double>() {{
			put("xs", 500_000.0);
			put("ys", 0.0);
		}};
		
		@SuppressWarnings("serial")
		public static Map<String, Double> South = new HashMap<String, Double>() {{
			put("xs", 500_000.0);
			put("ys", 10_000_000.0);
		}};
	}
	
	// The scale factor of UTM
	public static final double scaleFactor = 0.9996;
	
	/* Project geographic coordinates to UTM
	 * @param lon: longitude of geographic coordinates
	 * @param lat: latitude of geographic coordinates
	 * @param a: semi-major axis of geographic coordinates' ellipsoid
	 * @param b: semi-minor axis of geographic coordinates' ellipsoid
	 * @return UTMCoord: Structure contains UTM coordinate (x,y,zone, hemi)
	 * */
	public static UTMCoord convert(double lon, double lat, double a, double b) {
		// Holding result
		UTMCoord result = new UTMCoord();
		
		double n = (a - b) / (a + b);

		// First eccentricity of ellipsoid
		double e = Math.sqrt(1.0 - (b * b) / (a * a));
		double e1sqr = (e * e) / (1.0 - e * e);
		
		// Determine the zone of the coordinates
		double zone = getZone(Math.toDegrees(lon), Math.toDegrees(lat));

		// The central meridian of UTM
		double lon0 = getCentralMeridian(zone);

		// The difference between longitude and central meridian
		double delta_lon = lon - lon0;
		
		// The radius of curvature of the earth perpendicular to the meridian plane
		double nu = a / Math.sqrt((1.0 - e * e * Math.sin(lat) * Math.sin(lat)));
		
		// Coefficients for calculating x and y
		double K1 = calculateMeridionalArc(lat, n, a);
		
		double K2 = nu * Math.sin(2.0 * lat) / 4.0;
		
		double K3 = (nu * Math.sin(lat) * Math.pow(Math.cos(lat), 3.0) / 24.0)
				* (5.0 - Math.pow(Math.tan(lat), 2.0)
						+ 9.0 * e1sqr * e1sqr * Math.pow(Math.cos(lat), 2.0)
						+ 4.0 * Math.pow(e1sqr, 4.0) * Math.pow(Math.cos(lat), 4.0));
		
		double K4 = nu * Math.cos(lat);
		
		double K5 = (nu * Math.pow(Math.cos(lat), 3.0) / 6.0)
				* (1.0 - Math.pow(Math.tan(lat), 2.0) 
						+ e1sqr * e1sqr * Math.pow(Math.cos(lat), 2.0));
		
		// Define x and y of UTM coordinates
		double x = 0.0;
		double y = 0.0;
		
		// Calculate x and y
		x = K4 * delta_lon + K5 * Math.pow(delta_lon, 3.0);
		y = K1 + K2 * Math.pow(delta_lon, 2.0) + K3 * Math.pow(delta_lon, 4.0);

		// Scale the transverse mercator projection to UTM
		x = x * scaleFactor;
		y = y * scaleFactor;
		
		// Adjust easting and northing for UTM system
		if (lat < 0) {
			x += Params.South.get("xs");
			y += Params.South.get("ys");
			result.hem = "South";
		} else {
			x += Params.North.get("xs");
			y += Params.North.get("ys");
			result.hem = "North";
		}

		// Get the resulting UTM coordinate
		result.x = x;
		result.y = y;
		result.zone = (int) zone;
		
		// Return result
		return result;
	}
	
	/* Inverse projection UTM to geographic coordinates
	 * @param x: The easting of UTM coordinates
	 * @param y: The northing of UTM coordinates
	 * @param zone: The zone which the UTM coordinates reside in
	 * @param hem: The hemisphere in which the UTM coordinates reside
	 * @param a: semi-major axis of the target geographic coordinates' ellipsoid
	 * @param b: semi-minor axis of the target geographic coordinates' ellipsoid
	 * @return Point<Double>: Structure contains geographic coordinates (x,y) for (longitude, latitude)
	 * */
	public static Point<Double> inverse(double x, double y, double zone, String hem, double a, double b) {
		// Define the resulting coordinates structure
		Point<Double> result = new Point<Double>();
		
		// First eccentricity
		double e = Math.sqrt(1.0 - (b * b) / (a * a));
		double e1sqr = (e * e) / (1.0 - e * e);
		
		// Define adjusted easting and northing variables
		double x1 = x;
		double y1 = y;
		// Adjust easting and northing back
		if(hem.equalsIgnoreCase("S") || hem.equalsIgnoreCase("South")) {
			x1 = Params.South.get("xs") - x1;
			y1 = Params.South.get("ys") - y1;
		} else if (hem.equalsIgnoreCase("N") || hem.equalsIgnoreCase("North")) {
			x1 = Params.North.get("xs") - x1;
			//y1 = Params.North.get("ys") - y1;
		} else {
			return null;
		}
		
		// Scale x and y back to Transverse Mercator
		y1 = y1 / scaleFactor;		
		x1 = x1 / scaleFactor;
		
		// Central meridian
		double lon0 = Math.toRadians(zone * 6.0 - 183.0);
		
		// Footprint latitude
		double fplat = calculateFootprintLatitude(y1, a, e);
		
		double C1 = e1sqr * Math.pow(Math.cos(fplat), 2);;
		double T1 = Math.pow(Math.tan(fplat), 2);
		double R1 = a * (1.0 - e * e) / Math.pow(1.0 - e * e * Math.pow(Math.sin(fplat), 2), 3.0 / 2.0);
		double N1 = a / Math.sqrt(1.0 - e * e * Math.pow(Math.sin(fplat), 2));
		double D = x1 / N1;
		
		// Coefficients for calculating latitude and longitude
		double Q1 = N1 * Math.tan(fplat) / R1;
		double Q2 = D * D / 2.0;
		double Q3 = (5.0 + 3.0 * T1 + 10.0 * C1 - 4.0 * C1 * C1 - 9.0 * e1sqr) * Math.pow(D, 4) / 24.0;
		double Q4 = (61.0 + 90.0 * T1 + 298.0 * C1 + 45.0 * T1 * T1 - 3.0 * C1 * C1 - 252.0 * e1sqr) * Math.pow(D, 6) / 720.0;
		double Q5 = D;
		double Q6 = (1.0 + 2.0 * T1 + C1) * Math.pow(D, 3) / 6.0;
		double Q7 = (5.0 - 2.0 * C1 + 28.0 * T1 - 3.0 * C1 * C1 + 8.0 * e1sqr + 24.0 * T1 * T1) * Math.pow(D, 5) / 120.0;
		
		// Calculate latitude
		double lat = fplat - Q1 * (Q2 + Q3 + Q4);
		// Calculate longitude
		double lon = lon0 - (Q5 - Q6 + Q7) / Math.cos(fplat);
		
		// Return
		result.x = lon;
		result.y = lat;
		return result;
	}
	
	/* Calculate the meridional arc length
	 * @param lat: latitude of the geographic coordinates
	 * @param n: (a-b)/(a+b)
	 * @param a: semi-major axis of the ellipsoid
	 * @return: The meridinal arc length
	 * */
	private static final double calculateMeridionalArc(double lat, double n, double a) {
		// Define meridional arc length
		double S = 0.0;
		// Some predefined constants
		double n2 = Math.pow(n, 2.0);
		double n3 = Math.pow(n, 3.0);
		double n4 = Math.pow(n, 4.0);
		// Calculate coefficients for meridional arc length
		double A = a * ((1.0 - n) * (1.0 + (5.0 * n2/ 4.0) + (81.0 * n4 / 64.0)));
		double B = (3.0 * a * n / 2.0) * ((1.0 - n) * (1.0 + (7.0 * n2 / 8.0) + (55.0 * n4 / 64.0)));
		double C = (15.0 * a * n2 / 16.0) * ((1.0 - n) * (1.0 + (3.0 * n2 / 4.0)));
		double D = (35.0 * a * n3 / 48.0) * ((1.0 - n) * (1.0 + (11.0 * n2 / 16.0)));
		double E = (315.0 * a * n4 / 512.0) * (1.0 - n);
		// Calculate meridional arc length
		S = A * lat - B * Math.sin(2.0 * lat) + C * Math.sin(4.0 * lat) - D * Math.sin(6.0 * lat) + E * Math.sin(8.0 * lat);
		
		return S;
	}
	
	/* Get the zone of UTM coordinates by given geographic coordinates
	 * @param lon: longitude of the geographic coordinates
	 * @param lat: latitude of the geographic coordinates
	 * @return: The zone which the resulting UTM coordinates will reside in
	 * */
	private static final double getZone(double lon, double lat) {
		double zone = 0.0;
		
		zone = Math.floor((lon + 180.0) / 6.0) + 1.0;
		
		if (lat >= 56.0 && lat < 64.0 && lon >= 3.0 && lon < 12.0) {
			zone = 32;
		}
		// Special zones for Svalbard
		if (lat >= 72.0 && lat < 84.0) {
			if (lon >= 0.0 && lon < 9.0) {
				zone = 31;
			} else if (lon >= 9.0 && lon < 21.0) {
				zone = 33;
			} else if (lon >= 21.0 && lon < 33.0) {
				zone = 35;
			} else if (lon >= 33.0 && lon < 42.0) {
				zone = 37;
			}
		}
		
		return zone;
	}
	
	/* Calculate the central meridian of UTM by given zone.
	 * @param zone: The zone of the UTM coordinates
	 * @return: The cenral meridian
	 * */
	private static final double getCentralMeridian(double zone) {
		double c = 0.0;
		
		c = (zone * 6.0) - 183.0;
		
		return Math.toRadians(c);
	}
	
	/* Calculate footprint latitude for inverse projection.
	 * @param M: The meridional arc length
	 * @param a: The semi-major axis of ellipsoid
	 * @param e: The first eccentricity of ellipsoid
	 * @return: The footprint latitude
	 * */
	private static final double calculateFootprintLatitude(double M, double a, double e) {
		// Define footprint latitude
		double fp = 0.0;
		
		// Two parameters for calculate coefficients and footprint
		double mu = M / (a * (1.0 - e * e / 4.0 - 3.0 * Math.pow(e, 4) / 64.0 - 5.0 * Math.pow(e, 6) / 256.0));
		double e1 = (1.0 - Math.sqrt(1.0 - e * e)) / (1 + Math.sqrt(1.0 - e * e));
		
		// Coeffs for footprint
		double J1 = (3.0 * e1 / 2.0 - 27.0 * Math.pow(e1, 3) / 32.0);
		double J2 = (21.0 * e1 * e1 / 16.0 - 55.0 * Math.pow(e1, 4) / 32.0);
		double J3 = 151.0 * Math.pow(e1, 3) / 96.0;
		double J4 = 1097.0 * Math.pow(e1, 4) / 512.0;
		
		// Calculate footprint
		fp = mu + J1 * Math.sin(2.0 * mu) + J2 * Math.sin(4.0 * mu) + J3 * Math.sin(6.0 * mu) + J4 * Math.sin(8.0 * mu);
		
		return fp;
	}

}

