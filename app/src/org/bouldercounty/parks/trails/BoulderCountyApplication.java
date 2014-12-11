package org.bouldercounty.parks.trails;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.bouldercounty.parks.trails.data.Document;
import org.bouldercounty.parks.trails.data.Folder;
import org.bouldercounty.parks.trails.data.Placemark;
import org.bouldercounty.parks.trails.utilities.XMLSaxParser;

import org.bouldercounty.parks.trails.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

import android.app.Application;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;


public class BoulderCountyApplication extends Application {


	public static String tag = "BoulderCountyApplication";

	private Location currentLocation;
	private boolean updatesOn = true;

	List<List<GeoPoint>> allGeoPoints = new ArrayList<List<GeoPoint>>();

	public Document kmlDocument;
	public Folder currentFolder;
	public Folder rootFolder;
	List<Folder> trailList;
	List<Placemark> trailPlacemarkList = new ArrayList<Placemark>();
	public String selectedTrail;

	public static boolean isMapAll;
	public static boolean isSatellite;

    public void onCreate() {
		super.onCreate();
		new ParseTrailsTask().execute("");
    }

	public void setCurrentFolder(Folder f) {
		this.currentFolder = f;
	}

	public Folder getCurrentFolder() {
		return currentFolder;
	}

	public List<Folder> getTrailList() {
		return trailList;
	}

	public void setTrailList(List<Folder> trailList) {
		this.trailList = trailList;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	public void setUpdatesOn(boolean updatesOn) {
		this.updatesOn = updatesOn;
	}

	public boolean isUpdatesOn() {
		return updatesOn;
	}

    class ParseTrailsTask extends AsyncTask<String, List<Folder>, List<Folder>> {

		@Override
		protected List<Folder> doInBackground(String... params) {
			Document kmlDocument = new Document();
			try {
				InputStream in = 
			    	getApplicationContext().getResources().openRawResource(R.raw.new_7_24_2012);

		      if (in!=null) {
		        InputStreamReader tmp=new InputStreamReader(in);
		        BufferedReader reader=new BufferedReader(tmp);
		        String str;

		        while ((str = reader.readLine()) != null) {
//		        	str = cleanData(str);
		        	kmlDocument	= XMLSaxParser.getParsedData(str);
		        	Log.e(tag, "");
		        }
		        // check StringBuffer
		        in.close();
	        	Log.e(tag, "");
		      }
			}
			catch (java.io.FileNotFoundException e) {
		    	e.printStackTrace();
		    	Log.e(tag, "FileNotFoundException: " + e.getMessage());
	    	} catch (IOException ioe) {
	    		Log.e(tag, "IOException : " + ioe.getMessage());
	    	} catch (Exception e) {
	    		Log.e(tag, "Exception : " + e.toString());
	    	} finally {
	    		
	    	}
	    	return new ArrayList<Folder>();
		}

		@Override
		protected void onPostExecute(List<Folder> result) {
			super.onPostExecute(result);

			Log.e(tag, "getting overlays for map-all view...");
			new GetAllOverlays().execute();

		}

		@Override
		protected void onProgressUpdate(List<Folder>... values) {
			super.onProgressUpdate(values);
		}

    }

    class GetAllOverlays extends AsyncTask<Void, List<Overlay>, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {	


			for (Folder f : Folder.getTrailList()) {
				if (null != f.parentFolder && !f.parentFolder.alreadyMapped 
//						&& f.parentFolder.name.contains("Betasso")
						) {

					List<GeoPoint> gpList = null;
					Log.e(tag, "");
					for (Folder folder : f.parentFolder.getFolders()) {
						
						GeoPoint startGP = null;
						GeoPoint gp1 = null;
						GeoPoint gp2 = null;
						for (Placemark pm : folder.getPlacemarks()) 
						{

								for (List<String> coordinates : pm.getCoordinates()) {
									gpList = new ArrayList<GeoPoint>();
									for (String path : coordinates) {
										
										try {
											if ( ( !" ".equalsIgnoreCase( path.substring(0, 1) ) && !"-".equalsIgnoreCase( path.substring(0, 1) ) ) || 
													(  !" ".equalsIgnoreCase( path.substring(path.length()-1) ) && !",0".equalsIgnoreCase( path.substring(path.length()-2) ) ) ) 
											{
												path = cleanPoints(path);
											}
										if (path != null && path.trim().length() > 0) {
											// get path overlays
											String[] pairs = path.split(" ");
											String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude
																					// lngLat[1]=latitude
																					// lngLat[2]=height

											if (lngLat.length < 3) {
												lngLat = pairs[1].split(",");
											}	
											startGP = new GeoPoint((int) (Double
													.parseDouble(lngLat[1]) * 1E6), (int) (Double
													.parseDouble(lngLat[0]) * 1E6));
											gpList.add(startGP);

											gp2 = startGP;
											for (int i = 1; i < pairs.length; i++) 
											{
												lngLat = pairs[i].split(",");
												gp1 = gp2;
												gp2 = new GeoPoint(
														(int) (Double.parseDouble(lngLat[1]) * 1E6),
														(int) (Double.parseDouble(lngLat[0]) * 1E6));
												if (gp1 != null && gp2 != null) {
													gpList.add(gp1);
													gpList.add(gp2);
												} else {
													Log.e(tag, "!");
												}

											}
										} // end if path != null

										} catch (Exception e) {
											Log.e(tag, "Exception: " + e.toString());
											continue;
										}
									}
									allGeoPoints.add(gpList); // 
								}
						}
						
					}
				}
				f.parentFolder.alreadyMapped = true;
			}

			return null;
		}


		@Override
		protected void onProgressUpdate(List<Overlay>... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			Log.e(tag, "trailList size=[" + Folder.getTrailList().size());
    		BoulderCountyApplication.this.setTrailList(Folder.getTrailList());

//			Log.e(tag, "****GeoPoints list size=[" + allGeoPoints.size());
		}

    }

	private String cleanData(String str) {
		try {
			str = str.replace("&apos;", ""); // note: "'", "\'", "\\'" and "/'" did not work for substitution/escaping
			str = str.replace("\'", "");
			str = str.replace("&amp;", "&");
		} catch (Exception e) {
			Log.e(tag, "Exception : " + e.getMessage());
		}
		return str;
	}

	public String cleanPoints(String path) {
		if ( !" ".equalsIgnoreCase( path.substring(0, 1) ) && !"-".equalsIgnoreCase( path.substring(0, 1) )) 
		{
			int startIndex = path.indexOf(" ");
			path = path.substring(startIndex);
//			Log.e(tag, "path = [" + path);
		}
		if ( !" ".equalsIgnoreCase( path.substring(path.length()-1) ) && !",0".equalsIgnoreCase( path.substring(path.length()-2) ) ) 
		{
			int end = path.length()-1;
			while (end > 0) {
				end--;
				if (!" ".equalsIgnoreCase( path.substring(end, end+1) )) {
					continue;
				} else {
					path = path.substring(0, end+1);
//					Log.e(tag, "path = [" + path);
					break;
				}
			}
		}
		return path;
	}

}