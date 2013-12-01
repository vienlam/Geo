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
			put("n", 6_375_585.7452); // Using IAG GRS 80 Ellipsoid 0.9996 * a
			put("xs", 500_000.0);
			put("ys", 0.0);
		}};
		
		@SuppressWarnings("serial")
		public static Map<String, Double> South = new HashMap<String, Double>() {{
			put("n", 6_375_585.7452); // Using IAG GRS 80 Ellipsoid 0.9996 * a
			put("xs", 500_000.0);
			put("ys", 10_000_000.0);
		}};
	}
	
	public static Point<Double> toWGS84 (double x, double y, double zone, String hem) {
		Point<Double> result = new Point<>();
		double n = 0.9996 * WGS84Converter.a;
		double lonori = zone * 6 - 183;
		double lonoriRad = Math.toRadians(lonori);
		
		Map<String, Double> params;
		if(hem.equalsIgnoreCase("S") || hem.equalsIgnoreCase("South")) {
			params = Params.South;
		} else if (hem.equalsIgnoreCase("N") || hem.equalsIgnoreCase("North")) {
			params = Params.North;
		} else {
			return null;
		}
		
		List<Double> coefs = calculateCoefficientsForInverseProjection(WGS84Converter.e);
				
		double re = (y - params.get("ys")) / (n * coefs.get(0));
		double im = (x - params.get("xs")) / (n * coefs.get(0));
		
		for(int k = 1 ; k < coefs.size() ; k++) {
			re -= n * coefs.get(k) * Math.sin(2 * k * re);
			im -= n * coefs.get(k) * Math.sin(2 * k * im);
		}
		
		double lon = lonoriRad + Math.atan(Math.sin(im) / Math.cos(re));
		double phi = Math.asin(Math.sin(re) / Math.cos(im));
		
		double iso = Utilities.calculateIsometricLatitude(phi, 0);
		double lat = Utilities.calculateLatitudeFromIsoLat(iso, WGS84Converter.e, Math.pow(10.0, -11));
		
		result.x = lon;
		result.y = lat;
		
		return result;
	}
	
	/*
	private List<Double> calculateCoefficientsForMeridionalArc (double e) {
		List<Double> coefs = new ArrayList<>();
		double c = 0;
		double e2 = e * e;
		double e4 = e2 * e2;
		double e6 = e4 * e2;
		double e8 = e6 * e2;
		
		// Coefficient 1
		c = 1 - (1 / 4) * e2 - (3 / 64) * e4 - (5 / 256) * e6 - (175 / 16384) * e8;
		coefs.add(c);
		// Coefficient 2
		c = -(3 / 8) * e2 - (3 / 12) * e4 - (45 / 1024) * e6 - (105 / 4096) * e8;
		coefs.add(c);
		// Coefficient 3
		c = (15 / 256) * e4 + (45 / 1024) * e6 + (105 / 4096) * e8;
		coefs.add(c);
		// Coefficient 4
		c = -(35 / 3072) * e6 - (175 / 12288) * e8;
		coefs.add(c);
		// Coefficient 5
		c = (315 / 131072) * e8;
		coefs.add(c);
		
		return coefs;
	}
	
	private double calculateMeridionalArc (double lat, double e) {
		double result;

		List<Double> coefs = calculateCoefficientsForMeridionalArc(e);

		result = coefs.get(0) * lat;
		for (int k = 1; k < coefs.size(); k++) {
			double c = coefs.get(k);
			result += c * Math.sin(2 * k * lat);
		}

		return result;
	}
	*/
	
	public static final List<Double> calculateCoefficientsForProjection (double e) {
		List<Double> coefs = new ArrayList<>();
		double c = 0;
		double e2 = e * e;
		double e4 = e2 * e2;
		double e6 = e4 * e2;
		double e8 = e6 * e2;
		
		// Coefficient 1
		c = 1 - (1 / 4) * e2 - (3 / 64) * e4 - (5 / 256) * e6 - (175 / 16384) * e8;
		coefs.add(c);
		// Coefficient 2
		c = (1 / 8) * e2 - (1 / 96) * e4 - (9 / 1024) * e6 - (901 / 184320) * e8;
		coefs.add(c);
		// Coefficient 3
		c = (13 / 768) * e4 + (17 / 5120) * e6 - (311 / 737280) * e8;
		coefs.add(c);
		// Coefficient 4
		c = (61 / 15360) * e6 + (899 / 430080) * e8;
		coefs.add(c);
		// Coefficient 5
		c = (49561 / 41287680) * e8;
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
		c = 1 - (1 / 4) * e2 - (3 / 64) * e4 - (5 / 256) * e6 - (175 / 16384) * e8;
		coefs.add(c);
		// Coefficient 2
		c = (1 / 8) * e2 + (1 / 48) * e4 + (7 / 2048) * e6 + (1 / 61440) * e8;
		coefs.add(c);
		// Coefficient 3
		c = (1 / 768) * e4 + (3 / 1280) * e6 + (599 / 268640) * e8;
		coefs.add(c);
		// Coefficient 4
		c = (17 / 30720) * e6 + (283 / 430080) * e8;
		coefs.add(c);
		// Coefficient 5
		c = (4397 / 41287680) * e8;
		coefs.add(c);
		
		return coefs;
	}
}
