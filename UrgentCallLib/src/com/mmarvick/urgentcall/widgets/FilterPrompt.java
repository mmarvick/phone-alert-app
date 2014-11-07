package com.mmarvick.urgentcall.widgets;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.activities.AlertFragment;
import com.mmarvick.urgentcall.activities.ContactListFragment;
import com.mmarvick.urgentcall.data.Alert;
import com.mmarvick.urgentcall.data.DbContract;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;

public class FilterPrompt {
	private final Context mContext;
	private final Fragment mFragment;
	private final Alert alert;
	
	private RadioButton onRadio;
	private RadioButton whitelistRadio;
	private RadioButton blacklistRadio;;
	private Button whitelistButton;
	private Button blacklistButton;
	
	private AlertDialog.Builder alertDialogBuilder;
	private OnOptionsChangedListener mOnOptionsChangedListener;
	

	public FilterPrompt(final Context context, final Fragment fragment, final Alert alert, final String title) {
		this.mContext = context;
		this.mFragment = fragment;
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
			        showListDialog(DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY);
				}
			});
			
			blacklistButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
			        showListDialog(DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED);
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
						alertListIsEmpty(true);
					} else if (blacklistRadio.isChecked() && alert.getBlockedContacts().isEmpty()) {
						alertListIsEmpty(false);
					} else {
						if (onRadio.isChecked()) {
							alert.setFilterBy(DbContract.ENTRY_FILTER_BY_EVERYONE);
						} else if (whitelistRadio.isChecked()) {
							alert.setFilterBy(DbContract.ENTRY_FILTER_BY_ALLOWED_ONLY);
						} else if (blacklistRadio.isChecked()) {
							alert.setFilterBy(DbContract.ENTRY_FILTER_BY_BLOCKED_IGNORED);
						} 
						
					}
					
					if (mOnOptionsChangedListener != null) mOnOptionsChangedListener.onOptionsChanged();
				}
			});
		}
		
	}
	
	public void show() {
		if (mContext.getResources().getBoolean(R.bool.paid_version)) {
			alertDialogBuilder.create().show();
		} else {
			upgradeNote();
		}
	}
	
	public void alertListIsEmpty(boolean allowList) {
		alert.setFilterBy(DbContract.ENTRY_FILTER_BY_EVERYONE);
		
		String message = "You need to add some people to the blocked contacts " +
				"list if you only want to prevent blocked contacts from coming through!";
		
		if (allowList) {
			message = "You need to add some people to the allowed contacts list " +
					"if you only want to let allowed callers through!";
		}
		
		new AlertDialog.Builder(mContext)
		.setMessage(message)
		.setPositiveButton("OK", null)
		.create().show();	
	}
	
	public void showListDialog(int listType) {
		FragmentManager manager = ((ActionBarActivity) mContext).getSupportFragmentManager();
		
		ContactListFragment dialog = new ContactListFragment(alert, listType, (AlertFragment) mFragment);

		dialog.show(manager, "dialog");
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
		UpgradeDialog.upgradeDialog(mContext, mContext.getString(R.string.upgrade_body_filter_by));
	}
	
	public void setOnOptionsChangedListener(OnOptionsChangedListener listener) {
		mOnOptionsChangedListener = listener;
	}

}
