package com.mmarvick.urgentcall.widgets;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

public class StateOnOffPrompt {
	private final RadioButton onRadio;
	private final RadioButton offRadio;
	private final Context context;
	private final String alertType;
	
	private AlertDialog dialog;
	
	public StateOnOffPrompt(final Context context, final String alertType) {
		LayoutInflater li = LayoutInflater.from(context);
		View promptView = li.inflate(R.layout.dialog_state_on_off,  null);	
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(promptView);
		
		this.context = context;
		this.alertType = alertType;
		
		onRadio = (RadioButton) promptView.findViewById(R.id.selection_binary_on);
		offRadio = (RadioButton) promptView.findViewById(R.id.selection_binary_off);
		
		select();
		
		alertDialogBuilder
		.setTitle(context.getString(R.string.state_change_dialog_title))
		.setPositiveButton(context.getString(R.string.state_change_dialog_ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (onRadio.isChecked()) {
					PrefHelper.setState(context, alertType, Constants.URGENT_CALL_STATE_ON);
				} else if (offRadio.isChecked()) {
					PrefHelper.setState(context, alertType, Constants.URGENT_CALL_STATE_OFF);
				}

			}
		})
		.setNegativeButton(context.getString(R.string.state_change_dialog_cancel), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	
		dialog = alertDialogBuilder.create();
	}
	
	public void show() {
		dialog.show();
	}
	
	private void select() {
		int state = PrefHelper.getState(context, alertType);
		switch (state) {
		case Constants.URGENT_CALL_STATE_ON:
			onRadio.setChecked(true);
			break;
		case Constants.URGENT_CALL_STATE_OFF:
			offRadio.setChecked(true);
			break;
		}
	}

}
