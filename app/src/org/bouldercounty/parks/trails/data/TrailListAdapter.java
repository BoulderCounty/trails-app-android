package org.bouldercounty.parks.trails.data;


import org.bouldercounty.parks.trails.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class TrailListAdapter extends BaseAdapter {

	public static String tag = "TrailListAdapter";

	private List<Folder> trailList;
	private Context context;
	private LayoutInflater mInflater;
	public int selectedItem;
	
	public TrailListAdapter(Context context, List<Folder> trailList) {
		this.trailList = trailList;
		this.context = context;

		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		return (null == trailList) ? 0 : trailList.size();
	}

	public Folder getItem(int position) {
		return (null == trailList) ? null : trailList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (null == convertView) {
			convertView = mInflater.inflate(R.layout.trail_list_item, null);

			holder = new ViewHolder();
			holder.trailSystem = (TextView)convertView.findViewById(R.id.park_name); 
			holder.trailName = (TextView)convertView.findViewById(R.id.trail_name); 
			holder.trailDistance = (TextView)convertView.findViewById(R.id.trail_length); 
			holder.trailDifficulty = (TextView)convertView.findViewById(R.id.trail_difficulty); 
			holder.hikeImage = (ImageView)convertView.findViewById(R.id.hiker_image);
			holder.bikeImage = (ImageView)convertView.findViewById(R.id.bike_image);
			holder.dogImage = (ImageView)convertView.findViewById(R.id.dog_image);
			holder.horseImage = (ImageView)convertView.findViewById(R.id.horse_image);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		holder.trailSystem.setText(
				(null == trailList.get(position).getName()) ? "no trail name" : trailList.get(position).getName().trim()
				);
		holder.trailName.setText(
				(null == trailList.get(position).trailSystemName) ? "no trail system" : trailList.get(position).trailSystemName.trim()
				);
		holder.trailDistance.setText(
				(null == trailList.get(position).trailheadList.get(0).trailDistance) ? "no trail distance" : trailList.get(position).trailheadList.get(0).trailDistance
				);
		StringBuilder difficulty = new StringBuilder();
		difficulty.append(
				(null == trailList.get(position).trailheadList.get(0).trailDifficulty) ? "" : trailList.get(position).trailheadList.get(0).trailDifficulty
				);
		difficulty.append(
				((null == trailList.get(position).trailheadList.get(0).extraDescription) || (trailList.get(position).trailheadList.get(0).extraDescription.length() < 2)) ? 
						"" : (";" + trailList.get(position).trailheadList.get(0).extraDescription)
				);
		holder.trailDifficulty.setText(
//				(null == trailList.get(position).trailheadList.get(0).trailDifficulty) ? "" : trailList.get(position).trailheadList.get(0).trailDifficulty
						difficulty.toString()
				);
		// set visibility on images...
		if (trailList.get(position).trailheadList.get(0).hike) {
			holder.hikeImage.setVisibility(View.VISIBLE);
		} else {
			holder.hikeImage.setVisibility(View.GONE);
		}
		if (trailList.get(position).trailheadList.get(0).horse) {
			holder.horseImage.setVisibility(View.VISIBLE);
		} else {
			holder.horseImage.setVisibility(View.GONE);
		}
		if (trailList.get(position).trailheadList.get(0).bike) {
			holder.bikeImage.setVisibility(View.VISIBLE);
		} else {
			holder.bikeImage.setVisibility(View.GONE);
		}
		if (trailList.get(position).trailheadList.get(0).dog) {
			holder.dogImage.setVisibility(View.VISIBLE);
		} else {
			holder.dogImage.setVisibility(View.GONE);
		}
		// do an even/odd variation for background
		if (position % 2 == 0) {
			convertView.setBackgroundResource(R.drawable.rowb_selector);
		} else {
			convertView.setBackgroundResource(R.drawable.rowa_selector);
		}

		return convertView;
	}

	public void forceReload() {
		notifyDataSetChanged();
	}

	public void clear() {
		trailList = new ArrayList<Folder>();
		notifyDataSetChanged();
	}
	
	public void sort() {
		Collections.sort(this.trailList);
	}

	public void setTrailList(List<Folder> trailList) 
	{
		this.trailList = trailList;
		notifyDataSetChanged();
		Log.e(tag, "trailList size=" + trailList.size());
	}

	public void setContext(Context context) {
		this.context = context;
	}

	static class ViewHolder {
		TextView trailSystem;
		TextView trailName;
		TextView trailDistance;
		TextView trailDifficulty;
		ImageView hikeImage;
		ImageView bikeImage;
		ImageView dogImage;
		ImageView horseImage;
	}

}