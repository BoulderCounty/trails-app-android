package org.bouldercounty.parks.trails.data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.android.maps.GeoPoint;

import android.util.Log;



public class Folder implements Comparable<Folder>  {

	public static String tag = "Folder";

	public String name;
	List<Folder> folders = new ArrayList<Folder>();
	List<Placemark> placemarks = new ArrayList<Placemark>();

	Map<List<String>, Integer> trailsMap = new HashMap();

	static List<Folder> trailList = new ArrayList<Folder>();
	public static boolean allProcessed;
	public boolean processed;
	public Folder parentFolder;
	boolean isTrailSystem;
	boolean isATrail;
	
	public static Map<String, Folder> pairFolders = new HashMap<String, Folder>();
	public boolean pairFolder;

	public boolean alreadyMapped;	
	
	public boolean isSelected;

	public String trailSystemName;

	public List<Placemark> parkingList = new ArrayList<Placemark>();
	public List<Placemark> trailheadList = new ArrayList<Placemark>();

	public double distanceFromCurrentLocation;
//	public static int sortType = 0; // 0 = sort by String name; 1 = sort by lat/lon/distance from center
	public static int sortType = SortType._default; // 0 = sort by String name; 1 = sort by lat/lon/distance from center

	public Folder() { }

	public Folder(String name, List<Folder> folders, List<Placemark> placemarks) {
		this.name = name;
		this.folders = folders;
		this.placemarks = placemarks;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Folder> getFolders() {
		return folders;
	}

	public void addFolder(Folder folder) {
		Log.e(tag, "");
				isTrailSystem = true;
				addToTrailList(folder);
			this.folders.add(folder);
			
			// pair up Mud Lake & Caribou Trail
			if (folder.parentFolder.name.equalsIgnoreCase("Mud Lake")) 
			{
				folder.parentFolder.pairFolder = true;
				if (!pairFolders.containsKey("Caribou Ranch")) {
//					Log.e(tag, "adding [" + folder.trailSystemName);
					pairFolders.put("Caribou Ranch", folder.parentFolder);
//					pairFolders.put(folder.parentFolder.name, folder.parentFolder);
				}
			} else if (folder.parentFolder.name.equalsIgnoreCase("Caribou Ranch")) {
				folder.parentFolder.pairFolder = true;
//				Log.e(tag, "adding [" + folder.trailSystemName);
				if (!pairFolders.containsKey("Mud Lake")) 
//				if (!pairFolders.containsKey("Caribou Ranch")) 
				{
//					pairFolders.put(folder.parentFolder.name, folder.parentFolder);
//					Log.e(tag, "adding [" + folder.trailSystemName);
					pairFolders.put("Mud lake", folder.parentFolder);
				}
			} else if (folder.parentFolder.name.equalsIgnoreCase("LoboTrail")) {
				folder.parentFolder.pairFolder = true;
//				Log.e(tag, "adding [" + folder.trailSystemName);
				if (!pairFolders.containsKey("NiwotTrails")) {
					pairFolders.put("NiwotTrails", folder.parentFolder);
				}
			} else if (folder.parentFolder.name.equalsIgnoreCase("NiwotTrails")) {
				folder.parentFolder.pairFolder = true;
//				Log.e(tag, "adding [" + folder.trailSystemName);
				if (!pairFolders.containsKey("LoboTrail")) {
					pairFolders.put("LoboTrail", folder.parentFolder);
				}
			}
	}

	public void addFolders(List<Folder> folders) {
		for (Folder f: folders) {
			addFolder(f);
		}
	}

	private void addToTrailList(Folder folder) {
		trailList.add(folder);
	}

	public List<Placemark> getFolderPlacemarks() {
		return placemarks;
	}

	public List<Placemark> getPlacemarks() {
		return placemarks;
	}
	public void setPlacemarks(List<Placemark> placemarks) {
		this.placemarks = placemarks;
	}

	public void addPlacemark(Placemark placemark) {
		// check if parking/trailhead & add to appropriate list
		if (placemark.id.contains("-p")) { // parking marker
			// parking can be shared between trails...so could be '-p1', '-p2'
			parkingList.add(placemark);
		} else if (placemark.id.endsWith("-tm")) { // trailhead marker
			trailheadList.add(placemark);
			// set miles, trail uses...
//		} else if (placemark.id.endsWith("-t")) { // single folder/single trail[head]
		} else {
			// hmmm, this folder may be added multiple times
//			addToTrailList(this); // is it set as a trail list?!?
			this.placemarks.add(placemark);
		}
	}

	public void addPlacemarks(List<Placemark> placemarks) {
		for (Placemark p: placemarks) {
			addPlacemark(p);
		}
	}

	@Override
	public String toString() {
		// add trailName
		return "name=" + this.name + " : " + 
//				"isATrail=" + isATrail + " : " + 
				"isTrailSystem=" + isTrailSystem + " : " +
				"placemarks=" + Placemark.printPlacemarks(this.placemarks) + " : " +
				"folders=" + printFolders(this.folders);
	}

	public static String printFolders(List<Folder> folders) {
		StringBuilder sb = new StringBuilder("[");
		for (Folder f : folders) {
			sb.append("[");
			sb.append( f.toString() );
			sb.append("] ");
		}
		return sb.toString();
	}

	public static void resetTrailList() {
		trailList = new ArrayList<Folder>();
	}

	public static boolean hasTrails() {
		if (trailList.size() > 0) { return true; }
		return false;
	}

	public static List<Folder> getTrailList() {
		return trailList;
	}

	/*
	 * Parameters	Folder Object to compare
	 */
	@Override
	public int compareTo(Folder another) {
		switch (sortType) {
		case 0:
			return sortByTrailSystemName(another);
//			break;
		case 1:
			return sortByTrailName(another);
//			break;
		case 2:
			return sortByTrailSystemName(another);
//			break;
		case 3:
			return 0;
//			break;
		case 4:
			return sortByDifficulty(another);
//			break;
		case 5:
			return sortByDogUse(another);
//			break;
		case 6:
			return sortByBikeUse(another);
//			break;
		case 7:
			return sortByHikeUse(another);
//			break;
		case 8:
			return sortByHorseUse(another);
//			break;
		}
		return 0;
	}

	public static void setTrailName(Folder f, String s) {
		f.trailSystemName = s;
	}

	public static void setTrailName(List<Folder> folders, String s) {
		for (Folder f : folders) {
			setTrailName(f, s);
		}
	}

	private static double[] getMarkerCoordinates(List<List<String>> coordList) {
		try {
			for (List<String> coordinates : coordList) {
				String[] s = coordinates.get(0).split(",");
				return new double[] { Double.parseDouble(s[0]), Double.parseDouble(s[1]) };
			}
		} catch (Exception e) {
			Log.e(tag+".getMarkerCoordinates", "Exception: " + e.toString());
		} finally {
			
		}
		return new double[] {0, 0};
	}

	public static boolean isInBounds(Folder parentFolder, int mapLatSpan, int mapLonSpan, GeoPoint mapCenter) {
		if (!parentFolder.alreadyMapped) {
			int[] folderLatLon = null;
			int[] latMinMax = null;
			int[] lonMinMax = null;

			try {
				folderLatLon = getLatLonToCompare( getMarkerCoordinates(parentFolder.parkingList.get(0).getCoordinates()) );
				latMinMax = getMapLatMinMax(mapLatSpan, mapCenter);
				lonMinMax = getMapLonMinMax(mapLonSpan, mapCenter);

				if ( (folderLatLon[0] < latMinMax[0] && folderLatLon[0] > latMinMax[1]) &&
					(folderLatLon[1] < lonMinMax[0] && folderLatLon[1] > lonMinMax[1]) ) 
				{
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				Log.e(tag+".isInBounds", "Exception : " + e.toString());
				Log.e(tag+".isInBounds", "folderLatLon size=[" + folderLatLon.length + "] / latMinMax=[" + latMinMax.length + "] / lonMinMax=[" + lonMinMax.length + "]");
			}
		} 
			return false;
	}

	public static int[] getMapLatMinMax(int[] mapSpan, GeoPoint mapCenter) {
		// mapSpan[0] is max Latitude; mapSpan[1] is min Latitude
		return new int[] { mapCenter.getLatitudeE6() + (mapSpan[0]/2), mapCenter.getLatitudeE6() - (mapSpan[0]/2) };
	}

	public static int[] getMapLatMinMax(int mapLatSpan, GeoPoint mapCenter) {
		return new int[] { mapCenter.getLatitudeE6() + (mapLatSpan/2), mapCenter.getLatitudeE6() - (mapLatSpan/2) };
	}

	public static int[] getMapLonMinMax(int[] mapSpan, GeoPoint mapCenter) {
		// mapSpan[0] is max Longitude; mapSpan[1] is min Longitude
		return new int[] { mapCenter.getLongitudeE6() + (mapSpan[1]/2), mapCenter.getLongitudeE6() - (mapSpan[1]/2) };
	}

	public static int[] getMapLonMinMax(int mapLonSpan, GeoPoint mapCenter) {
		return new int[] { mapCenter.getLongitudeE6() + (mapLonSpan/2), mapCenter.getLongitudeE6() - (mapLonSpan/2) };
	}

	public static int[] getLatLonToCompare(double[] coords) {
		return new int[] { (int) (coords[1] * 1E6),(int) (coords[0] * 1E6) };
		// int[0] is Latitude; int[1] is Longitude
	}

	private int sortByDogUse(Folder another) {
		/*
		 * Returns  0 if both trails allow dogs 
		 * 		   -1 if this Folder allows dogs, but another does not allow dogs
		 *			1 if this Folder does not allow dogs, but another does not
		 */
		StringBuilder methodName = new StringBuilder(".sortByDogUse");
		try {
			if (this.trailheadList.get(0).dog && !another.trailheadList.get(0).dog) {
				return -1;
			} else if (!this.trailheadList.get(0).dog && another.trailheadList.get(0).dog) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			Log.e(tag+methodName.toString(), "Exception : " + e.toString());
			return 0;
		}		
	}

	private int sortByBikeUse(Folder another) {
		/*
		 * Returns  0 if both trails allow dogs 
		 * 		   -1 if this Folder allows dogs, but another does not allow dogs
		 *			1 if this Folder does not allow dogs, but another does not
		 */
		StringBuilder methodName = new StringBuilder(".sortByBikeUse");
		try {
			if (this.trailheadList.get(0).bike && !another.trailheadList.get(0).bike) {
				return -1;
			} else if (!this.trailheadList.get(0).bike && another.trailheadList.get(0).bike) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			Log.e(tag+methodName.toString(), "Exception : " + e.toString());
			return 0;
		}		
	}

	private int sortByHikeUse(Folder another) {
		/*
		 * Returns  0 if both trails allow dogs 
		 * 		   -1 if this Folder allows dogs, but another does not allow dogs
		 *			1 if this Folder does not allow dogs, but another does not
		 */
		StringBuilder methodName = new StringBuilder(".sortByHikeUse");
		try {
			if (this.trailheadList.get(0).hike && !another.trailheadList.get(0).hike) {
				return -1;
			} else if (!this.trailheadList.get(0).hike && another.trailheadList.get(0).hike) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			Log.e(tag+methodName.toString(), "Exception : " + e.toString());
			return 0;
		}		
	}

	private int sortByHorseUse(Folder another) {
		/*
		 * Returns  0 if both trails allow dogs 
		 * 		   -1 if this Folder allows dogs, but another does not allow dogs
		 *			1 if this Folder does not allow dogs, but another does not
		 */
		StringBuilder methodName = new StringBuilder(".sortByHorseUse");
		try {
			if (this.trailheadList.get(0).horse && !another.trailheadList.get(0).horse) {
				return -1;
			} else if (!this.trailheadList.get(0).horse && another.trailheadList.get(0).horse) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			Log.e(tag+methodName.toString(), "Exception : " + e.toString());
			return 0;
		}		
	}

	private int sortByTrailName(Folder another) {
			/*
			 * Returns  0 if both Folder trail system names are equal 
			 * 		   -1 if this Folder's ...
			 *			1 if this Folder's ...
			 */
			StringBuilder methodName = new StringBuilder(".compareTo");
			try {
				// sort by parentFolder, then ...
				if (this.getName().compareToIgnoreCase( another.getName() ) > 0) {
					return 1;
				} else if (this.getName().compareToIgnoreCase( another.getName() ) < 0) {
					return -1;
				} else {
					return 0;
				}
			} catch (Exception e) {
//				Log.e(tag, "Exception : " + e.getMessage());
				Log.e(tag+methodName.toString(), "Exception : " + e.toString());
				return 0;
			}		
	}

	private int sortByTrailSystemName(Folder another) {
		/*
		 * Returns  0 if both Folder trail system names are equal 
		 * 		   -1 if this Folder's ...
		 *			1 if this Folder's ...
		 */
		StringBuilder methodName = new StringBuilder(".compareTo");
		try {
			// sort by parentFolder, then ...
			if (this.trailSystemName.compareToIgnoreCase( another.trailSystemName ) > 0) {
				return 1;
			} else if (this.trailSystemName.compareToIgnoreCase( another.trailSystemName ) < 0) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			Log.e(tag+methodName.toString(), "Exception : " + e.toString());
			return 0;
		}		
	}


	private int sortByDifficulty(Folder another) {
			/*
			 * Returns  0 if both Folder trail system names are equal 
			 * 		   -1 if this Folder's trails system occurs before another
			 *			1 if this Folder's trails system occurs after another
			 */
			StringBuilder methodName = new StringBuilder(".sortByDifficulty");
			try {
				if (this.trailheadList.get(0).equals("Easy") && 
						!another.trailheadList.get(0).trailDifficulty.equals("Easy")
						) {
					return 1;
				} else if (another.trailheadList.get(0).equals("Easy") && 
						!this.trailheadList.get(0).trailDifficulty.equals("Easy")) 
				{
					return -1;
				} else {
					return 0;
				}
			} catch (Exception e) {
				Log.e(tag+methodName.toString(), "Exception : " + e.toString());
				return 0;
			}		
	}
}