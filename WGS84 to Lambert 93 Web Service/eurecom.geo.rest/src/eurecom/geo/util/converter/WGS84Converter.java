package eurecom.geo.util.converter;

import java.util.List;
import java.util.Map;

import eurecom.geo.util.converter.LambertConverter.*;
import eurecom.geo.util.converter.UTMConverter.*;

public class WGS84Converter {

	public static final double e = 0.081_819_191_042_831_85;//0.081_819_190_928_906_24;//0.081_819_191_119_888_33; //GRS80
	public static final double a = 6_378_137.0;
	
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
	
	/* Project WGS84 to UTM Coordinate
	 * @param lon: longitude coordinate
	 * @param lat: latitude coordinate
	 * @return UTMCoord: UTM coordinate structure which contains the x, y, zone, and hemisphere
	 * */
	public static UTMCoord toUTM(double lon, double lat) {
		UTMCoord result;
		
		// Determine the hemisphere in which the WGS84 coordinate is in
		if (lat < 0) {
			// Project to UTM with parameter for South hemisphere
			// Go to algorithm 30
			result = convertGeographicToUTM(lon, lat, UTMConverter.Params.South);
			result.hem = "South";
		} else {
			// Project to UTM with parameter for North hemisphere
			// Go to algorithm 30
			result = convertGeographicToUTM(lon, lat, UTMConverter.Params.North);
			result.hem = "North";
		}
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
	
	
	// Algorithm 30
	/* Project WGS84 coordinate onto UTM
	 * @param lon: longitude
	 * @param lat: latitude
	 * @param params: parameters for UTM projection
	 * */
	private static UTMCoord convertGeographicToUTM (double lon, double lat, Map<String, Double> params) {
		UTMCoord result = new UTMCoord();
		
		// Parameter n for UTM projection
		// a is the semi-major axis of GRS80 ellipsoid
		double n = 0.9996 * a; // GRS 80, 0.9996 * a
		
		// Determine the zone of the coodinate
		double zone = Math.floor((lon + 180)/6) + 1; 
		if( lat >= 56.0 && lat < 64.0 && lon >= 3.0 && lon < 12.0 ) {
		    zone = 32;
		}
		// Special zones for Svalbard
		if( lat >= 72.0 && lat < 84.0 ) {
		  if  ( lon >= 0.0  && lon <  9.0 ) { 
		    zone = 31;
		  } else if( lon >= 9.0  && lon < 21.0 ){
		    zone = 33;
		  } else if(lon >= 21.0 && lon < 33.0 ){
		    zone = 35;
		  } else if(lon >= 33.0 && lon < 42.0 ){ 
		    zone = 37;
		  }
		}
		
		// the origin longitude parameter for UTM projection
		double lonori = zone * 6 - 183;
		
		// Convert all coordinates to radian for calculation
		double lonRad = Math.toRadians(lon);
		double latRad = Math.toRadians(lat);
		double lonoriRad = Math.toRadians(lonori);
		
		// Calculate needed coefficients for projection
		List<Double> coefs = UTMConverter.calculateCoefficientsForProjection(e);
		
		// Calculate isometric latitude from latitude and first eccentricity of 
		// GRS80 ellipsoid, needed for calculate the lamda later
		double iso = Utilities.calculateIsometricLatitude(latRad, e);
		// needed for calculate isos later
		double phi = Math.asin(Math.sin(lonRad - lonoriRad) / Math.cosh(iso));
		
		// Needed for calculate UTM coordinate
		double isos = Utilities.calculateIsometricLatitude(phi, 0);
		double lamda = Math.atan(Math.sinh(iso) / Math.cos(lonRad - lonoriRad));
		
		// Calculate the resulting UTM coordinate
		double x = n * coefs.get(0) * isos;
		double y = n * coefs.get(0) * lamda;
		
		for(int k = 1 ; k < coefs.size() ; k++) {
			x += n * coefs.get(k) * Math.sin(2 * k * isos);
			y += n * coefs.get(k) * Math.sin(2 * k * lamda);
		}
		
		x += params.get("xs");
		y += params.get("ys");
		
		// Get the resulting UTM coordinate
		result.x = x;
		result.y = y;
		result.zone = (int) zone;
		
		return result;
	}
}