package simulateddata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class Main {
	public static void main (String[] args) throws MalformedURLException, IOException {
		//System.out.println(checkTown(40.714224,-72.9614523));
		int numUsers = 3;
		int numDays = 100;
		String town = "Lexington";
		String state = "MA";
		int day = 0;
		
		ArrayList<User> users = createUsers(numUsers, town, state, numDays, day);
		System.out.println(users);
		
		Random r = new Random();
		for (day = 0; day < numDays; day++) {
			if (r.nextDouble() < 0.3) {
				ArrayList<User> newUser = createUsers(1, town, state, numDays, day);
				if (newUser != null) {
					users.addAll(newUser);
				}
				//System.out.println(day);
			}
			for (User user : users) {
				user.nextCondition(day);
			}
		}
		
		System.out.println(users);
		
		for (User user : users) {
			user.insertNoInput();
		}
		
		System.out.println(users);
		
		for (User user : users) {
			user.fixInput();
		}
		
		System.out.println(users);
	}
	
	private static ArrayList<User> createUsers (int numUsers, String town, String state, int numDays, int currDay) throws IOException {
		ArrayList<User> users = new ArrayList<User>();
		ArrayList<Double> bounds = getBounds(town, state);
		//System.out.println(bounds);
		if (bounds == null) {
			return null;
		}
		for (int user = 0; user < numUsers; user++) {
			double lat = random(bounds.get(0), bounds.get(1));
			double lng = random(bounds.get(2), bounds.get(3));
			String t = checkTown(lat, lng);
			if (t == null) {
				user--;
				continue;
			}
			int[] days = new int[numDays];
			for (int i = 0; i < numDays; i++) {
				days[i] = -1;
			}
			users.add(new User(lat, lng, t, days, currDay));
		}
		return users;
	}
	
	private static ArrayList<Double> getBounds (String town, String state) throws IOException {
		try {
			String URL = "https://maps.googleapis.com/maps/api/geocode/json?address=" + town + "+" + state;
			InputStream is = new URL(URL).openStream();
			
			ArrayList<String> lines = new ArrayList<String>();
			ArrayList<Double> bounds = new ArrayList<Double>();
	
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = reader.readLine();
			int i = 0;
			int l = 0;
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
				if (line == null && l == 0) {
					return null;
				}
				else if (line == null) {
					break;
				}
				i++;
				if (line.contains("\"southwest\" : {")) {
					l = i;
				}
			}
			
			bounds.add(Double.parseDouble(lines.get(l-3).substring(25, lines.get(l-3).length() - 1)));
			bounds.add(Double.parseDouble(lines.get(l+1).substring(25, lines.get(l+1).length() - 1)));
			bounds.add(Double.parseDouble(lines.get(l-2).substring(25, lines.get(l-2).length())));
			bounds.add(Double.parseDouble(lines.get(l+2).substring(25, lines.get(l+2).length())));
			
			return bounds;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	private static String checkTown (double lat, double lng) throws MalformedURLException, IOException {
		try {
			String URL = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true";
			InputStream is = new URL(URL).openStream();
			
			ArrayList<String> lines = new ArrayList<String>();
	
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = reader.readLine();
			int i = 0;
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
				if (line == null) {
					return null;
				}
				i++;
				if (line.contains("\"types\" : [ \"locality\", \"political\" ]")) {
					line = lines.get(i - 1);
					return (line.substring(31, line.length() - 2));
				}
			}
			
			return null;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	private static double random (double start, double end) {
		Random r = new Random();
		double randomValue = start + (end - start) * r.nextDouble();
		return randomValue;
	}
}