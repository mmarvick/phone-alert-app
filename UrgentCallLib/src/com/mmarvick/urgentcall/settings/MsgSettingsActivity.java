package com.mmarvick.urgentcall.settings;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.activities.ContactListActivity;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbContract;
import com.mmarvick.urgentcall.data.RulesDbHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;
import com.mmarvick.urgentcall.widgets.EditTextStringPrompt;
import com.mmarvick.urgentcall.widgets.OnOptionsChangedListener;
import com.mmarvick.urgentcall.widgets.StateListsPrompt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class MsgSettingsActivity extends PreferenceActivity {

	private PreferenceScreen prefScreen;
	private Preference msgState;
	private Preference keyword;
	private Preference whoAlerts;
	private Preference whoList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_msg);
		
		prefScreen = getPreferenceScreen();
		
		msgState = findPreference("MSG_STATUS");
		keyword = findPreference("MSG_KEY");
		whoAlerts = findPreference("MSG_FILTER");
		whoList = findPreference("MSG_FILTER_USERS");
		
		msgState.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_OFF) {
					int backupState = PrefHelper.getBackupState(getApplicationContext(), RulesEntry.MSG_STATE);
					PrefHelper.setState(getApplicationContext(), RulesEntry.MSG_STATE, backupState);
				} else {
					PrefHelper.saveBackupState(getApplicationContext(), RulesEntry.MSG_STATE);
					PrefHelper.setState(getApplicationContext(), RulesEntry.MSG_STATE, Constants.URGENT_CALL_STATE_OFF);
				}
				setStates();
				return true;
			}
		});
		
		keyword.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				EditTextStringPrompt msgKeyPrompt = new EditTextStringPrompt(MsgSettingsActivity.this, Constants.MSG_MESSAGE_MIN,
						Constants.MSG_MESSAGE, Constants.MSG_MESSAGE_DEFAULT, Constants.MSG_MESSAGE_TITLE);
				msgKeyPrompt.setOnOptionsChangedListener(new OnOptionsChangedListener() {
					
					@Override
					public void onOptionsChanged() {
						setStates();
						
					}
				});
				
				msgKeyPrompt.show();
				return true;
			}
		});
		
		whoAlerts.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				StateListsPrompt msgStatePrompt = new StateListsPrompt(MsgSettingsActivity.this, RulesEntry.MSG_STATE,
						getApplicationContext().getString(R.string.state_change_dialog_title_msg), false);
				msgStatePrompt.setOnOptionsChangedListener(new OnOptionsChangedListener() {
						
						@Override
						public void onOptionsChanged() {
							setStates();
							
						}
					});
				msgStatePrompt.show();
				return true;
			}
		});
		
		whoList.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
		        Intent listIntent = new Intent(getApplicationContext(), ContactListActivity.class);
		        listIntent.putExtra(Constants.ALERT_TYPE, RulesEntry.MSG_STATE);
		        if (PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_WHITELIST) {
		        	listIntent.putExtra(Constants.USER_STATE, RulesEntry.STATE_ON);
		        } else {
		        	listIntent.putExtra(Constants.USER_STATE, RulesEntry.STATE_OFF);
		        }
		        startActivity(listIntent);
				return true;
			}
		});
		
	}
	
    @Override
    public void onResume() {
	     setStates();
	     super.onResume();
    }
    
    private void setStates() {
    	if (PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_OFF) {
    		msgState.setSummary("Off");
    		keyword.setEnabled(false);
    	} else {
    		msgState.setSummary("On");
    		keyword.setEnabled(true);
    	}
    	
    	keyword.setSummary(PrefHelper.getMessageToken(getApplicationContext()));
    	
    	if (PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_ON) {
    		setWhoTitles(Constants.URGENT_CALL_STATE_ON);
    		whoAlerts.setEnabled(true);
    		whoList.setEnabled(false);
    	} else if (PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_WHITELIST) {
    		setWhoTitles(Constants.URGENT_CALL_STATE_WHITELIST);
    		whoAlerts.setEnabled(true);
    		whoList.setEnabled(true);
    	} else if (PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_BLACKLIST) {
    		setWhoTitles(Constants.URGENT_CALL_STATE_BLACKLIST);
    		whoAlerts.setEnabled(true);
    		whoList.setEnabled(true);    		
    	} else {
    		setWhoTitles(PrefHelper.getBackupState(getApplicationContext(), RulesEntry.MSG_STATE));
    		whoAlerts.setEnabled(false);
    		whoList.setEnabled(false);      		
    	}
    	
    }
    
    private void setWhoTitles(int state) {
    	if (state == Constants.URGENT_CALL_STATE_WHITELIST) {
    		whoAlerts.setSummary("Allowed users only");
    		whoList.setTitle("Allow List");    		
    	} else if (state == Constants.URGENT_CALL_STATE_BLACKLIST) {
    		whoAlerts.setSummary("Everyone but blocked users");
    		whoList.setTitle("Block List");    		
    	} else {
    		whoAlerts.setSummary("All users");
    		whoList.setTitle("Allow / Block List");    		
    	}
    }
    
}
