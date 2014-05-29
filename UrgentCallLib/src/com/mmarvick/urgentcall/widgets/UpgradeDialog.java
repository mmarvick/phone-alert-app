package com.mmarvick.urgentcall.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class UpgradeDialog {
	public static void upgradeDialog(final Context context, String messageText) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder
			.setTitle("Pro Version Only")
			.setMessage(messageText + 
					"\n\nWould you like to upgrade?")
			.setCancelable(true)
			.setNeutralButton("No Thanks", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			})
			.setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					String packageName = "com.mmarvick.urgentcall_pro";
					try {
					    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
					} catch (android.content.ActivityNotFoundException anfe) {
					    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
					}
				}
			});
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		
		
	}
}
