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


import org.bouldercounty.parks.trails.data.Folder;
import org.bouldercounty.parks.trails.data.Placemark;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import org.bouldercounty.parks.trails.R;

/**
 * A view representing a MapView marker information balloon.
 * <p>
 * This class has a number of Android resource dependencies:
 * <ul>
 * <li>drawable/balloon_overlay_bg_selector.xml</li>
 * <li>drawable/balloon_overlay_close.png</li>
 * <li>drawable/balloon_overlay_focused.9.png</li>
 * <li>drawable/balloon_overlay_unfocused.9.png</li>
 * <li>layout/balloon_map_overlay.xml</li>
 * </ul>
 * </p>
 * 
 * @author Jeff Gilfelt
 *
 */
public class BalloonOverlayView<Item extends OverlayItem> extends FrameLayout {

	public static String tag = "BalloonOverlayView";


	private LinearLayout layout;
	private TextView title;
	private TextView snippet;
	
	Folder folder;
	Placemark placemark;
	
	View v;

	BoulderCountyApplication app;

	Context context;

	public BalloonOverlayView(final Context context, int balloonBottomOffset, final Folder folder, final Placemark pm, final MapView mapView) 
	{

		super(context);
		app = (BoulderCountyApplication)context.getApplicationContext();

		this.context = context;
		this.folder = folder;

//		setPadding(10, 0, 10, balloonBottomOffset);
		setPadding(10, 0, 5, balloonBottomOffset);
		layout = new LinearLayout(context);
		layout.setVisibility(VISIBLE);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflater.inflate(R.layout.balloon_overlay, layout);
		title = (TextView) v.findViewById(R.id.balloon_item_title);
		title.setText(folder.getName());
		snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
		snippet.setText("Parking");
		placemark = pm;

		if (!BoulderCountyApplication.isMapAll && !pm.isParking) {
//			Log.e(tag, "<nonMapAll><nonParking>pm isParking=[" + pm.isParking);
			((LinearLayout) v.findViewById(R.id.use)).setVisibility(View.VISIBLE);
			((ImageView) v.findViewById(R.id.view_trails_btn)).setVisibility(View.GONE);
			if (pm.dog) {
				((ImageView) v.findViewById(R.id.dog)).setVisibility(View.VISIBLE);
			} else {
				((ImageView) v.findViewById(R.id.dog)).setVisibility(View.GONE);
			}
			if (pm.bike) {
				((ImageView) v.findViewById(R.id.bike)).setVisibility(View.VISIBLE);
			} else {
				((ImageView) v.findViewById(R.id.bike)).setVisibility(View.GONE);
			}
			if (pm.hike) {
				((ImageView) v.findViewById(R.id.hiker)).setVisibility(View.VISIBLE);
			} else {
				((ImageView) v.findViewById(R.id.hiker)).setVisibility(View.GONE);
			}
			if (pm.horse) {
				((ImageView) v.findViewById(R.id.horse)).setVisibility(View.VISIBLE);
			} else {
				((ImageView) v.findViewById(R.id.horse)).setVisibility(View.GONE);
			}
		} else if (!BoulderCountyApplication.isMapAll && pm.isParking) {
//			Log.e(tag, "<nonMapAll><Parking>pm isParking=[" + pm.toString());
			((LinearLayout) v.findViewById(R.id.use)).setVisibility(View.GONE);
			((ImageView) v.findViewById(R.id.view_trails_btn)).setVisibility(View.VISIBLE);
			((ImageView) v.findViewById(R.id.view_trails_btn)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				  try {
					  	
						if (!BoulderCountyApplication.isMapAll && pm.isParking) {
//								Log.e(tag, "pm=[" + pm);
//								Log.e(tag, "placemark=[" + placemark);
				                String address = (null != pm.parkingAddress && pm.parkingAddress.length() > 5) ? pm.parkingAddress : "1521 CR 126 Nederland CO 80466";
				                address	= address.replace(' ', '+');
//				                Log.e(tag, "address=[" + address);

									Intent geoIntent = new Intent(android.content.Intent.ACTION_VIEW, 
											Uri.parse("geo:0,0?q=" + address ));
						            BalloonOverlayView.this.context.startActivity(geoIntent);
						} else {
							Toast.makeText(BalloonOverlayView.this.getContext(), folder.getName(), Toast.LENGTH_SHORT).show();
							// clear the map, kick off route drawing...
							((BoulderCountyApplication)context.getApplicationContext()).currentFolder = folder;
							((BoulderCountyMap)context).drawTrails();
						}

				  } catch (Exception e) {
					  Log.e(tag, "Exception : " + e.toString());
				  }
				}
			});
		} else if (BoulderCountyApplication.isMapAll && pm.isParking) {
			((ImageView) v.findViewById(R.id.view_trails_btn)).setVisibility(View.VISIBLE);
			((LinearLayout) v.findViewById(R.id.use)).setVisibility(View.GONE);
			((ImageView) v.findViewById(R.id.view_trails_btn)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				  try {
					  	
//						if (!BoulderCountyApplication.isMapAll && pm.isParking) {
//								Log.e(tag, "pm=[" + pm);
//								Log.e(tag, "placemark=[" + placemark);
				                String address = (null != pm.parkingAddress && pm.parkingAddress.length() > 5) ? pm.parkingAddress : "1521 CR 126 Nederland CO 80466";
				                address	= address.replace(' ', '+');
//				                Log.e(tag, "address=[" + address);

									Intent geoIntent = new Intent(android.content.Intent.ACTION_VIEW, 
											Uri.parse("geo:0,0?q=" + address ));
						            BalloonOverlayView.this.context.startActivity(geoIntent);
//						} else {
//							Toast.makeText(BalloonOverlayView.this.getContext(), folder.getName(), Toast.LENGTH_SHORT).show();
//							// clear the map, kick off route drawing...
//							((BoulderCountyApplication)context.getApplicationContext()).currentFolder = folder;
//							((BoulderCountyMap)context).drawTrails();
//						}

				  } catch (Exception e) {
					  Log.e(tag, "Exception : " + e.toString());
				  }
				}
			});
		}	else {
			((LinearLayout) v.findViewById(R.id.use)).setVisibility(View.VISIBLE);
			((ImageView) v.findViewById(R.id.view_trails_btn)).setVisibility(View.VISIBLE);
			if (pm.dog) {
				((ImageView) v.findViewById(R.id.dog)).setVisibility(View.VISIBLE);
			} else {
				((ImageView) v.findViewById(R.id.dog)).setVisibility(View.GONE);
			}
			if (pm.bike) {
				((ImageView) v.findViewById(R.id.bike)).setVisibility(View.VISIBLE);
			} else {
				((ImageView) v.findViewById(R.id.bike)).setVisibility(View.GONE);
			}
			if (pm.hike) {
				((ImageView) v.findViewById(R.id.hiker)).setVisibility(View.VISIBLE);
			} else {
				((ImageView) v.findViewById(R.id.hiker)).setVisibility(View.GONE);
			}
			if (pm.horse) {
				((ImageView) v.findViewById(R.id.horse)).setVisibility(View.VISIBLE);
			} else {
				((ImageView) v.findViewById(R.id.horse)).setVisibility(View.GONE);
			}

//			Log.e(tag, "<mapAll>pm isParking=[" + pm.isParking);
			((ImageView) v.findViewById(R.id.view_trails_btn)).setVisibility(View.VISIBLE);
			((ImageView) v.findViewById(R.id.view_trails_btn)).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
				  try {

						if (!BoulderCountyApplication.isMapAll && pm.isParking) {
//								Log.e(tag, "pm=[" + pm);
//								Log.e(tag, "placemark=[" + placemark);
				                String address = (null != pm.parkingAddress && pm.parkingAddress.length() > 5) ? pm.parkingAddress : "1521 CR 126 Nederland CO 80466";
				                address	= address.replace(' ', '+');
//				                Log.e(tag, "address=[" + address);

									Intent geoIntent = new Intent(android.content.Intent.ACTION_VIEW, 
											Uri.parse("geo:0,0?q=" + address ));
						            BalloonOverlayView.this.context.startActivity(geoIntent);
						} else {
							Toast.makeText(BalloonOverlayView.this.getContext(), folder.getName(), Toast.LENGTH_SHORT).show();
							// clear the map, kick off route drawing...
							folder.isSelected = true;
							((BoulderCountyApplication)context.getApplicationContext()).currentFolder = folder;
							((BoulderCountyMap)context).drawTrails();
						}

				  } catch (Exception e) {
					  Log.e(tag, "Exception : " + e.toString());
				  }
				}
			});
		}

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;

		addView(layout, params);

	}

	/**
	 * Sets the view data from a given overlay item.
	 * 
	 * @param item - The overlay item containing the relevant view data 
	 * (title and snippet). 
	 */
	public void setData(Item item) {

		layout.setVisibility(VISIBLE);
		if (item.getTitle() != null) {
			title.setVisibility(VISIBLE);
			title.setText(item.getTitle());
		} else {
			title.setVisibility(GONE);
		}
		if (item.getSnippet() != null) {
			snippet.setVisibility(VISIBLE);
			snippet.setText(item.getSnippet());
		} else {
			snippet.setVisibility(GONE);
		}

	}

//	private double[] getMarkerCoordinates(List<List<String>> coordList) {
//		try {
//			for (List<String> coordinates : coordList) {
//				String[] s = coordinates.get(0).split(",");
//				return new double[] { Double.parseDouble(s[0]), Double.parseDouble(s[1]) };
//			}
//		} catch (Exception e) {
//			Log.e(tag+".getMarkerCoordinates", "Exception: " + e.toString());
//		} finally {
//		}
//		return new double[] {0, 0};
//	}

	@Override
	public String toString() {
		return "[" + folder.name +
		"] [" + placemark +
		"]";
	}

}