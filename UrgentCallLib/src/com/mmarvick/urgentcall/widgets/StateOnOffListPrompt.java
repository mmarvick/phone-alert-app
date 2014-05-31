package com.mmarvick.urgentcall.widgets;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.activities.ContactListActivity;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class StateOnOffListPrompt {
	private final RadioButton whitelistRadio;
	private final RadioButton offRadio;
	private final Button whitelistButton;
	private final Context context;
	private final String alertType;
	
	private AlertDialog dialog;
	
	public StateOnOffListPrompt(final Context context, final String alertType) {
		LayoutInflater li = LayoutInflater.from(context);
		View promptView = li.inflate(R.layout.dialog_state_on_off_list,  null);	
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(promptView);
		
		this.context = context;
		this.alertType = alertType;
		
		whitelistRadio = (RadioButton) promptView.findViewById(R.id.selection_binary_list_whitelist);
		offRadio = (RadioButton) promptView.findViewById(R.id.selection_binary_list_off);
		whitelistButton = (Button) promptView.findViewById(R.id.button_binary_list_whitelist);

		whitelistButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
		        Intent whitelistIntent = new Intent(context, ContactListActivity.class);
		        whitelistIntent.putExtra(Constants.ALERT_TYPE, alertType);
		        whitelistIntent.putExtra(Constants.USER_STATE, RulesEntry.STATE_ON);
				context.startActivity(whitelistIntent);
			}
		});
		
		select();
		
		alertDialogBuilder
		.setTitle(context.getString(R.string.state_change_dialog_title))
		.setPositiveButton(context.getString(R.string.state_change_dialog_ok), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (whitelistRadio.isChecked()) {
					PrefHelper.setState(context, alertType, Constants.URGENT_CALL_STATE_WHITELIST);
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
		case Constants.URGENT_CALL_STATE_OFF:
			offRadio.setChecked(true);
			break;
		case Constants.URGENT_CALL_STATE_WHITELIST:
			whitelistRadio.setChecked(true);
			break;
		}
	}

}
