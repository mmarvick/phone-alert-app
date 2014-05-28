package com.mmarvick.urgentcall;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class StatePrompt {
	private final RadioButton onRadio;
	private final RadioButton whitelistRadio;
	private final RadioButton blacklistRadio;
	private final RadioButton offRadio;
	private final Button whitelistButton;
	private final Button blacklistButton;
	private final Context context;
	
	public StatePrompt(final Context context) {
		LayoutInflater li = LayoutInflater.from(context);
		View promptView = li.inflate(R.layout.state_selector_prompt,  null);	
		
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setView(promptView);
		
		this.context = context;
		
		onRadio = (RadioButton) promptView.findViewById(R.id.selection_on);
		whitelistRadio = (RadioButton) promptView.findViewById(R.id.selection_whitelist);
		blacklistRadio = (RadioButton) promptView.findViewById(R.id.selection_blacklist);
		offRadio = (RadioButton) promptView.findViewById(R.id.selection_off);
		whitelistButton = (Button) promptView.findViewById(R.id.button_whitelist);
		blacklistButton = (Button) promptView.findViewById(R.id.button_blacklist);
		
		if (!context.getResources().getBoolean(R.bool.paid_version)) {
			whitelistButton.setEnabled(false);
			blacklistButton.setEnabled(false);
			
			whitelistRadio.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					UpgradeDialog.upgradeDialog(context, "");
					select();
				}
			});
			
			blacklistRadio.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					UpgradeDialog.upgradeDialog(context, "");
					select();
				}
			});
		}
		
		select();
		
		alertDialogBuilder
		.setTitle("Select Urgent Call state")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (onRadio.isChecked()) {
					PrefHelper.setState(context, Constants.SIMPLE_STATE_ON);
				} else if (whitelistRadio.isChecked()) {
					PrefHelper.setState(context, Constants.SIMPLE_STATE_WHITELIST);
				} else if (blacklistRadio.isChecked()) {
					PrefHelper.setState(context, Constants.SIMPLE_STATE_BLACKLIST);
				} else if (offRadio.isChecked()) {
					PrefHelper.setState(context, Constants.SIMPLE_STATE_OFF);
				}

			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
	
		AlertDialog dialog = alertDialogBuilder.create();
		dialog.show();
	}
	
	private void select() {
		int state = PrefHelper.getState(context);
		Log.e("State:", "" + state);
		switch (state) {
		case Constants.SIMPLE_STATE_ON:
			Log.e("This evaluates...","Okay!");
			onRadio.setChecked(true);
			break;
		case Constants.SIMPLE_STATE_OFF:
			offRadio.setChecked(true);
			break;
		case Constants.SIMPLE_STATE_WHITELIST:
			whitelistRadio.setChecked(true);
			break;
		case Constants.SIMPLE_STATE_BLACKLIST:
			blacklistRadio.setChecked(true);
			break;
		}
	}

}
