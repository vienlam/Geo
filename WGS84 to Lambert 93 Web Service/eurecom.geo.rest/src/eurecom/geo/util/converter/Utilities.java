package eurecom.geo.util.converter;

public class Utilities {

	// Algorithm 1
	/*
	 * Calculate Isometric Latitude
	 * 
	 * @param lat: Latitude coordinate
	 * 
	 * @param e: The eccentricity of the ellipsoid
	 * 
	 * @return isometric latitude
	 */
	public static double calculateIsometricLatitude(double lat, double e) {
		double t = Math.tan((Math.PI / 4) + (lat / 2));
		t = t * Math.pow((1 - e * Math.sin(lat)) / (1 + e * Math.sin(lat)),	e / 2);

		return Math.log(t);
	}

	// Algorithm 2
	/*
		 * */
	public static double calculateLatitudeFromIsoLat(double isolat, double e, double tol) {
		double phi_p = 2 * Math.atan(Math.exp(isolat)) - (Math.PI / 2);
		double phi_c = 0;
		do {
			phi_c = 2 * Math.atan(Math.pow((1 + e * Math.sin(phi_p)) / (1 - e * Math.sin(phi_p)), e / 2) * Math.exp(isolat)) - (Math.PI / 2);
			if (Math.abs(phi_c - phi_p) > tol) {
				phi_p = phi_c;
			} else {
				break;
			}
		} while (true);
		return phi_c;
	}
	
}
