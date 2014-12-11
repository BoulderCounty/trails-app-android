package org.bouldercounty.parks.trails.data;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;


public class TrailListItem extends RelativeLayout {


	public static String tag = "TrailListItem";

	private Folder trail;


	public TrailListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		System.out.println();
	}

	public Folder getTrail() {
		return this.trail;
	}

}