package com.mmarvick.urgentcall.widgets;

import com.mmarvick.urgentcall.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class UpgradeDialog {
	public static void upgradeDialog(final Context context, String messageText) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder
			.setTitle(context.getString(R.string.upgrade_title))
			.setMessage(messageText + "\n\n" + context.getString(R.string.upgrade_body_general))
			.setCancelable(true)
			.setNeutralButton("No Thanks", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			})
			.setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					String packageName = context.getString(R.string.package_pro);
					Uri uri;
					try {
						uri = Uri.parse(context.getString(R.string.url_play_app) + packageName);
					} catch (android.content.ActivityNotFoundException anfe) {
						uri = Uri.parse(context.getString(R.string.url_play_web) + packageName);
					}
					
					context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
				}
			});
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}
}
