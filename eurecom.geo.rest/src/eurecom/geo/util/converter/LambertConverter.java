package eurecom.geo.util.converter;

import java.util.HashMap;
import java.util.Map;

public class LambertConverter {
	
	// Lambert projection's parameters
	public static class LambertParams {
		
		@SuppressWarnings("serial")
		public static final Map<String, Double> Lambert93 = new HashMap<String, Double>() {{
			put("lamda0Green", 0.052_359_877_559_83); // 3 degrees
			put("lamda0Paris", 0.0);
			put("n", 0.725_607_7650);
			put("c", 11_754_255.426);
			put("e", 0.081_819_191_042_815_79);
			put("xs", 700_000.0);
			put("ys", 12_655_612.050);
		}};
		
		@SuppressWarnings("serial")
		public static final Map<String, Double> LambertI = new HashMap<String, Double>() {{
			put("lamda0Green", 0.052_359_877_559_83); // 3 degrees
			put("lamda0Paris", 0.0);
			put("n", 0.760_405_965_6);
			put("c", 11_603_796.98);
			put("e", 0.081_819_191_042_815_79);
			put("xs", 600_000.0);
			put("ys", 5_657_616.674);
		}};
		
		@SuppressWarnings("serial")
		public static final Map<String, Double> LambertII = new HashMap<String, Double>() {{
			put("lamda0Green", 0.052_359_877_559_83); // 3 degrees
			put("lamda0Paris", 0.0);
			put("n", 0.728_968_627_4);
			put("c", 11_745_793.39);
			put("e", 0.081_819_191_042_815_79);
			put("xs", 600_000.0);
			put("ys", 6_199_695.768);
		}};
		
		@SuppressWarnings("serial")
		public static final Map<String, Double> LambertIII = new HashMap<String, Double>() {{
			put("lamda0Green", 0.052_359_877_559_83); // 3 degrees
			put("lamda0Paris", 0.0);
			put("n", 0.695_912_766_6);
			put("c", 11_947_992.52);
			put("e", 0.081_819_191_042_815_79);
			put("xs", 600_000.0);
			put("ys", 6_791_905.085);
		}};
		
		@SuppressWarnings("serial")
		public static final Map<String, Double> LambertIV = new HashMap<String, Double>() {{
			put("lamda0Green", 0.052_359_877_559_83); // 3 degrees
			put("lamda0Paris", 0.0);
			put("n", 0.671_267_932_2);
			put("c", 12_136_281.99);
			put("e", 0.081_819_191_042_815_79);
			put("xs", 234.358);
			put("ys", 7_239_161.542);
		}};
		
	}
	
	/* Convert to WGS84 from Lambert93
	 * @param x: The x coordinate of Lambert93
	 * @param y: The y coordinate of Lambert93
	 * @return The point which contains the longitude in x and latitude in y of WGS84
	 * */
	public static Point<Double> fromLambert93ToWGS84(double x, double y) {
		Point<Double> result = inverse(x, y, LambertParams.Lambert93);
		return result;
	}
	
	/* Convert to WGS84 from Lambert I
	 * @param x: The x coordinate of Lambert I
	 * @param y: The y coordinate of Lambert I
	 * @return The point which contains the longitude in x and latitude in y of WGS84
	 * */
	public static Point<Double> fromLambertIToWGS84(double x, double y) {
		Point<Double> result = inverse(x, y, LambertParams.LambertI);
		return result;
	}
	
	/* Convert to WGS84 from Lambert II
	 * @param x: The x coordinate of Lambert II
	 * @param y: The y coordinate of Lambert II
	 * @return The point which contains the longitude in x and latitude in y of WGS84
	 * */
	public static Point<Double> fromLambertIIToWGS84(double x, double y) {
		Point<Double> result = inverse(x, y, LambertParams.LambertII);
		return result;
	}
	
	/* Convert to WGS84 from Lambert III
	 * @param x: The x coordinate of Lambert III
	 * @param y: The y coordinate of Lambert III
	 * @return The point which contains the longitude in x and latitude in y of WGS84
	 * */
	public static Point<Double> fromLambertIIIToWGS84(double x, double y) {
		Point<Double> result = inverse(x, y, LambertParams.LambertIII);
		return result;
	}
	
	/* Convert to WGS84 from Lambert IV
	 * @param x: The x coordinate of Lambert IV
	 * @param y: The y coordinate of Lambert IV
	 * @return The point which contains the longitude in x and latitude in y of WGS84
	 * */
	public static Point<Double> fromLambertIVToWGS84(double x, double y) {
		Point<Double> result = inverse(x, y, LambertParams.LambertIV);
		return result;
	}


	// Algorithm 4
	private static Point<Double> inverse(double x, double y, Map<String, Double> params) {
		Point<Double> result = new Point<>();

		double r = Math.sqrt(Math.pow(x - params.get("xs"), 2) + Math.pow(y - params.get("ys"), 2));
		
		double gamma = Math.atan((x - params.get("xs"))	/ (params.get("ys") - y));
		
		result.x = params.get("lamda0Green") + (gamma / params.get("n"));
		
		double il = (-1 / params.get("n")) * Math.log(Math.abs(r / params.get("c")));
		
		result.y = Utilities.calculateLatitudeFromIsoLat(il, params.get("e"), Math.pow(10.0, -11));

		return result;
	}

	// Algorithm 21
	/*
	 * private double calculateEllipsoidGrandNormal(double lat, double a, double
	 * e) { double N = a / Math.sqrt(1 - (e * e * Math.pow(Math.sin(lat), 2)));
	 * return N; }
	 * 
	 * // Algorithm 54 private void lambertSecanteParams(double a, double e,
	 * double lamda0, double phi0, double phi1, double phi2, double x0, double
	 * y0) { lamdaC = lamda0; n1 = calculateEllipsoidGrandNormal(phi1, a, e); n2
	 * = calculateEllipsoidGrandNormal(phi2, a, e); il1 =
	 * calculateIsometricLatitude(phi1, e); il2 =
	 * calculateIsometricLatitude(phi2, e);
	 * 
	 * n = Math.log((n2 * Math.cos(phi2))/(n1 * Math.cos(phi1))) / (il1 - il2);
	 * c = n1 * Math.cos(phi1) * Math.exp(n * il1) / n; if(phi0.toFixed(4) ==
	 * (Math.PI/2).toFixed(4)) { xs = x0; ys = y0; } else { xs = x0; ys = y0 + c
	 * * Math.exp(n * calculateIsometricLatitude(phi0, e)); }
	 * 
	 * return { lamdaC: lamdaC, e: e, n: n, c: c, xs: xs, ys: ys }; }
	 */
}
