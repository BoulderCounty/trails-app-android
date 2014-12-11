package org.bouldercounty.parks.trails;


import org.bouldercounty.parks.trails.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;


public class AboutBoulderCountyActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_bcpt);

		setupViews();
	}

	private void setupViews() {
		((Button)findViewById(R.id.feedback_btn)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "email@domain.org" });
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Trails Android App");
				AboutBoulderCountyActivity.this.startActivity(Intent.createChooser(emailIntent, "eMail using:"));
			}
		});
	}

}