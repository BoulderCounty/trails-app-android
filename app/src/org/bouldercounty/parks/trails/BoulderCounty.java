package org.bouldercounty.parks.trails;

import org.bouldercounty.parks.trails.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;


public class BoulderCounty extends Activity {

	public static String tag = "BoulderCounty";

    private BoulderCountyApplication app;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        app = (BoulderCountyApplication)getApplication();
        setupViews();
    }

	private void setupViews() {
		((ImageButton)findViewById(R.id.map_trails)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mapTrails();
			}
		});

		((ImageButton)findViewById(R.id.list_trails)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listTrails();
			}
		});

		((ImageView)findViewById(R.id.info)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(BoulderCounty.this, AboutBoulderCountyActivity.class));
			}
		});

		((ImageButton)findViewById(R.id.trail_updates)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(BoulderCounty.this, TrailUpdatesWebActivity.class));
			}
		});
	}

	private void mapTrails() {
		Intent intent = new Intent(BoulderCounty.this, BoulderCountyMap.class);
		// map all if no currentFolder!
		app.currentFolder = null;
		startActivity(intent);
	}

	private void listTrails() {
		Intent intent = new Intent(BoulderCounty.this, BoulderCountyTrailList.class);
		startActivity(intent);
	}

    @Override
    public void onResume() {
        super.onResume();
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
	    return true;
	}

}