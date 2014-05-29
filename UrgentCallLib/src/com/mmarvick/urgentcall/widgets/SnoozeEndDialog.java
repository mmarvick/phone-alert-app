package com.mmarvick.urgentcall.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;

public class SnoozeEndDialog {
	private AlertDialog dialog;
	
	public SnoozeEndDialog(final Context context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		
		alertDialogBuilder
			.setTitle(context.getString(R.string.cancel_snooze_dialog_title))
			.setMessage(context.getString(R.string.cancel_snooze_dialog_body))
			.setCancelable(true)
			.setPositiveButton(context.getString(R.string.cancel_snooze_dialog_yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					PrefHelper.setSnoozeTime(context, 0);
				}
			})
			.setNeutralButton(context.getString(R.string.cancel_snooze_dialog_no), new DialogInterface.OnClickListener() {

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
