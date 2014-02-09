package eurecom.geo.util.converter;

//import java.util.List;
import java.util.Map;

import eurecom.geo.util.converter.LambertConverter.*;
//import eurecom.geo.util.converter.UTMConverter.*;

public class WGS84Converter {

	// (latitude < -90.0 || latitude > 90.0 || longitude < -180.0 || longitude >= 180.0)
	public static final double e = 0.081_819_191_042_831_85;//0.081_819_190_928_906_24;//0.081_819_191_119_888_33; //GRS80
	public static final double esqr = 6.694_379_990_13e-03;
	public static final double a = 6_378_137.0;
	public static final double b = 6_356_752.314;
	
	public static Point<Double> toLambert93(double lon, double lat) {
		Point<Double> result = convertGeographicToPlaneLambert(lon, lat, LambertParams.Lambert93);
		
		return result;
	}
	
	public static Point<Double> toLambertI(double lon, double lat) {
		Point<Double> result = convertGeographicToPlaneLambert(lon, lat, LambertParams.LambertI);
		
		return result;
	}
	
	public static Point<Double> toLambertII(double lon, double lat) {
		Point<Double> result = convertGeographicToPlaneLambert(lon, lat, LambertParams.LambertII);
		
		return result;
	}
	
	public static Point<Double> toLambertIII(double lon, double lat) {
		Point<Double> result = convertGeographicToPlaneLambert(lon, lat, LambertParams.LambertIII);
		
		return result;
	}
	
	public static Point<Double> toLambertIV(double lon, double lat) {
		Point<Double> result = convertGeographicToPlaneLambert(lon, lat, LambertParams.LambertIV);
		
		return result;
	}
	
	// Algorithm 3
	private static Point<Double> convertGeographicToPlaneLambert(double lon,
			double lat, Map<String, Double> params) {
		Point<Double> result = new Point<>();

		double lonRad = Math.toRadians(lon);
		double latRad = Math.toRadians(lat);

		double il = Utilities.calculateIsometricLatitude(latRad,
				params.get("e"));

		result.x = params.get("xs")
				+ (params.get("c") * Math.exp(-1 * params.get("n") * il) * Math
						.sin(params.get("n") * (lonRad - params.get("lamda0Green"))));
		result.y = params.get("ys")
				- (params.get("c") * Math.exp(-1 * params.get("n") * il) * Math
						.cos(params.get("n") * (lonRad - params.get("lamda0Green"))));

		return result;
	}
}