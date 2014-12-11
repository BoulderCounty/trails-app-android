/***
 * Copyright (c) 2010 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.bouldercounty.parks.trails;


import java.util.ArrayList;
import java.util.List;

import org.bouldercounty.parks.trails.data.Folder;
import org.bouldercounty.parks.trails.data.Placemark;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import org.bouldercounty.parks.trails.R;


/**
 * An abstract extension of ItemizedOverlay for displaying an information balloon
 * upon screen-tap of each marker overlay.
 * 
 * @author Jeff Gilfelt
 */
public abstract class BalloonItemizedOverlay<Item extends OverlayItem> extends ItemizedOverlay<Item> {


	public static String tag = "BalloonItemizedOverlay";

	private MapView mapView;
	private BalloonOverlayView<Item> balloonView;
	private View clickRegion;
	private int viewOffset = 45;
	final MapController mc;
	private Item currentFocussedItem;
	private int currentFocussedIndex;

	String trailName, trailSystem, trailDistance;
	Folder folder;
	protected Placemark pm;
	boolean isMapAll, isParking;

	protected List<Placemark> pmList = new ArrayList<Placemark>();;


	public BalloonItemizedOverlay(Drawable defaultMarker, MapView mapView, Folder folder) {
		super(defaultMarker);

		this.mapView = mapView;
		this.isParking = true;
		this.isMapAll = false;
		this.folder = folder;

		mc = mapView.getController();
	}

	/**
	 * Set the horizontal distance between the marker and the bottom of the information
	 * balloon. The default is 0 which works well for center bounded markers. If your
	 * marker is center-bottom bounded, call this before adding overlay items to ensure
	 * the balloon hovers exactly above the marker. 
	 * 
	 * @param pixels - The padding between the center point and the bottom of the
	 * information balloon.
	 */
	public void setBalloonBottomOffset(int pixels) {
		viewOffset = pixels;
	}
	public int getBalloonBottomOffset() {
		return viewOffset;
	}

	/**
	 * Override this method to handle a "tap" on a balloon. By default, does nothing 
	 * and returns false.
	 * 
	 * @param index - The index of the item whose balloon is tapped.
	 * @param item - The item whose balloon is tapped.
	 * @return true if you handled the tap, otherwise false.
	 */
	protected boolean onBalloonTap(int index, Item item) {
		return removeBalloonOverlay();
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	protected final boolean onTap(int index) {

		currentFocussedIndex = index;
		currentFocussedItem = createItem(index);

		createAndDisplayBalloonOverlay(index);

		mc.setCenter(currentFocussedItem.getPoint());

		return true;
	}

	/**
	 * Creates the balloon view. Override to create a sub-classed view that
	 * can populate additional sub-views.
	 */
	protected BalloonOverlayView<Item> createBalloonOverlayView(int index) {
		removeBalloonOverlay();

		BalloonOverlayView<Item> bov = new BalloonOverlayView<Item>(getMapView().getContext(), getBalloonBottomOffset(), this.folder, pmList.get(index), mapView);
//		Log.e(tag, "adding balloon overlay [" + bov);
		return bov;
	}

	/**
	 * Expose map view to subclasses.
	 * Helps with creation of balloon views. 
	 */
	protected MapView getMapView() {
		return mapView;
	}

	/**
	 * Sets the visibility of this overlay's balloon view to GONE. 
	 */
	protected void hideBalloon() {
		if (balloonView != null) {
			balloonView.setVisibility(View.GONE);
		}
		balloonView = null;
	}

	/**
	 * Hides the balloon view for any other BalloonItemizedOverlay instances
	 * that might be present on the MapView.
	 * 
	 * @param overlays - list of overlays (including this) on the MapView.
	 */
	protected void hideOtherBalloons(List<Overlay> overlays) {

		for (Overlay overlay : overlays) {
			if (overlay instanceof BalloonItemizedOverlay<?> && overlay != this) {
				((BalloonItemizedOverlay<?>) overlay).hideBalloon();
			}
		}

	}

	/**
	 * Sets the onTouchListener for the balloon being displayed, calling the
	 * overridden {@link #onBalloonTap} method.
	 */
	private OnTouchListener createBalloonTouchListener() {
		return new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				View l =  ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
				Drawable d = l.getBackground();

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int[] states = {android.R.attr.state_pressed};
					if (d.setState(states)) {
						d.invalidateSelf();
					}
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					int newStates[] = {};
					if (d.setState(newStates)) {
						d.invalidateSelf();
					}
					// call overridden method
					onBalloonTap(currentFocussedIndex, currentFocussedItem);
					return true;
				} else {
					return false;
				}

			}
		};
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#getFocus()
	 */
	@Override
	public Item getFocus() {
		return currentFocussedItem;
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#setFocus(Item)
	 */
	@Override
	public void setFocus(Item item) {
		currentFocussedItem = item;

		if (currentFocussedItem == null){
			hideBalloon();
		}
		else{
//			createAndDisplayBalloonOverlay();
		}	
	}


	/**
	 * Creates and displays the balloon overlay by recycling the current 
	 * balloon or by inflating it from xml. 
	 * @return true if the balloon was recycled false otherwise 
	 */
	private boolean createAndDisplayBalloonOverlay(int index) { 
//		Log.e(tag+".createAndDisplayBalloonOverlay", "creating ballon ovelay with pm=[" + pm);
		boolean isRecycled;
//		Log.e(tag, "map overlay size = [" + mapView.getOverlays().size());
		if (mapView.getOverlays().size() > 1) { // in case overlays fail to create...
			if (balloonView == null) { // should always be null - can't recycle because placemark needs to be specific
				balloonView = createBalloonOverlayView(index);
				clickRegion = (View) balloonView.findViewById(R.id.balloon_inner_layout);
				clickRegion.setOnTouchListener(createBalloonTouchListener());
				isRecycled = false;
			} else {
				isRecycled = true;
			}

			balloonView.setVisibility(View.GONE);

			List<Overlay> mapOverlays = mapView.getOverlays();
			if (mapOverlays.size() > 1) {
				hideOtherBalloons(mapOverlays);
			}

			if (currentFocussedItem != null)
				balloonView.setData(currentFocussedItem);

			GeoPoint point = currentFocussedItem.getPoint();
			MapView.LayoutParams params = new MapView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
					MapView.LayoutParams.BOTTOM_CENTER);
			params.mode = MapView.LayoutParams.MODE_MAP;

			balloonView.setVisibility(View.VISIBLE);

			if (isRecycled) {
				balloonView.setLayoutParams(params);
			} else {
				mapView.addView(balloonView, params);
			}

			return isRecycled;
		} else {
			return false; // ??!??
		}

	}

	protected boolean removeBalloonOverlay() {
//		Log.e(tag, "balloonView =[");
		if (balloonView != null) {
//			Log.e(tag, "setting balloonView visibility to GONE");
			balloonView.setVisibility(View.GONE);
			balloonView = null;
			return true;
		}
		return false;
	}

	@Override
	protected Item createItem(int i) {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}
	
}