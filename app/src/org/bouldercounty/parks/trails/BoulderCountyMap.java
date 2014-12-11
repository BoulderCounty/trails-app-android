package org.bouldercounty.parks.trails;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.bouldercounty.parks.trails.data.Folder;
import org.bouldercounty.parks.trails.data.Placemark;
import org.bouldercounty.parks.trails.map.MyOwnLocationOverlay;
import org.bouldercounty.parks.trails.map.RoutePathOverlay;
import org.bouldercounty.parks.trails.utilities.Constants;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout;

import org.bouldercounty.parks.trails.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


public class BoulderCountyMap extends MapActivity
{

	public static String tag = "BoulderCountyMap";

	private boolean viewSet;

    private RoutePathOverlay routePathOverlay;

	private BoulderCountyApplication app;

    LocationManager manager;
    Location currentLocation;

	protected MapController mc;
	private boolean displayLocation;

	// use when user taps on balloon... do we allow another view?!?
	private boolean mapAll;

    Drawable parkingMarker;
    Drawable trailheadMarker;

	GeoPoint centerPoint = null;

	private MyOwnLocationOverlay mMyLocationOverlay;

	List<String> coordinates;
	Folder currentFolder;
	List<List<String>> trailsToDraw;
	boolean lastOne;
	String[] spm = null;
	List<GeoPoint> path;
	GeoPoint firstPoint;
	GeoPoint lastPoint;

	boolean isDrawing;

	private boolean fromList = true;

	boolean isSatellite;

    //Control circle animation here
	Timer timer;
	public int maxMetersForCircle = 9000; //Size the circle will start at in its biggest size default 2500?!?
    private int framesPerSecond = 60; //frames per second (frames per second)
    private int timeOfCircleShrinkAnimationInMillieseconds = 1000; //speed of animation

    //Do not modify
    private int fireRateForCircleTimer = 1000 / framesPerSecond;

    // map zoom level
    static int currentZoom = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.trail_mapview);


        //Initialize route path overlay
        if (routePathOverlay == null) routePathOverlay = new RoutePathOverlay();


        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        app = (BoulderCountyApplication)getApplication();

		if (!viewSet) { 
			viewSet = true;
			setupViews(); 
		}

    }

	class DrawTrailRoute extends AsyncTask<Void, Overlay, List<Overlay>> 
	{

        private ProgressDialog routeDialog = new ProgressDialog(BoulderCountyMap.this);
        private List<Overlay> markerOverlays;

        protected void onPreExecute() {
        	routeDialog.setMessage("drawing trails...");
        	routeDialog.show();
        }

		@Override
		protected List<Overlay> doInBackground(Void... params) {
				// get the route list///
				trailsToDraw = new ArrayList<List<String>>();

				currentFolder = app.getCurrentFolder();
				BoulderCountyApplication.isMapAll = false;

				int color = 0xFF4500;

				markerOverlays = new ArrayList<Overlay>();

				if (null != currentFolder && null != currentFolder.parentFolder) {

			        int val, pval, tval;

				  if (currentFolder.parentFolder.getFolders().size() > 0) 
				  {
				        routePathOverlay = new RoutePathOverlay();

						double[] markerCoordinates = null;
//						List<GeoPoint> gpList = null;
						int count = 0;
						for (Folder f : currentFolder.parentFolder.getFolders()) {

			    			if (f.isSelected) {
								// draw this one by itself
								color = 0x800080; // purple
							} else {
								color = 0xFF4500; // orange
							}
			    			val = getRoutePaths(f, markerOverlays, color);
							// reset isSelected flag here
							f.isSelected = false;
								pval = addParking(f, markerOverlays);

								tval = addTrailhead(f, markerOverlays);

						}
						Log.e(tag, "count=[" + count);
				  } else {
					  Log.e(tag, "wth - ?!?!?");
				  }
					if (currentFolder.parentFolder.pairFolder) {
						 int paircount = currentFolder.pairFolders.values().size();
						 Log.e(tag, "pair folder count = ************************ [" + paircount + "] ************************");

						List<Folder> pFolderList = new ArrayList<Folder>();
						Folder pFolder = null;

						Set<String> keys = 	Folder.pairFolders.keySet();
						Iterator<String> iter = keys.iterator();

						String key = "";
						while (iter.hasNext()) {
							key = (String)iter.next();
							Log.e(tag, "checking next... against [" + currentFolder.parentFolder.name);
							Log.e(tag, "key=[" + key);
							if ( currentFolder.parentFolder.name.equalsIgnoreCase(key) ) {
								pFolderList.add(Folder.pairFolders.get(key));
								Log.e(tag, "moving on...");
								break;
							}
						}
						for (Folder pf : pFolderList) {
							color = 0xFF4500;
							if (pf.getFolders().size() > 0) { 
								for (Folder apf : pf.getFolders()) {
						    		val = getRoutePaths(apf, markerOverlays, color);
						    		pval = addParking(apf, markerOverlays) ;
						    		tval = addTrailhead(apf, markerOverlays) ;
								}
							}
						}

					}
				}

			return markerOverlays;
		}

		@Override
		protected void onProgressUpdate(Overlay... values) 
		{
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(List<Overlay> result) {
			super.onPostExecute(result);
			try {
				((MapView) findViewById(R.id.trail_map)).getOverlays().add(routePathOverlay);
				((MapView) findViewById(R.id.trail_map)).getOverlays().addAll(result);
				((MapView) findViewById(R.id.trail_map)).postInvalidate();

				((MapView) findViewById(R.id.trail_map)).getController().setZoom(15);

				if (null != centerPoint) {
					((MapView) findViewById(R.id.trail_map)).getController().setCenter(centerPoint);
				}

				((MapView) findViewById(R.id.trail_map)).setBuiltInZoomControls(true);
				//  show notes?!?
				Log.e(tag, "currentFolder.parentFolder.trailSystemName=[" + currentFolder.parentFolder.trailSystemName);
				Log.e(tag, "currentFolder.parentFolder.name=[" + currentFolder.parentFolder.name);
				if ("Betasso Preserve".equalsIgnoreCase( currentFolder.parentFolder.trailSystemName) ) {
					Constants.showAlert(BoulderCountyMap.this, 
							new StringBuilder(getResources().getString(R.string.betasso_alert_title)).toString(),
							new StringBuilder(getResources().getString(R.string.betasso_alert_message)).toString()
							);
				}
				routeDialog.dismiss();
			} catch (Exception e) {
				// popup dialog
				Log.e(tag, "Exception : " + e.toString());
			}
		}
	}

    public class MyTimerTask extends TimerTask {


        int smallestCircleMeters = 0;  //smallest the circle gets
        int largestCircleMeters = maxMetersForCircle;  //how big the circle starts at

        int distanceToShrink = largestCircleMeters - smallestCircleMeters;
        int count = 0;
        float numberOfSeconds = timeOfCircleShrinkAnimationInMillieseconds / 1000;
        int maxCount = (int)( timeOfCircleShrinkAnimationInMillieseconds / fireRateForCircleTimer );
        float distanceToShrinkEachIteration = (float)distanceToShrink / (float)maxCount;
        int metersForCircle = 0;

        int zoomLevel = ((MapView) findViewById(R.id.trail_map)).getZoomLevel();

			@Override
			public void run() {

				double distance = 0;
				double distInMeters = 0;

				

				if (null != currentFolder && currentFolder.getPlacemarks().size() > 0) {
					double[] coords = getMarkerCoordinates(currentFolder.getPlacemarks().get(0).coordinates);
					distance = calculateDistanceFromMyLocation(coords[1], coords[0]);

					distInMeters = distance/1609.34 + maxMetersForCircle;
				}
			    if (zoomLevel < 8) {
			    	 //how big the circle is at zoom 7 or less
		            largestCircleMeters = maxMetersForCircle/2;
		        } else if (zoomLevel > 7 && zoomLevel < 13) {
		        	 //how big the circle is at zoom 8-12
		            largestCircleMeters = maxMetersForCircle/3; 
		        } else if (zoomLevel > 12) {
		        	 //how big the circle is at zoom 13+
		            largestCircleMeters = maxMetersForCircle/6; 
		        } else {
		        	 //how big the circle is at unknown zoom
		            largestCircleMeters = maxMetersForCircle/3; 
		        }
	        
			    distanceToShrink = largestCircleMeters - smallestCircleMeters;

				if (null != currentLocation 
						&& count < maxCount
						) 
				{
                    metersForCircle = (int) (distanceToShrink - (count * distanceToShrinkEachIteration));
                    if (metersForCircle < 0) metersForCircle = 0;
//					Log.e(tag, "Circle animation fire. Count = " + count + " Meters = " + metersForCircle);
					mMyLocationOverlay.setMeters(metersForCircle);
				    ((MapView) findViewById(R.id.trail_map)).postInvalidate();
					count++;
				}
                else
                {
                    //Cancel timer

                }

			}

        }
        	
	private void setupViews() {

        mMyLocationOverlay = new MyOwnLocationOverlay(BoulderCountyMap.this, (MapView) findViewById(R.id.trail_map));
		BoulderCountyMap.this.mc = ((MapView) findViewById(R.id.trail_map)).getController();

	    parkingMarker = this.getResources().getDrawable(R.drawable.parking);
	    trailheadMarker = this.getResources().getDrawable(R.drawable.trailhead);

	    if (null == mMyLocationOverlay || !mMyLocationOverlay.isMyLocationEnabled())
	    	((ImageButton)findViewById(R.id.locate_image)).setImageResource(R.drawable.location_off_l);
	    
       ((RelativeLayout)findViewById(R.id.select_locate_me)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mMyLocationOverlay.isMyLocationEnabled()) {
			        mMyLocationOverlay.enableMyLocation();
				}

				Toast.makeText(BoulderCountyMap.this, "Locate me selected", Toast.LENGTH_LONG).show();
				mMyLocationOverlay.runOnFirstFix(new Runnable() {
					
					GeoPoint gp = ((MapView) findViewById(R.id.trail_map)).getMapCenter();
//					int zoom = ((MapView) findViewById(R.id.trail_map)).getZoomLevel();

					public void run() {

						int latSpan = ((MapView) findViewById(R.id.trail_map)).getLatitudeSpan();
						int lonSpan = ((MapView) findViewById(R.id.trail_map)).getLongitudeSpan();


						((MapView) findViewById(R.id.trail_map)).getController().
							animateTo
							(
								gp,
                                new Runnable() {

                                    @Override
                                    public void run() {   //This fires after the map is centered [on user location - original implementation centered map on user location]

                                        //Start user location animated circle
                                        ((MapView) findViewById(R.id.trail_map)).getOverlays().add(mMyLocationOverlay);
                                        if (timer != null) {
                                            timer.cancel();
                                            timer = null;
                                        }
                                        timer = new Timer();
                                        timer.scheduleAtFixedRate(new MyTimerTask(), 0, fireRateForCircleTimer);


                                    }
                                }
						);

                                if (null != mMyLocationOverlay.getMyLocation()) {
            						((MapView) findViewById(R.id.trail_map)).getController().zoomToSpan(
            								latSpan,
            								lonSpan
            								);
                                } else {
                                	Toast.makeText(BoulderCountyMap.this, "no data for current location...", Toast.LENGTH_SHORT).show();
                                	// pop up dialog?!?
                                }
					}
				});
			}
       });
	    
       ((ImageButton)findViewById(R.id.locate_image)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mMyLocationOverlay.isMyLocationEnabled()) {
			        mMyLocationOverlay.enableMyLocation();
				}

				Toast.makeText(BoulderCountyMap.this, "Locate...", Toast.LENGTH_LONG).show();
				mMyLocationOverlay.runOnFirstFix(new Runnable() {
					
					GeoPoint gp = ((MapView) findViewById(R.id.trail_map)).getMapCenter();
//					int zoom = ((MapView) findViewById(R.id.trail_map)).getZoomLevel();

					public void run() {

						int latSpan = ((MapView) findViewById(R.id.trail_map)).getLatitudeSpan();
						int lonSpan = ((MapView) findViewById(R.id.trail_map)).getLongitudeSpan();


						((MapView) findViewById(R.id.trail_map)).getController().
							animateTo
							(
								gp,
                                new Runnable() {

                                    @Override
                                    public void run() {   //This fires after the map is centered [on user location...]

                                        //Start user location animated circle
                                        ((MapView) findViewById(R.id.trail_map)).getOverlays().add(mMyLocationOverlay);
                                        if (timer != null) {
                                            timer.cancel();
                                            timer = null;
                                        }
                                        timer = new Timer();
                                        timer.scheduleAtFixedRate(new MyTimerTask(), 0, fireRateForCircleTimer);


                                    }
                                }
						);

                                if (null != mMyLocationOverlay.getMyLocation()) {
            						((MapView) findViewById(R.id.trail_map)).getController().zoomToSpan(
            								latSpan,
            								lonSpan
            								);
                                } else {
                                	Toast.makeText(BoulderCountyMap.this, "no data for current location...", Toast.LENGTH_SHORT).show();
                                	// pop up dialog
                                }
					}
				});
			}
       });

	    ((CheckBox)findViewById(R.id.checkBox1)).setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.e(tag, "checking for satellite on/off");
			Log.e(tag, "toggle.isSelected =" + ((CheckBox)findViewById(R.id.checkBox1)).isSelected());
			if (((CheckBox)findViewById(R.id.checkBox1)).isChecked()) {
				((MapView) findViewById(R.id.trail_map)).setSatellite(true);
			}
			else  {
				Log.e(tag, "toggle.isSelected =" + ((CheckBox)findViewById(R.id.checkBox1)).isChecked());
				((MapView) findViewById(R.id.trail_map)).setSatellite(false);
			}
		}
	});

        if (null == app.getCurrentFolder())
        {
        	mapAll = true;
        	BoulderCountyApplication.isMapAll = true;
        	fromList = false;
        	if (!((MapView) findViewById(R.id.trail_map)).getOverlays().isEmpty()) {
            	((MapView) findViewById(R.id.trail_map)).getOverlays().clear();
    			((MapView) findViewById(R.id.trail_map)).removeAllViews();
            	((MapView) findViewById(R.id.trail_map)).getController().setZoom(10);
            	((MapView) findViewById(R.id.trail_map)).postInvalidate();
        	}
//			Log.e(tag+".setupViews", "map overlay size=[" + ((MapView) findViewById(R.id.trail_map)).getOverlays().size());
        	new newDrawAll().execute();
        } else {
        	mapAll = false;
        	BoulderCountyApplication.isMapAll = false;
        	fromList = true;

        	drawTrails(); // clear done in drawTrails
        }
        Log.e(tag, "");
	}

	public void drawTrails() {
		mapAll = false;
		// clear the map
		if ( !((MapView) findViewById(R.id.trail_map)).getOverlays().isEmpty() ) {
			((MapView) findViewById(R.id.trail_map)).getOverlays().clear();
			((MapView) findViewById(R.id.trail_map)).removeAllViews();
		}
		new DrawTrailRoute().execute();
	}

    @Override
    protected boolean isLocationDisplayed() {
        return displayLocation;
    }

    @Override
    protected boolean isRouteDisplayed() {
        return true;
    }

	@Override
	public void onBackPressed() {
		((MapView) findViewById(R.id.trail_map)).removeAllViews();
		if (!mapAll & !fromList) {
			if (!((MapView) findViewById(R.id.trail_map)).getOverlays().isEmpty()) {
				((MapView) findViewById(R.id.trail_map)).getOverlays().clear();
				((MapView) findViewById(R.id.trail_map)).postInvalidate();
			}
			mapAll = true;
			BoulderCountyApplication.isMapAll = true;
			new newDrawAll().execute();
		} else {
			if (null != app.currentFolder) {
				app.currentFolder.isSelected = false;
			}
			super.onBackPressed();
		}
	}

    @Override
    public void onResume() {
        super.onResume();

		if (!viewSet) { 
			viewSet = true;
			setupViews(); 
		}

		((MapView) findViewById(R.id.trail_map)).setSatellite(false);
	       ((CheckBox)findViewById(R.id.checkBox1)).setChecked(false);

        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Ask the user to enable GPS
             AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location Manager");
            builder.setMessage("This app requires use of your location - but GPS is currently disabled.\n"
                     +"Would you like to change these settings now?");
             builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                 public void onClick(DialogInterface dialog, int which) {
                     //Launch settings, allowing user to make a change
                     Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                 }
             });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                 public void onClick(DialogInterface dialog, int which) {
                    //No location service, no Activity
                    finish();
                 }
             });
            builder.create().show();
        }

		((MapView) findViewById(R.id.trail_map)).removeAllViews();

        //Get a cached location, if it exists
        currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (null != currentLocation) {
            setMyLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
        //Register for updates
        int minTime = 6000;
        float minDistance = 5;
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                minTime, minDistance, listener);
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
	    return true;
	}

	private void setMyLocation(double lat, double lon) {
        if (null != (TextView)findViewById(R.id.my_location) && null != currentLocation) {
    		NumberFormat formatter = new DecimalFormat("#0.000000");
    		StringBuilder locStrBuilder = new StringBuilder(getResources().getString(R.string.your_current_location));
    		locStrBuilder.append("   Lat:  ");
    		locStrBuilder.append(formatter.format(lat));
    		locStrBuilder.append("  Long:  ");
       		locStrBuilder.append(formatter.format(lon));
            ((TextView)findViewById(R.id.my_location)).setText(locStrBuilder.toString());
            // will need to scroll on smaller screens?!?
//          --((Button)findViewById(R.id.my_location)).setText(locStrBuilder.toString());-- must change in trail_mapview as well
        }

	}

    //Handle location callback events
    private LocationListener listener = new LocationListener() {

         @Override
         public void onLocationChanged(Location location) {
            currentLocation = location;
            if (null != currentLocation) {
                setMyLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
		    	((ImageButton)findViewById(R.id.locate_image)).setImageResource(R.drawable.location_searching_l);
            }
         }

         @Override
        public void onProviderDisabled(String provider) { }

         @Override
         public void onProviderEnabled(String provider) { }

         @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

    };

//    private class MyLocationOverlayExtension extends MyLocationOverlay {
//        public MyLocationOverlayExtension(Context context, MapView mapView) {
//            super(context, mapView);
//        }
//        @Override
//        public synchronized void onLocationChanged(Location location) {
//            super.onLocationChanged(location);
//        }
//    }

	@Override
	protected void onPause() {
		super.onPause();
		manager.removeUpdates(listener);
	    mMyLocationOverlay.disableMyLocation();
    	((ImageButton)findViewById(R.id.locate_image)).setImageResource(R.drawable.location_off_l);

	}

	private double[] getMarkerCoordinates(List<List<String>> coordList) {
		try {
			for (List<String> coordinates : coordList) {
				String[] s = coordinates.get(0).split(",");
				return new double[] { Double.parseDouble(s[0]), Double.parseDouble(s[1]) };
			}
		} catch (Exception e) {
			Log.e(tag+".getMarkerCoordinates", "Exception: " + e.toString());
		} finally {
		}
		return new double[] {0, 0}; // {longitude, latitude}
	}

    class MyItemizedOverlay 
    extends BalloonItemizedOverlay<OverlayItem>
    {
    	String title;
    	String snippet;

    	private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    	public MyItemizedOverlay(Drawable defaultMarker, MapView mapView, Folder folder) {
    		super(boundCenterBottom(defaultMarker), mapView, folder);

    		doPopulate();
    	}

    	public void addOverlayItem(int lat, int lon, String title, String snippet, Drawable altMarker, Placemark pm) {
    		super.pm = pm;
    		this.title = title;
    		this.snippet = snippet;

    		pmList.add(pmList.size(), pm);
    		GeoPoint point = new GeoPoint(lat, lon);
    		OverlayItem overlayItem = new OverlayItem(point, title, snippet);
    		addOverlayItem(overlayItem, altMarker);
    	}

    	private void addOverlayItem(OverlayItem overlayItem) {
    		mOverlays.add(overlayItem);

    		doPopulate();
    	}

    	public void addOverlayItem(OverlayItem overlayItem, Drawable altMarker) {
    		overlayItem.setMarker(boundCenterBottom(altMarker));
    		addOverlayItem(overlayItem);
    	}

        @Override
        public boolean onTouchEvent(MotionEvent event, MapView mapView)
        {  
            return false;
        }

    	@Override
    	protected OverlayItem createItem(int i) {
    		return mOverlays.get(i);
    	}

    	@Override
    	public int size() {
    		return mOverlays.size();
    	}

    	@Override
    	protected boolean onBalloonTap(int index, OverlayItem item) {
    		removeBalloonOverlay();
    		return true;
    	}

		@Override
		public boolean onTap(GeoPoint p, MapView mapView) {
			removeBalloonOverlay();
			return super.onTap(p, mapView);
		}

		 public void clear() {
			 if (!mOverlays.isEmpty()) {
					mOverlays.clear();

					doPopulate();		
			 }
		}

		 // New methods to support doing bulk overlays - then calling populate
	    	public void addOverlayList(List<OverlayItem> overlayitems) {
	    	    // Object temp[] = overlayitems.toArray();
	    	    try{
	    	        // for(int i = 0;i<temp.length;i++)
	    	        for(OverlayItem o : overlayitems)
	    	        {
	    	            // mOverlays.add((OverlayItem)temp[i]);
	    	            mOverlays.add(o);
	    	        }

	        	    doPopulate();
	    	    } catch (Error e)
	    	    {
	    	        Toast.makeText(BoulderCountyMap.this, "Error when adding the overlays: "+e.getMessage(), 
	    	                Toast.LENGTH_LONG).show();
	    	        Log.e(tag, "Error when adding the overlays: "+e.getMessage());
	    	    } catch (Exception ex)
	    	    {
	    	        Toast.makeText(BoulderCountyMap.this, "An Exception occured when adding the overlays: "+ex.toString(), 
	    	                Toast.LENGTH_LONG).show();
	    	        Log.e(tag, "An Exception occured when adding the overlays: "+ex.toString());
	    	    }
	    	}

	    	public void doPopulate(){
	    	    setLastFocusedIndex(-1);
	    	    populate();
	    	}
	    	
	    	@Override
	    	public String toString() {
	    		return "[" + title + 
	    		"] " +
	    		"[" + snippet +
	    		"] " +
	    		"[" + ((null == pm) ? "no placemark found" : pm.toString()) +
	    		"]";
	    	}
    }

	public String cleanPoints(String path) {
		if ( !" ".equalsIgnoreCase( path.substring(0, 1) ) && !"-".equalsIgnoreCase( path.substring(0, 1) )) 
		{
			int startIndex = path.indexOf(" ");
			path = path.substring(startIndex);
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
					break;
				}
			}
		}
		return path;
	}

	class newDrawAll extends AsyncTask<Void, List<Overlay>, List<Overlay>> 
	{

        private List<Overlay> markerOverlays;
        private ProgressDialog allTrailsDialog = new ProgressDialog(BoulderCountyMap.this);

        protected void onPreExecute() {
//          allTrailsDialog = new ProgressDialog(BoulderCountyMap.this);
        	isDrawing = true;
	        allTrailsDialog.setMessage("drawing trails...");
	        allTrailsDialog.show();
        }

		@Override
		protected List<Overlay> doInBackground(Void... params) {

			BoulderCountyApplication.isMapAll = true;
			double[] markerCoordinates = null;

			markerOverlays = new ArrayList<Overlay>();

			try {

		        routePathOverlay = new RoutePathOverlay();

		        Log.e(tag+".newDrawAll.doInBackground", "");
				for (List<GeoPoint> gp : app.allGeoPoints) {

                    //Add path to overlay for drawing later
                    routePathOverlay.addNewPathWithColor(gp, 0xFF4500);
				}


				for (Folder f : Folder.getTrailList()) {
					
//					Log.e(tag, "parsing trail system [" + f.getName());

						MyItemizedOverlay parkingOverlay = 
						 new MyItemizedOverlay(parkingMarker, (MapView) findViewById(R.id.trail_map), f);
						MyItemizedOverlay trailheadOverlay = 
							 new MyItemizedOverlay(trailheadMarker, (MapView) findViewById(R.id.trail_map), f);


						for (Placemark pm : f.parkingList) {
						 markerCoordinates = getMarkerCoordinates(pm.coordinates);
			    			GeoPoint geoPoint = new GeoPoint((int)(markerCoordinates[1]*1E6),(int)(markerCoordinates[0]*1E6));
			    			parkingOverlay.addOverlayItem((int)(markerCoordinates[1]*1E6),(int)(markerCoordinates[0]*1E6), pm.getName(), "Parking", parkingMarker, pm);
			    			centerPoint = geoPoint;
						}

						for (Placemark pm : f.trailheadList) {
							 markerCoordinates = getMarkerCoordinates(pm.coordinates);
				    			GeoPoint geoPoint = new GeoPoint((int)(markerCoordinates[1]*1E6),(int)(markerCoordinates[0]*1E6));
				    			StringBuilder snippetString = new StringBuilder();
				    			if (null != pm.trailDistance) {
					    			snippetString.append( pm.trailDistance );
					    			snippetString.append(", ");
				    			}
				    			if (null != pm.trailDifficulty) {
					    			snippetString.append( pm.trailDifficulty );
					    			snippetString.append(", ");
				    			}
				    			snippetString.append( (null == f.parentFolder.trailSystemName) ? pm.getName() : f.parentFolder.trailSystemName );
				    			trailheadOverlay.addOverlayItem((int)(markerCoordinates[1]*1E6),(int)(markerCoordinates[0]*1E6), pm.getName(), snippetString.toString(), trailheadMarker, pm);
				    			centerPoint = geoPoint;
						}
						parkingOverlay.doPopulate();
		    			markerOverlays.add(parkingOverlay);
		    			trailheadOverlay.doPopulate();
		    			markerOverlays.add(trailheadOverlay);

				}
				//publishProgress(markerOverlays);
			  } catch (Exception e) {
				  Log.e(tag, "Exception : " + e.toString());
			  }
			return markerOverlays;
		}

		
		@Override
		protected void onProgressUpdate(List<Overlay>... values) {
			super.onProgressUpdate(values);
//			Log.e(tag+".newDrawAll.onProgressUpdate", "****** map overlay count=[" + ((MapView) findViewById(R.id.trail_map)).getOverlays().size());
		}


		@Override
		protected void onPostExecute(List<Overlay> result) {
			super.onPostExecute(result);
			try {
                //Publish on finish
				((MapView) findViewById(R.id.trail_map)).getOverlays().add(routePathOverlay);
				((MapView) findViewById(R.id.trail_map)).getOverlays().addAll(result);
				((MapView) findViewById(R.id.trail_map)).postInvalidate();

//    			Log.e(tag+".newDrawAll.onPostExecute", "****** map overlay count=[" + ((MapView) findViewById(R.id.trail_map)).getOverlays().size());

				((MapView) findViewById(R.id.trail_map)).getController().setZoom(12);

				if (null != centerPoint) {
					((MapView) findViewById(R.id.trail_map)).getController().setCenter(centerPoint);
				} else {
					Log.e(tag, "centerPoint is null!!!!!");
				}

				((MapView) findViewById(R.id.trail_map)).setBuiltInZoomControls(true);
				isDrawing = false;

				allTrailsDialog.dismiss();
			} catch (Exception e) {
				// use popup dialog?!?
				Log.e(tag, "Exception : " + e.toString());
			}
		}

	}

	private int addParking(Folder f, List<Overlay> markerOverlays) {

		MyItemizedOverlay parkingOverlay = new MyItemizedOverlay(parkingMarker, (MapView) findViewById(R.id.trail_map), currentFolder);
		double[] markerCoordinates = null;

		// add markers...
		try {

			for (Placemark pm : f.parkingList) {
				 markerCoordinates = getMarkerCoordinates(pm.coordinates);
	    			GeoPoint geoPoint = new GeoPoint((int)(markerCoordinates[1]*1E6),(int)(markerCoordinates[0]*1E6));
	    			parkingOverlay.addOverlayItem((int)(markerCoordinates[1]*1E6),(int)(markerCoordinates[0]*1E6), pm.getName(), "Parking", parkingMarker, pm);
	    			centerPoint = geoPoint;
			}
			parkingOverlay.doPopulate();
			markerOverlays.add(parkingOverlay);

		} catch (Exception e) {
			Log.e(tag, "Exception: " + e.toString());
		}
		return 0;
	}

	private int addTrailhead(Folder f, List<Overlay> markerOverlays) {

		MyItemizedOverlay trailHeadOverlay = new MyItemizedOverlay(trailheadMarker, (MapView) findViewById(R.id.trail_map), currentFolder);
		double[] markerCoordinates = null;

		// add markers...
		try {
			for (Placemark pm : f.trailheadList) {
				 markerCoordinates = getMarkerCoordinates(pm.coordinates);
	    			GeoPoint geoPoint = new GeoPoint((int)(markerCoordinates[1]*1E6),(int)(markerCoordinates[0]*1E6));
	    			StringBuilder snippetString = new StringBuilder();
	    			if (null != pm.trailDistance) {
		    			snippetString.append( pm.trailDistance );
		    			snippetString.append(", ");
	    			}
	    			if (null != pm.trailDifficulty) {
		    			snippetString.append( pm.trailDifficulty );
		    			snippetString.append(", ");
	    			}
	    			snippetString.append( (null == currentFolder.parentFolder.trailSystemName) ? pm.getName() : currentFolder.parentFolder.trailSystemName );
	    			trailHeadOverlay.addOverlayItem((int)(markerCoordinates[1]*1E6),(int)(markerCoordinates[0]*1E6), pm.getName(), snippetString.toString(), trailheadMarker, pm);
	    			centerPoint = geoPoint;
			}
			trailHeadOverlay.doPopulate();
			markerOverlays.add(trailHeadOverlay);

		} catch (Exception e) {
			Log.e(tag, "Exception: " + e.toString());
		}
		return 0;
	}

	private int getRoutePaths(Folder f, List<Overlay> markerOverlays, int color) {
		
		List<GeoPoint> gpList = null;

		GeoPoint startGP = null;
		GeoPoint gp1 = null;
		GeoPoint gp2 = null;
		for (Placemark pm : f.getPlacemarks()) 
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
							// src
							startGP = new GeoPoint((int) (Double
									.parseDouble(lngLat[1]) * 1E6), (int) (Double
									.parseDouble(lngLat[0]) * 1E6));
							centerPoint = startGP;
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

                    //Add path to overlay list for drawing later
                    routePathOverlay.addNewPathWithColor(gpList, color);
				}
		}
		return 0;
	}

	private double calculateDistanceFromMyLocation(double trailLat, double trailLon) {

		double lat_d = trailLat;// * 1E6;
		double lon_d = trailLon;// * 1E6;

		mMyLocationOverlay.getLastFix().getLatitude();

         double deltaLat = Math.toRadians(mMyLocationOverlay.getLastFix().getLatitude() - lat_d);
         double deltaLon = Math.toRadians(mMyLocationOverlay.getLastFix().getLongitude() - lon_d);
         lat_d = Math.toRadians(lat_d);
         double myLat = Math.toRadians(mMyLocationOverlay.getLastFix().getLatitude());
         lon_d = Math.toRadians(lon_d);
         double myLon = Math.toRadians(mMyLocationOverlay.getLastFix().getLatitude()); // why not Longitude?!?
         double earthRadius = 6371;
         double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.cos(lat_d) * Math.cos(myLat) * Math.sin(deltaLon/2) * Math.sin(deltaLon/2);
         double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
         double distanceFromCurrentLocation = earthRadius * c;
         
         return distanceFromCurrentLocation;
	}

}