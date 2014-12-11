package org.bouldercounty.parks.trails.data;


import java.util.List;
import java.util.Map;


public class TrailUsesObject {

	String trailName;
	boolean horse;
	boolean bike;
	boolean dog;
	boolean hike;
	String difficulty;
	String miles;
	String property;
//	String notes;

	static Map<String, TrailUsesObject> namedTrail;
	static Map<String, List<TrailUsesObject>> propertyTrails;


	public TrailUsesObject(String trailName, boolean horse, boolean bike, boolean dog, 
						boolean hike, String difficulty, String miles, String property) 
	{
		this.trailName = trailName;
		this.horse = horse;
		this.bike = bike;
		this.dog = dog;
		this.hike = hike;
		this.difficulty = difficulty;
		this.miles = miles;
		this.property = property;
	}

	public TrailUsesObject() { }

	public String getTrailName() {
		return trailName;
	}

	public void setTrailName(String trailName) {
		this.trailName = trailName;
	}

	public boolean isHorse() {
		return horse;
	}

	public void setHorse(boolean horse) {
		this.horse = horse;
	}

	public boolean isBike() {
		return bike;
	}

	public void setBike(boolean bike) {
		this.bike = bike;
	}

	public boolean isDog() {
		return dog;
	}

	public void setDog(boolean dog) {
		this.dog = dog;
	}

	public boolean isHike() {
		return hike;
	}

	public void setHike(boolean hike) {
		this.hike = hike;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	public String getMiles() {
		return miles;
	}

	public void setMiles(String miles) {
		this.miles = miles;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public static Map<String, TrailUsesObject> getNamedTrail() {
		return namedTrail;
	}

	public static void setNamedTrail(Map<String, TrailUsesObject> namedTrail) {
		TrailUsesObject.namedTrail = namedTrail;
	}

	public static Map<String, List<TrailUsesObject>> getPropertyTrails() {
		return propertyTrails;
	}

	public static void setPropertyTrails(Map<String, List<TrailUsesObject>> propertyTrails) {
		TrailUsesObject.propertyTrails = propertyTrails;
	}

}