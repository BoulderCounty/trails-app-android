package org.bouldercounty.parks.trails.data;


import java.util.ArrayList;
import java.util.List;

import android.util.Log;


public class Placemark implements Comparable<Placemark> {

	public static String tag = "Placemark";

	public String id;
	String name;
	String description;
	public List<List<String>> coordinates;
	public boolean isParking;

	public static List<TrailUsesObject> trailUses;
	public boolean hike;
	public boolean horse;
	public boolean bike;
	public boolean dog;
	public String trailDistance;
	public String trailName;

	public String parkingAddress = "";

	public String extraDescription = "";

	public Folder parentFolder;
	public boolean isATrail;

	public String trailDifficulty = "";

	public Placemark() { }

	public Placemark(String id, String name, String description) { 
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
			parseDescription(description);
	}
	private void parseDescription(String description) {
		String[] sArray = description.split(",");
		if (sArray.length == 2) { // hmmm?
			this.trailDistance = sArray[0];
			this.trailName = sArray[1];
		}
	}

	public List<List<String>> getCoordinates() {
		return coordinates;
	}

	public void addCoordinates(List<String> coords) {
		if (null == this.coordinates) { coordinates = new ArrayList<List<String>>(); }
		this.coordinates.add(coords);
	}

	@Override
	public String toString() {
		String printName = (null == this.name) ? "" : "name="+this.name+":";
		String printId = (null == this.id) ? "" : "id="+this.id+":";
		return printName + printId + ": isParking=" + isParking + ":" +
				"description=" + this.description +
				"coordinates=" + printCoordinates() +
				"parking address =[" + parkingAddress + "]";
	}

	public static String printPlacemarks(List<Placemark> placemarks) {
		StringBuilder sb = new StringBuilder("[");
		for (Placemark p : placemarks) {
			sb.append("[");
			sb.append( p.toString() );
			sb.append("] ");
		}
		return sb.toString();
	}

	private String printCoordinates() {
		StringBuilder sb = new StringBuilder("[");
		for (List<String> coords : coordinates) {
			sb.append("[");
			for (String s : coords) {
				sb.append("[");
				sb.append( s );
				sb.append("] ");
			}
			sb.append("]");
		}
		return sb.toString();
	}

	public static List<TrailUsesObject> getTrailUses() {
		return trailUses;
	}

	@Override
	public int compareTo(Placemark another) {
		StringBuilder methodName = new StringBuilder(".compareTo");
		try {
			if (null != this.trailName && null != another.trailName) {
				if (this.trailName.compareToIgnoreCase( another.trailName ) > 0) {
					return 1;
				} else if (this.trailName.compareToIgnoreCase( another.trailName ) < 0) {
					return -1;
				} else {
					return 0;
				}
			}
			return 0;
		} catch (Exception e) {
			Log.e(tag+methodName.toString(), "Exception : " + e.toString());
			return 0;
		}
	}

}