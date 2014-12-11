package org.bouldercounty.parks.trails.utilities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class Constants {

	public static String MAP_POINTS_CONSTANT = "ALL";
	public static String MAP_TRAIL_CONSTANT = "SELECTION";

	public static String MAP_TYPE_DESCRIPTION = "MAPTYPE";
	public static int MAP_TYPE_REQUEST_CODE = 5;
	public static int MAP_TYPE_PREVIOUS = 0;
	public static int MAP_TYPE_NORMAL = 1;
	public static int MAP_TYPE_SATELLITE = 2;

    public static void showAlert(Context context, String alertTitle, String alertMessage) 
    {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(alertMessage)
		       .setCancelable(false)
		       .setNegativeButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
//		builder.setTitle(alertTitle);
		AlertDialog alert = builder.create();
		alert.show();

    }

}