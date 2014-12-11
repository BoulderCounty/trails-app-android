package org.bouldercounty.parks.trails;


import java.util.ArrayList;

import org.bouldercounty.parks.trails.data.Folder;
import org.bouldercounty.parks.trails.data.TrailListAdapter;

import org.bouldercounty.parks.trails.R;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;


public class BoulderCountyTrailList extends ListActivity {

	public final static String tag = "BoulderCountyTrailList";

	BoulderCountyApplication app;
    private TrailListAdapter adapter;
    
    private Folder lastSelection;

    LocationManager manager;
    Location currentLocation;
    String selectedProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_trails);

        app = (BoulderCountyApplication)getApplication();
        manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

    	Log.e(tag, "");

    	init();
    }

	private void init() {
        adapter = new TrailListAdapter(this, null == app.getTrailList() ? new ArrayList<Folder>() : app.getTrailList());
        setListAdapter(adapter);
        adapter.sort();
		hideSpinner(true);
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {	
		if (null != lastSelection) { lastSelection.isSelected = false; }
		Folder trail = adapter.getItem(position);
		trail.isSelected = true;
		lastSelection = trail;
		adapter.selectedItem = position;
		if (null != trail.name && trail.name.length() > 2) { // name might be 'CHP'
			app.selectedTrail = trail.name;
		}
		((BoulderCountyApplication)getApplication()).setCurrentFolder(trail);
		Intent intent = new Intent(BoulderCountyTrailList.this, BoulderCountyMap.class);
        startActivity(intent);
	}

    @Override
    public void onResume() {
        super.onResume();
		hideSpinner(false);
        init();
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
	    return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	public void hideSpinner(boolean hide) {
		((RelativeLayout)findViewById(R.id.refresh)).setVisibility(View.GONE);
		if (hide) {
			((RelativeLayout)findViewById(R.id.spinner)).setVisibility(View.GONE);
		}
		else {
			((RelativeLayout)findViewById(R.id.spinner)).setVisibility(View.VISIBLE);
		}
	}

}