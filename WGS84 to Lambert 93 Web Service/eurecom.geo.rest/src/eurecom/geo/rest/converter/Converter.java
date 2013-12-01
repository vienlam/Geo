package eurecom.geo.rest.converter;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import eurecom.geo.util.converter.*;
import eurecom.geo.util.converter.UTMConverter.*;

import org.apache.commons.io.*;

@Path("/converter")
public class Converter {
	
	public static final String newLine = System.getProperty("line.separator");
	
	/* Get data from given source
	 * @param source: The source's URI
	 * @return List of lines from source
	 * */
	private List<String> getDataFromSource(String source, String encode) {
		URL website;
		URLConnection connection;
		InputStream in;
		List<String> out;
		/*ReadableByteChannel rbc;
		FileOutputStream fos;*/
		
		// Need to check the encode to support
		
		try {
			
			// Create a URL from source
			website = new URL(source);
			// Open connection to fetch the resource
			connection = website.openConnection();
			// Get resource stream
			in = connection.getInputStream();
			// Read resource line by line
			out = IOUtils.readLines(in, encode);
			
			return out;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}
	
	/* Write string to file
	 * @param fileName: Name of the file
	 * @param encode: File encoding
	 * @return File
	 * */
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnTitle() {
		return "This is a converter";
	}
	
	@Path("/WGS84Lambert93")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String convertWGS84ToLambert93 (@QueryParam("lon") double lon, @QueryParam("lat") double lat) {
		Point<Double> result = WGS84Converter.toLambert93(lon, lat);
		return result.x + " " + result.y;
	}
	
	@Path("/WGS84Lambert93/file")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String convertWGS84ToLambert93 (@QueryParam("source") String source, @QueryParam("encode") String encode) {
		String result = "";
		
		List<String> data = getDataFromSource(source, encode);
		// Convert each coordinates in data and write to output string
		// Define the number format that we want to use which is US
		NumberFormat _format = NumberFormat.getInstance(Locale.US);
		// Loop through all the list
		for (Iterator<String> i = data.iterator(); i.hasNext(); result += newLine) {
			String str = i.next();
			// Get the coordinates
			String[] coords = str.split(" ");
			// Check if there is line which does not conform to our format
			// Continue if not satisfy
			if (coords.length > 2) {
				continue;
			}
			try {
				// Convert to double
				Number nlong = _format.parse(coords[0]);
				Number nlat = _format.parse(coords[1]);
				// And do the converting
				Point<Double> lam = WGS84Converter.toLambert93(
						Double.parseDouble(nlong.toString()),
						Double.parseDouble(nlat.toString()));
				// Append to result string
				result += lam.x + " " + lam.y;
			} catch (ParseException e) {

			}
		}
		
		return result;
	}
	
	@Path("/Lambert93WGS84")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String convertLambert93ToWGS84 (@QueryParam("x") double x, @QueryParam("y") double y) {
		Point<Double> result = LambertConverter.fromLambert93ToWGS84(x, y);
		return Math.toDegrees(result.x) + " " + Math.toDegrees(result.y);
	}
	
	@Path("/Lambert93WGS84/file")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String convertLambert93ToWGS84 (@QueryParam("source") String source, @QueryParam("encode") String encode) {
		String result = "";
		
		List<String> data = getDataFromSource(source, encode);
		// Convert each coordinates in data and write to output string
		// Define the number format that we want to use which is US
		NumberFormat _format = NumberFormat.getInstance(Locale.US);
		// Loop through all the list
		for(Iterator<String> i = data.iterator() ; i.hasNext() ; result += newLine) {
			String str = i.next();
			// Get the coordinates
			String[] coords = str.split(" ");
			// Check if there is line which does not conform to our format
			// Continue if not satisfy
			if (coords.length > 2 || coords.length <= 0) {
				continue;
			}
			try {
				// Convert to double
				Number nx = _format.parse(coords[0]);
				Number ny = _format.parse(coords[1]);
				// And do the converting
				Point<Double> wgs = LambertConverter.fromLambert93ToWGS84(
						Double.parseDouble(nx.toString()), 
						Double.parseDouble(ny.toString()));
				// Append to result string
				result += Math.toDegrees(wgs.x) + " " + Math.toDegrees(wgs.y);
			} catch (ParseException e) {
				
			}
		}
		
		return result;
	}
	
	@Path("/WGS84WGS84UTM")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String convertWGS84ToWGS84UTM (@QueryParam("lon") double lon, @QueryParam("lat") double lat) {
		UTMCoord result = WGS84Converter.toUTM(lon, lat);
		return result.x + " " + result.y + " " + result.zone + " " + result.hem;
	}
	
	@Path("/WGS84WGS84UTM/file")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String convertWGS84ToWGS84UTM (@QueryParam("source") String source, @QueryParam("encode") String encode) {
		String result = "";
		
		List<String> data = getDataFromSource(source, encode);
		// Convert each coordinates in data and write to output string
		// Define the number format that we want to use which is US
		NumberFormat _format = NumberFormat.getInstance(Locale.US);
		// Loop through all the list
		for (Iterator<String> i = data.iterator(); i.hasNext(); result += newLine) {
			String str = i.next();
			// Get the coordinates
			String[] coords = str.split(" ");
			// Check if there is line which does not conform to our format
			// Continue if not satisfy
			if (coords.length > 2 || coords.length <= 0) {
				continue;
			}
			try {
				// Convert to double
				Number nlong = _format.parse(coords[0]);
				Number nlat = _format.parse(coords[1]);
				// And do the converting
				Point<Double> lam = WGS84Converter.toLambert93(
						Double.parseDouble(nlong.toString()),
						Double.parseDouble(nlat.toString()));
				// Append to result string
				result += lam.x + " " + lam.y;
			} catch (ParseException e) {

			}
		}
		
		return result;
	}
	
	@Path("/WGS84UTMWGS84")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String convertWGS84UTMToWGS84 (@QueryParam("x") double x, @QueryParam("y") double y, @QueryParam("zone") double zone, @QueryParam("hem") String hem) {
		Point<Double> result = UTMConverter.toWGS84(x, y, zone, hem);
		if (result == null) {
			return "Some parameters are wrong!";
		} else {
			return Math.toDegrees(result.x) + " " + Math.toDegrees(result.y);
		}
		//return x + " " + y + " " + zone + " " + hem;
	}
	
	@Path("/WGS84UTMWGS84/file")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String convertWGS84UTMToWGS84 (@QueryParam("source") String source, @QueryParam("encode") String encode) {
		String result = "";
		
		List<String> data = getDataFromSource(source, encode);
		// Convert each coordinates in data and write to output string
		// Define the number format that we want to use which is US
		NumberFormat _format = NumberFormat.getInstance(Locale.US);
		// Loop through all the list
		for(Iterator<String> i = data.iterator() ; i.hasNext() ; result += newLine) {
			String str = i.next();
			// Get the coordinates
			String[] coords = str.split(" ");
			// Check if there is line which does not conform to our format
			// Continue if not satisfy
			if (coords.length > 4 || coords.length <= 0) {
				continue;
			}
			try {
				// Convert to double
				Number nx = _format.parse(coords[0]);
				Number ny = _format.parse(coords[1]);
				Number nz = _format.parse(coords[2]);
				String hem = coords[3];
				// And do the converting
				Point<Double> wgs = UTMConverter.toWGS84(
						Double.parseDouble(nx.toString()), 
						Double.parseDouble(ny.toString()),
						Double.parseDouble(nz.toString()),
						hem);
				// Append to result string
				result += Math.toDegrees(wgs.x) + " " + Math.toDegrees(wgs.y);
			} catch (ParseException e) {
				
			}
		}
		
		return result;
	}
}
