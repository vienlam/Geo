package eurecom.geo.util.converter;

import java.util.List;
import java.util.Map;

import eurecom.geo.util.converter.LambertConverter.*;
import eurecom.geo.util.converter.UTMConverter.*;

public class WGS84Converter {

	public static final double e = 0.081_819_191_042_815_79;
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
	
	public static UTMCoord toUTM(double lon, double lat) {
		UTMCoord result;
		if (lat < 0) {
			result = convertGeographicToUTM(lon, lat, UTMConverter.Params.South);
			result.hem = "South";
		} else {
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
	private static UTMCoord convertGeographicToUTM (double lon, double lat, Map<String, Double> params) {
		UTMCoord result = new UTMCoord();
		double n = 0.9996 * a; // GRS 80, 0.9996 * a
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
		
		double lonori = zone * 6 - 183; // zone -1
		
		double lonRad = Math.toRadians(lon);
		double latRad = Math.toRadians(lat);
		double lonoriRad = Math.toRadians(lonori);
		
		List<Double> coefs = UTMConverter.calculateCoefficientsForProjection(e);
		
		double iso = Utilities.calculateIsometricLatitude(latRad, e);
		
		double phi = Math.asin(Math.sin(lonRad - lonoriRad) / Math.cosh(iso));
		
		double isos = Utilities.calculateIsometricLatitude(phi, 0);
		
		double lamda = Math.atan(Math.sinh(iso) / Math.cos(lonRad - lonoriRad));
		
		double x = n * coefs.get(0) * isos;
		double y = n * coefs.get(0) * lamda;
		
		for(int k = 1 ; k < coefs.size() ; k++) {
			x += n * coefs.get(k) * Math.sin(2 * k * isos);
			y += n * coefs.get(k) * Math.sin(2 * k * lamda);
		}
		
		x += params.get("xs");
		y += params.get("ys");
		
		result.x = x;
		result.y = y;
		result.zone = (int) zone;
		
		return result;
	}
}