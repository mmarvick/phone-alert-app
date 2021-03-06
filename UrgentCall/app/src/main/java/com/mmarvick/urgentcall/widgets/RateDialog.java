package com.mmarvick.urgentcall.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.helpers.StoreHelper;

public class RateDialog {
	private AlertDialog dialog;
	
	public RateDialog(final Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		
		alertDialogBuilder
			.setTitle(context.getString(R.string.rate_dialog_title))
			.setMessage(context.getString(R.string.rate_dialog_body))
			.setCancelable(true)
			.setPositiveButton(context.getString(R.string.rate_dialog_yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String packageName;
					if (context.getResources().getBoolean(R.bool.paid_version)) {
						packageName = context.getString(R.string.package_pro);
					} else {
						packageName = context.getString(R.string.package_lite);
					}

                    StoreHelper.goToStore(packageName, context);
				}
			})
			.setNeutralButton(context.getString(R.string.rate_dialog_no), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
				
			});
		
		dialog = alertDialogBuilder.create();
	}

	public void show() {
		dialog.show();
	}
	
	public void cancel() {
		dialog.cancel();
	}
	
	public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
		dialog.setOnDismissListener(listener);;
	}
	
}
