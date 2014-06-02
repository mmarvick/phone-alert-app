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

public class StateListsPrompt {
	private RadioButton onRadio;
	private RadioButton whitelistRadio;
	private RadioButton blacklistRadio;;
	private Button whitelistButton;
	private Button blacklistButton;
	private final Context context;
	private final String alertType;
	
	private AlertDialog.Builder alertDialogBuilder;
	private OnOptionsChangedListener mOnOptionsChangedListener;

	public StateListsPrompt(final Context context, final String alertType, String title) {
		this.context = context;
		this.alertType = alertType;
		
		if (context.getResources().getBoolean(R.bool.paid_version)) {
			LayoutInflater li = LayoutInflater.from(context);
			View promptView = li.inflate(R.layout.dialog_state_lists,  null);	
			
			alertDialogBuilder = new AlertDialog.Builder(context);
			alertDialogBuilder.setView(promptView);
			

			
			onRadio = (RadioButton) promptView.findViewById(R.id.selection_on);
			whitelistRadio = (RadioButton) promptView.findViewById(R.id.selection_whitelist);
			blacklistRadio = (RadioButton) promptView.findViewById(R.id.selection_blacklist);
			whitelistButton = (Button) promptView.findViewById(R.id.button_whitelist);
			blacklistButton = (Button) promptView.findViewById(R.id.button_blacklist);
	
			whitelistButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
			        Intent whitelistIntent = new Intent(context, ContactListActivity.class);
			        whitelistIntent.putExtra(Constants.ALERT_TYPE, alertType);
			        whitelistIntent.putExtra(Constants.USER_STATE, RulesEntry.STATE_ON);
					context.startActivity(whitelistIntent);
				}
			});
			
			blacklistButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
			        Intent blacklistIntent = new Intent(context, ContactListActivity.class);
			        blacklistIntent.putExtra(Constants.ALERT_TYPE, alertType);
			        blacklistIntent.putExtra(Constants.USER_STATE, RulesEntry.STATE_OFF);
					context.startActivity(blacklistIntent);
				}
			});
			
			/*if (!context.getResources().getBoolean(R.bool.paid_version)) {
				whitelistButton.setEnabled(false);
				blacklistButton.setEnabled(false);
				
				whitelistRadio.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						upgradeNote();
					}
				});
				
				blacklistRadio.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						upgradeNote();
					}
				});
			} else { */
				onRadio.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						whitelistButton.setVisibility(View.GONE);
						blacklistButton.setVisibility(View.GONE);
						
					}
				});
				
				whitelistRadio.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						whitelistButton.setVisibility(View.VISIBLE);
						blacklistButton.setVisibility(View.GONE);			
						
					}
				});
				
				blacklistRadio.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						whitelistButton.setVisibility(View.GONE);
						blacklistButton.setVisibility(View.VISIBLE);
						
					}
				});
			//}
			
			select();
			
			alertDialogBuilder
			.setTitle(title)
			.setPositiveButton(context.getString(R.string.state_change_dialog_ok), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (onRadio.isChecked()) {
						PrefHelper.setState(context, alertType, Constants.URGENT_CALL_STATE_ON);
					} else if (whitelistRadio.isChecked()) {
						PrefHelper.setState(context, alertType, Constants.URGENT_CALL_STATE_WHITELIST);
					} else if (blacklistRadio.isChecked()) {
						PrefHelper.setState(context, alertType, Constants.URGENT_CALL_STATE_BLACKLIST);
					} 
					
					if (mOnOptionsChangedListener != null) mOnOptionsChangedListener.onOptionsChanged();
				}
			});
		}
		
	}
	
	public void show() {
		if (context.getResources().getBoolean(R.bool.paid_version)) {
			alertDialogBuilder.create().show();
		} else {
			upgradeNote();
		}
	}
	
	private void select() {
		int state = PrefHelper.getState(context, alertType);
		switch (state) {
		case Constants.URGENT_CALL_STATE_ON:
			onRadio.setChecked(true);
			whitelistButton.setVisibility(View.GONE);
			blacklistButton.setVisibility(View.GONE);
			break;
		case Constants.URGENT_CALL_STATE_WHITELIST:
			whitelistRadio.setChecked(true);
			whitelistButton.setVisibility(View.VISIBLE);
			blacklistButton.setVisibility(View.GONE);			
			break;
		case Constants.URGENT_CALL_STATE_BLACKLIST:
			blacklistRadio.setChecked(true);
			whitelistButton.setVisibility(View.GONE);
			blacklistButton.setVisibility(View.VISIBLE);			
			break;
		}
	}
	
	private void upgradeNote() {
		UpgradeDialog.upgradeDialog(context, context.getString(R.string.upgrade_body_state));
		//select();
	}
	
	public void setOnOptionsChangedListener(OnOptionsChangedListener listener) {
		mOnOptionsChangedListener = listener;
	}

}
