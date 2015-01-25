package com.mmarvick.urgentcall.widgets;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.helpers.StoreHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class UpgradeDialog {
	public static void upgradeDialog(final Context context, String messageText) {
		upgradeDialog(context, messageText, context.getString(R.string.upgrade_title));
	}
	
	public static void upgradeDialog(final Context context, String messageText, String title) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		alertDialogBuilder
			.setTitle(title)
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
                    StoreHelper.goToStore(packageName, context);
				}
			});
		
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}
}
