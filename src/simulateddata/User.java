package simulateddata;

import java.util.Random;

public class User {
	private double lat;
	private double lng;
	private String town;
	private int[] days; //0=Susceptible, 1=Sick, 2=Recovered, -1=No Input
	
	public User (double lat, double lng, String town, int[] days, int currDay) {
		this.lat = lat;
		this.lng = lng;
		this.town = town;
		this.days = days;
		nextCondition(currDay);
	}
	
	public int getCondition (int day) {
		return days[day];
	}
	
	public void nextCondition (int day) {
		Random r = new Random();
		if (day > 2 && days[day - 1] == 1 && days[day - 2] == 1 && days[day - 3] == 1) {
			days[day] = 2;
		}
		else if (day > 0 && days[day - 1] == 1) {
			days[day] = 1;
		}
		else if (day > 0 && days[day - 1] == 2) {
			days[day] = 2;
		}
		else if (r.nextDouble() < 0.15) {
			days[day] = 1;
		}
		else {
			days[day] = 0;
		}
	}
	
	public static String printArray (int[] array) {
		if (array.length == 0) {
			return "[]";
		}
		String output = "[" + array[0];
		for (int i = 1; i < array.length; i++) {
			output += ", " + array[i];
		}
		output += "]";
		return output;
	}
	
	public void insertNoInput () {
		Random r = new Random();
		for (int day = 0; day < days.length; day++) {
			if (r.nextDouble() > 0.8) {
				days[day] = -1;
			}
		}
	}
	
	public void fixInput () {
		if (days[0] == -1) {
			days[0] = 0;
		}
		for (int day = 1; day < days.length; day++) {
			if (days[day] == -1) {
				if (day > 2 && days[day - 1] == 1 && days[day - 2] == 1 && days[day - 3] == 1) {
					days[day] = 2;
				}
				else {
					days[day] = days[day - 1];
				}
			}
		}
	}
	
	public String toString () {
		return printArray(days) + " ||||||||||||||||";
	}
}