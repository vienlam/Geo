package eurecom.geo.rest.converter;

import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/converter")
public class Converter {
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnTitle() {
		return "This is a converter";
	}
	
	@Path("/WGS84Lambert93")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String convertWGS84ToLambert93 (@QueryParam("lon") double lon, @QueryParam("lat") double lat) {
		Point<Double> result = convertGeographicToPlaneLambert(lon, lat, LambertParams.Lambert93);
		return result.x + " " + result.y;
	}
	
	@Path("/Lambert93WGS84")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String convertLambert93ToWGS84 (@QueryParam("x") double x, @QueryParam("y") double y) {
		Point<Double> result = convertPlaneLambertToGeographic(x, y, LambertParams.Lambert93);
		return Math.toDegrees(result.x) + " " + Math.toDegrees(result.y);
	}	

	// Algorithm 1
	private double calculateIsometricLatitude(double lat, double e) {
		double t = Math.tan((Math.PI/4) + (lat/2));
		t = t * Math.pow((1 - e * Math.sin(lat))/(1 + e * Math.sin(lat)), e/2);
		return Math.log(t);
	}

	// Algorithm 2
	private double calculateLatitudeFromIsoLat(double isolat, double e, double tol) {
		double phi_p = 2 * Math.atan(Math.exp(isolat)) - (Math.PI / 2);
		double phi_c = 0;
		do {
			phi_c = 2 * Math.atan(Math.pow((1 + e * Math.sin(phi_p))/(1 - e * Math.sin(phi_p)), e/2) * Math.exp(isolat)) - (Math.PI / 2);
			if (Math.abs(phi_c - phi_p) > tol) {
				phi_p = phi_c;
			} else {
				break;
			}
		} while(true);
		return phi_c;
	}
	
	// Algorithm 3
	private Point<Double> convertGeographicToPlaneLambert(double lon, double lat, Map<String,Double> params) {
		Point<Double> result = new Point<>();
		
		double lonRad = Math.toRadians(lon);
		double latRad = Math.toRadians(lat);
		
		double il = calculateIsometricLatitude(latRad, LambertParams.Lambert93.get("e"));
		
		result.x = LambertParams.Lambert93.get("xs") + (LambertParams.Lambert93.get("c") * Math.exp(-1 * LambertParams.Lambert93.get("n") * il) * Math.sin(LambertParams.Lambert93.get("n") * (lonRad - LambertParams.Lambert93.get("lamda0Green"))));
		result.y = LambertParams.Lambert93.get("ys") - (LambertParams.Lambert93.get("c") * Math.exp(-1 * LambertParams.Lambert93.get("n") * il) * Math.cos(LambertParams.Lambert93.get("n") * (lonRad - LambertParams.Lambert93.get("lamda0Green"))));
		
		return result;
	}

	
	// Algorithm 4
	private Point<Double> convertPlaneLambertToGeographic(double x, double y, Map<String,Double> params) {
		Point<Double> result = new Point<>();
		
		double r = Math.sqrt(Math.pow(x - LambertParams.Lambert93.get("xs"), 2) + Math.pow(y - LambertParams.Lambert93.get("ys"), 2));
		double gamma = Math.atan((x - LambertParams.Lambert93.get("xs")) / (LambertParams.Lambert93.get("ys") - y));
		result.x = LambertParams.Lambert93.get("lamda0Green") + (gamma / LambertParams.Lambert93.get("n"));
		double il = (-1 / LambertParams.Lambert93.get("n") )* Math.log(Math.abs(r/LambertParams.Lambert93.get("c")));
		result.y = calculateLatitudeFromIsoLat(il, LambertParams.Lambert93.get("e"), Math.pow(10.0, -11));
		
		return result;
	}

	// Algorithm 21
	/*private double calculateEllipsoidGrandNormal(double lat, double a, double e) {
		double N = a / Math.sqrt(1 - (e * e * Math.pow(Math.sin(lat), 2)));
		return N;
	}

	// Algorithm 54
	private void lambertSecanteParams(double a, double e, double lamda0, double phi0, double phi1, double phi2, double x0, double y0) {
		lamdaC = lamda0;
		n1 = calculateEllipsoidGrandNormal(phi1, a, e);
		n2 = calculateEllipsoidGrandNormal(phi2, a, e);
		il1 = calculateIsometricLatitude(phi1, e);
		il2 = calculateIsometricLatitude(phi2, e);
		
		n = Math.log((n2 * Math.cos(phi2))/(n1 * Math.cos(phi1))) / (il1 - il2);
		c = n1 * Math.cos(phi1) * Math.exp(n * il1) / n;
		if(phi0.toFixed(4) == (Math.PI/2).toFixed(4)) {
			xs = x0;
			ys = y0;
		} else {
			xs = x0;
			ys = y0 + c * Math.exp(n * calculateIsometricLatitude(phi0, e));
		}
		
		return {
			lamdaC: lamdaC,
			e: e,
			n: n,
			c: c,
			xs: xs,
			ys: ys
		};
	}*/
}
