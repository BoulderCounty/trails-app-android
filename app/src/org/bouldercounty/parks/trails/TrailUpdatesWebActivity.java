package org.bouldercounty.parks.trails;


import org.bouldercounty.parks.trails.R;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class TrailUpdatesWebActivity extends Activity {

	public static String tag = "TrailUpdatesWebActivity";

	WebView webview;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.twitter_updates);

        setupViews();
     }


	private void setupViews() {
		if (null != (ImageView)findViewById(R.id.refresh_image)) {
			((ImageView)findViewById(R.id.refresh_image)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					TrailUpdatesWebActivity.this.hideSpinner(false);
					reloadPage();
				}
			});
		}

		((TextView)findViewById(R.id.title)).setText(
				new StringBuilder(getResources().getString(R.string.twitter_header)).toString()
				);
		((WebView)findViewById(R.id.webView1)).getSettings().setJavaScriptEnabled(true);
        //Add a client to the view 
		((WebView)findViewById(R.id.webView1)).setWebViewClient(mClient);
		((WebView)findViewById(R.id.webView1)).loadUrl("http://yourwebsite.com/trailconditions.html");

		hideSpinner(true);		
	}


	@Override
	protected void onResume() {
		super.onResume();
		TrailUpdatesWebActivity.this.hideSpinner(false);
		setupViews();
	}

	public void hideSpinner(boolean hide) {
		((RelativeLayout)findViewById(R.id.refresh)).setVisibility(View.VISIBLE);
		if (hide) {
			((RelativeLayout)findViewById(R.id.spinner)).setVisibility(View.GONE);
		}
		else {
			((RelativeLayout)findViewById(R.id.spinner)).setVisibility(View.VISIBLE);
		}
	}

	private void reloadPage() {
		((WebView)findViewById(R.id.webView1)).reload();
	}

    private WebViewClient mClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Uri request = Uri.parse("http://yourwebsite.com/trailconditions.html");

            return false;
        }

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			TrailUpdatesWebActivity.this.hideSpinner(true);
		}

    };

}