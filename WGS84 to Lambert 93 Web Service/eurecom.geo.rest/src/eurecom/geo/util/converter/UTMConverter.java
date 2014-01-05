package eurecom.geo.util.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class UTMConverter {
	public static final class UTMCoord {
		public double x;
		public double y;
		public int zone;
		public String hem;
		
		public UTMCoord() {
			x = -1;
			y = -1;
			zone = -1;
			hem = "";
		}
	}
	
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
	
	/* Convert to WGS84 coordinate
	 * @param x: the x coordinate of UTM
	 * @param y: the y coordinate of UTM
	 * @param zone: the zone of the UTM coordinate
	 * @param hem: the hemisphere in which the coordinate is
	 * @return Point: a point contains the WGS84 coordinate
	 * */
	public static Point<Double> toWGS84 (double x, double y, double zone, String hem) {
		Point<Double> result = new Point<>();
		
		// Calculate the n parameter from the semi-major axis of GRS80 ellisoid
		double n = 0.9996 * WGS84Converter.a;
		// Calculate the origin longitude
		double lonori = zone * 6 - 183;
		double lonoriRad = Math.toRadians(lonori);
		
		// Get the needed parameter for UTM inverse conver
		Map<String, Double> params;
		if(hem.equalsIgnoreCase("S") || hem.equalsIgnoreCase("South")) {
			params = Params.South;
		} else if (hem.equalsIgnoreCase("N") || hem.equalsIgnoreCase("North")) {
			params = Params.North;
		} else {
			return null;
		}
		
		// Calculate the required coefficients for inverse projection from the first
		// eccentricity of GRS80 ellipsoid
		List<Double> coefs = calculateCoefficientsForInverseProjection(WGS84Converter.e);
		
		// Calculate the WGS84 coordinate
		double re = (y - params.get("ys")) / (n * coefs.get(0));
		double im = (x - params.get("xs")) / (n * coefs.get(0));
		double rep = 0, imp = 0;
		
		for(int k = 1 ; k < coefs.size() ; k++) {
			rep += coefs.get(k) * Math.sin(2 * k * re);
			imp += coefs.get(k) * Math.sin(2 * k * im);
		}
		
		rep = re - rep;
		imp = im - imp;
		
		double lon = lonoriRad + Math.atan(Math.sinh(imp) / Math.cos(rep));
		
		double phi = Math.asin(Math.sin(rep) / Math.cosh(imp));
		double iso = Utilities.calculateIsometricLatitude(phi, 0);
		double lat = Utilities.calculateLatitudeFromIsoLat(iso, WGS84Converter.e, Math.pow(10.0, -11));
		
		result.x = lon;
		result.y = lat;
		
		return result;
	}
	
	public static final List<Double> calculateCoefficientsForProjection (double e) {
		List<Double> coefs = new ArrayList<>();
		double c = 0;
		double e2 = e * e;
		double e4 = e2 * e2;
		double e6 = e4 * e2;
		double e8 = e6 * e2;
		
		// Coefficient 1
		c = 1.0 - (1.0 / 4) * e2 - (3.0 / 64) * e4 - (5.0 / 256) * e6 - (175.0 / 16384) * e8;
		coefs.add(c);
		// Coefficient 2
		c = (1.0 / 8) * e2 - (1.0 / 96) * e4 - (9.0 / 1024) * e6 - (901.0 / 184320) * e8;
		coefs.add(c);
		// Coefficient 3
		c = (13.0 / 768) * e4 + (17.0 / 5120) * e6 - (311.0 / 737280) * e8;
		coefs.add(c);
		// Coefficient 4
		c = (61.0 / 15360) * e6 + (899.0 / 430080) * e8;
		coefs.add(c);
		// Coefficient 5
		c = (49561.0 / 41287680.0) * e8;
		coefs.add(c);
		
		return coefs;
	}
	
	private static List<Double> calculateCoefficientsForInverseProjection (double e) {
		List<Double> coefs = new ArrayList<>();
		double c = 0;
		double e2 = e * e;
		double e4 = e2 * e2;
		double e6 = e4 * e2;
		double e8 = e6 * e2;
		
		// Coefficient 1
		c = 1.0 - (1.0 / 4) * e2 - (3.0 / 64) * e4 - (5.0 / 256) * e6 - (175.0 / 16384) * e8;
		coefs.add(c);
		// Coefficient 2
		c = (1.0 / 8) * e2 + (1.0 / 48) * e4 + (7.0 / 2048) * e6 + (1.0 / 61440) * e8;
		coefs.add(c);
		// Coefficient 3
		c = (1.0 / 768) * e4 + (3.0 / 1280) * e6 + (559.0 / 368640) * e8;
		coefs.add(c);
		// Coefficient 4
		c = (17.0 / 30720) * e6 + (283.0 / 430080) * e8;
		coefs.add(c);
		// Coefficient 5
		c = (4397.0 / 41287680) * e8;
		coefs.add(c);
		
		return coefs;
	}
}
