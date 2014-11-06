package com.mmarvick.urgentcall.widgets;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.activities.ContactListActivity;
import com.mmarvick.urgentcall.data.Alert;
import com.mmarvick.urgentcall.data.DbContract;
import com.mmarvick.urgentcall.data.OldPrefHelper;
import com.mmarvick.urgentcall.data.OldDbContractDatabase.RulesEntryOld;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class FilterPrompt {
	private final Context context;
	private final Alert alert;
	
	private RadioButton onRadio;
	private RadioButton whitelistRadio;
	private RadioButton blacklistRadio;;
	private Button whitelistButton;
	private Button blacklistButton;
	
	private AlertDialog.Builder alertDialogBuilder;
	private OnOptionsChangedListener mOnOptionsChangedListener;
	

	public FilterPrompt(final Context context, final Alert alert, final String title) {
		this.context = context;
		this.alert = alert;
		
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
			        //TODO
				}
			});
			
			blacklistButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
			        //TODO
				}
			});
			
			onRadio.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					enableDisableButtons();
				}
			});
			
			whitelistRadio.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					enableDisableButtons();
				}
			});
			
			blacklistRadio.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					enableDisableButtons();
				}
			});
			
			select();
			
			alertDialogBuilder
			.setTitle(title)
			.setPositiveButton(context.getString(R.string.state_change_dialog_ok), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (whitelistRadio.isChecked() && alert.getAllowedContacts().isEmpty()) {
						
					} else if (blacklistRadio.isChecked() && alert.getBlockedContacts().isEmpty()) {
						
					} else {
						if (onRadio.isChecked()) {
							alert.setFilterBy(DbContract.ENTRY_FILTER_BY_EVERYONE);
						} else if (whitelistRadio.isChecked()) {
							alert.setFilterBy(DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY);
						} else if (blacklistRadio.isChecked()) {
							alert.setFilterBy(DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED);
						} 
						
						if (mOnOptionsChangedListener != null) mOnOptionsChangedListener.onOptionsChanged();
					}
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
		int filterBy = alert.getFilterBy();
		switch (filterBy) {
		case DbContract.ENTRY_FILTER_BY_EVERYONE:
			onRadio.setChecked(true);
			break;
		case DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY:
			whitelistRadio.setChecked(true);			
			break;
		case DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED:
			blacklistRadio.setChecked(true);			
			break;
		}
		
		enableDisableButtons();
		
	}
	
	private void enableDisableButtons() {
		if (whitelistRadio.isChecked()) {
			whitelistButton.setVisibility(View.VISIBLE);
			blacklistButton.setVisibility(View.GONE);			
		} else if (blacklistRadio.isChecked()) {
			whitelistButton.setVisibility(View.GONE);
			blacklistButton.setVisibility(View.VISIBLE);			
		} else {
			whitelistButton.setVisibility(View.GONE);
			blacklistButton.setVisibility(View.GONE);
		}	
	}
	
	private void upgradeNote() {
		UpgradeDialog.upgradeDialog(context, context.getString(R.string.upgrade_body_state));
	}
	
	public void setOnOptionsChangedListener(OnOptionsChangedListener listener) {
		mOnOptionsChangedListener = listener;
	}

}
