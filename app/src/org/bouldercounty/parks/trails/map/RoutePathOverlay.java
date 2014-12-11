package org.bouldercounty.parks.trails.map;


import java.util.*;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RoutePathOverlay extends Overlay {

	public static String tag = "RoutePathOverlay";

	boolean sizePrinted;

	// orange: 0xED872D; purple: 0x8A2BE2
	private boolean _drawStartEnd;
	Paint paint;
	Path path = new Path();
	Point pointA = new Point();

    public ArrayList listOfPaths;

	GeoPoint gPointA;

	public RoutePathOverlay() {
        if (paint == null)
            paint = new Paint();
	}

    public void addNewPathWithColor(List<GeoPoint>points, int color)
    {
        ArrayList newPathList = new ArrayList(points);
        if (listOfPaths == null) listOfPaths = new ArrayList();

        //Create dictionary with required items
        Hashtable theDict = new Hashtable();
        theDict.put("list", newPathList);
        theDict.put("color", color);

        //Add dictionary to list of paths
        listOfPaths.add(theDict);
    }

    //
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {

    	if (!sizePrinted && listOfPaths != null) {
    		Log.e(tag, "listOfPaths size=[" + listOfPaths.size());
    		sizePrinted = true;
    	}

        super.draw(canvas, mapView, shadow);


        //Optimizations...
        if (pointA == null) pointA = new Point();


        //Current zoom level
        int zoomLevel = mapView.getZoomLevel();

        //Current visible rect
        RectF mapRect = new RectF(mapView.getLeft()-30, mapView.getTop()-100, mapView.getRight()+30, mapView.getBottom());

        if (shadow == false && listOfPaths != null) {

	        //Gather all paths stored in list of paths
	        int c = listOfPaths.size();
	        for (int z=0; z<c; z++)
	        {
	            //Get dictionary
	            Hashtable theDict = (Hashtable) listOfPaths.get(z);
	
	            //Set path
	            ArrayList <GeoPoint> _points = (ArrayList) theDict.get("list");
	
	            //Set color
	            int _pathColor = (Integer) theDict.get("color");
	
	
	            //Draw this path
	            Projection projection = mapView.getProjection();
	                Point startPoint = null, endPoint = null;
	                Path path = new Path();
	                //We are creating the path
	
	                //How many points in this path
	                int size = _points.size();
	
	                //Detail level
	                int lowDetailDivider = 50; // 50?
	                int skipper = size / lowDetailDivider;
	
	                //Detail gradually fades in
	                if (zoomLevel <= 19){
	                    int amount = zoomLevel-8;
	                    float multiplier = amount*2f;
	                    int extra = (int)(amount * multiplier);
	                    lowDetailDivider += extra;
	                    skipper = size / lowDetailDivider;
	                }
	
	                //Super high detail up close
	                if (zoomLevel > 19)
	                {
	                    skipper = size / 800; // 800?
	                }
	
	                //Minimum of 1
	                if (skipper < 1) skipper = 1;
	
	                //indicate if skipped last point
	                Boolean skippedPoint = false;
	
	                for (int i = 0; i < size; i+=skipper) {
	
	                    GeoPoint gPointA = (i < size) ? _points.get(i) : _points.get(size-1);
	                    projection.toPixels(gPointA, pointA);
	
	                    //Only draw points within visible map rect
	                    Boolean pointIsWithinVisibleRect = ((pointA.x > mapRect.left && pointA.x < mapRect.right) && (pointA.y < mapRect.bottom && pointA.y > mapRect.top)) ? true : false;
	                    if (pointIsWithinVisibleRect){
	
	                        if (i == 0) { //This is the start point
	//						Log.e("RoutePathOverlay.draw", "drawing startPoint");
	                            startPoint = pointA;
	                            path.moveTo(pointA.x, pointA.y);
	
	                        }
	                        else {
	                            if (i == _points.size() - 1) { //This is the end point
	//							Log.e("RoutePathOverlay.draw", "drawing endPoint");
	                                endPoint = pointA;
	
	                                if (!skippedPoint){
	                                    path.lineTo(pointA.x, pointA.y);
	                                }
	
	                            }
	                            else  //Is a middle point
	                            {
	
	                                if (!skippedPoint){
	                                    path.lineTo(pointA.x, pointA.y);
	                                }
	                                else{
	                                    //Skipped last point
	                                    path.moveTo(pointA.x, pointA.y);
	                                    skippedPoint = false;
	                                }
	
	                            }
	
	                        }
	
	                    }
	                    else{
	                        skippedPoint = true;
	                    }
	
	
	                }
	
	                paint.setAntiAlias(true);
	                paint.setColor(_pathColor);
	                paint.setStyle(Paint.Style.STROKE);
	                paint.setStrokeWidth(3); // 2 - at most!?!
	                paint.setAlpha(90);
	
	                if (!path.isEmpty())  {
	
	                    //Clip drawing area to visible map rect to ensure only drawing on screen
	                    canvas.clipRect(mapRect);
	
	                    //Draw paths
	                    canvas.drawPath(path, paint);
	
	                }	
	
	        }
        
    	} // if no shadow


    }

	public boolean getDrawStartEnd() {
		return _drawStartEnd;
	}

	public void setDrawStartEnd(boolean markStartEnd) {
		_drawStartEnd = markStartEnd;
	}
}