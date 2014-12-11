package org.bouldercounty.parks.trails;


import org.bouldercounty.parks.trails.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;


public class BoulderCountySplashScreen extends Activity {

	private long ms=0;
//	private long splashTime=2000;
	private boolean splashActive = true;
	private boolean paused=false;
	BoulderCountyApplication app;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        app = (BoulderCountyApplication)getApplication();

		setContentView(R.layout.splash);
		Thread mythread = new Thread() {
			public void run() {
				try {
//					while (splashActive && ms < splashTime) 
					while (splashActive && null == app.getTrailList()) 
					{
						if(!paused)
							ms=ms+100;
						sleep(100);
					}
				} catch(Exception e) {
					Log.e("SplashScreen", e.getMessage());
				}
				finally {
					Intent intent = new Intent(BoulderCountySplashScreen.this, BoulderCounty.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
			}
		};
		mythread.start(); 
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