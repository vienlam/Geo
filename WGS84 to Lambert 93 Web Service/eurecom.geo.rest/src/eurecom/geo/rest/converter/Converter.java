package eurecom.geo.rest.converter;

import java.io.File;
import java.io.IOException;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

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
	private File writeToFile(String str) {
		File file = null;
		try {
			file = new File("result.txt");
			file.mkdirs();
			file.createNewFile();
			FileUtils.writeStringToFile(file, str);
			return file;
		} catch (IOException e) {
			
		}
		
		return null;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnTitle() {
		return "This is a converter :)";
	}
	
	@Path("/WGS84RGF93Lambert93")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String convertWGS84ToLambert93 (@QueryParam("lon") double lon, @QueryParam("lat") double lat, @QueryParam("mapview") int map) {
		Point<Double> result = WGS84Converter.toLambert93(lon, lat);
		if (map == 1) {
			return result.x + " " + result.y + " " + lon + " " + lat;
		}
		return result.x + " " + result.y;
	}
	
	@Path("/WGS84RGF93Lambert93/file")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public Response convertWGS84ToLambert93 (@QueryParam("source") String source, @QueryParam("encode") String encode) {
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
		
		File file = writeToFile(result);
		
		if (file != null) {
			ResponseBuilder responseBuilder = Response.ok((Object) file);
			responseBuilder.header("Content-Disposition",
					"attachment; filename=\"file_from_server.log\"");
			Response response = responseBuilder.build();
			//file.delete();
			return response;
		} else {
			return Response.status(200).entity(result).build();
		}
		
		//return result;
	}
	
	@Path("/RGF93Lambert93WGS84")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String convertLambert93ToWGS84 (@QueryParam("x") double x, @QueryParam("y") double y, @QueryParam("mapview") int map) {
		Point<Double> result = LambertConverter.fromLambert93ToWGS84(x, y);
		
		String res = Math.toDegrees(result.x) + " " + Math.toDegrees(result.y);
		if (map == 1) {
			res += " " + res;
		}
		return res;
	}
	
	@Path("/RGF93Lambert93WGS84/file")
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
	public String convertWGS84ToWGS84UTM (@QueryParam("lon") double lon, @QueryParam("lat") double lat, @QueryParam("mapview") int map) {
		UTMCoord result = UTMConverter.convert(Math.toRadians(lon), Math.toRadians(lat), WGS84Converter.a, WGS84Converter.b);
		//UTMCoord result = WGS84Converter.toUTM(lon, lat);
		String res = result.x + " " + result.y + " " + result.zone + " " + result.hem;
		if (map == 1) {
			res += " " + lon + " " + lat;
		}
		return res;
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
	public String convertWGS84UTMToWGS84 (@QueryParam("x") double x, @QueryParam("y") double y, @QueryParam("zone") double zone, @QueryParam("hem") String hem, @QueryParam("mapview") int map) {
		Point<Double> result = UTMConverter.inverse(x, y, zone, hem, WGS84Converter.a, WGS84Converter.b);
		
		if (result == null) {
			return "Some parameters are wrong!";
		} else {
			String res = Math.toDegrees(result.x) + " " + Math.toDegrees(result.y);
			if (map == 1) {
				res += " " + res;
			}
			return res;
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
//				Point<Double> wgs = UTMConverter.toWGS84(
//						Double.parseDouble(nx.toString()), 
//						Double.parseDouble(ny.toString()),
//						Double.parseDouble(nz.toString()),
//						hem);
				// Append to result string
//				result += Math.toDegrees(wgs.x) + " " + Math.toDegrees(wgs.y);
			} catch (ParseException e) {
				
			}
		}
		
		return result;
	}
	
	@Path("/RGF93Lambert93WGS84UTM")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String convertRGF93Lamber93ToWGS84UTM(@QueryParam("x") double x, @QueryParam("y") double y, @QueryParam("mapview") int map) {
		Point<Double> wgs = LambertConverter.fromLambert93ToWGS84(x, y);
		UTMCoord result = UTMConverter.convert(wgs.x, wgs.y, WGS84Converter.a, WGS84Converter.b);
		String res = result.x + " " + result.y + " " + result.zone + " " + result.hem;
		if (map == 1) {
			res += " " + wgs.x + " " + wgs.y;
		}
		return res;
	}
	
	@Path("/WGS84UTMRGF93Lambert93")
	@GET
	@Produces({MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
	public String convertWGS84UTMToRGF93Lamber93(@QueryParam("x") double x, @QueryParam("y") double y, @QueryParam("zone") double zone, @QueryParam("hem") String hem, @QueryParam("mapview") int map) {
		Point<Double> wgs = UTMConverter.inverse(x, y, zone, hem, WGS84Converter.a, WGS84Converter.b);
		Point<Double> result = WGS84Converter.toLambert93(Math.toDegrees(wgs.x), Math.toDegrees(wgs.y));
		String res = result.x + " " + result.y;
		if (map == 1) {
			res += " " + wgs.x + " " + wgs.y;
		}
		return res;
	}
}
