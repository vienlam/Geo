package eurecom.geo.util.converter;

import java.util.Map;

import eurecom.geo.util.converter.LambertConverter.LambertParams;

public class WGS84Converter {

	public static Point<Double> toLambert93(double lon, double lat) {
		Point<Double> result = convertGeographicToPlaneLambert(lon, lat, LambertParams.Lambert93);
		
		return result;
	}
	
	// Algorithm 3
		private static Point<Double> convertGeographicToPlaneLambert(double lon,
				double lat, Map<String, Double> params) {
			Point<Double> result = new Point<>();

			double lonRad = Math.toRadians(lon);
			double latRad = Math.toRadians(lat);

			double il = Utilities.calculateIsometricLatitude(latRad,
					LambertParams.Lambert93.get("e"));

			result.x = LambertParams.Lambert93.get("xs")
					+ (LambertParams.Lambert93.get("c")
							* Math.exp(-1 * LambertParams.Lambert93.get("n") * il) * Math
								.sin(LambertParams.Lambert93.get("n")
										* (lonRad - LambertParams.Lambert93
												.get("lamda0Green"))));
			result.y = LambertParams.Lambert93.get("ys")
					- (LambertParams.Lambert93.get("c")
							* Math.exp(-1 * LambertParams.Lambert93.get("n") * il) * Math
								.cos(LambertParams.Lambert93.get("n")
										* (lonRad - LambertParams.Lambert93
												.get("lamda0Green"))));

			return result;
		}
	
}
