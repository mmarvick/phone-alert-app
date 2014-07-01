package com.mmarvick.urgentcall.settings;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;
import com.mmarvick.urgentcall.widgets.EditTextIntPrompt;
import com.mmarvick.urgentcall.widgets.EditTextStringPrompt;
import com.mmarvick.urgentcall.widgets.OnOptionsChangedListener;
import com.mmarvick.urgentcall.widgets.StateListsPrompt;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.view.View;
import android.view.View.OnClickListener;

public class RCSettingsActivity extends AlertSettingsActivity {
	protected Preference callQty;
	protected Preference callTime;
	protected Preference whoAlerts;
	
	protected void onCreate(Bundle savedInstanceState) {
		xml = R.xml.pref_rc;
		alertType = RulesEntry.RC_STATE;
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void loadPrefs() {
		super.loadPrefs();
		callQty = findPreference(alertType + "_CALL_QTY");
		callTime = findPreference(alertType + "_CALL_TIME");
		whoAlerts = findPreference(alertType + "_FILTER");
		prefs.add(whoAlerts);
		prefs.add(callTime);
		prefs.add(callQty);
	}
	
	protected void startPrefListeners() {
		super.startPrefListeners();
		
		callQty.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				EditTextIntPrompt callNumberPrompt = new EditTextIntPrompt(RCSettingsActivity.this, Constants.CALL_QTY_MIN, Constants.CALL_QTY_MAX,
						Constants.CALL_QTY, Constants.CALL_QTY_DEFAULT, Constants.CALL_QTY_TITLE);
				callNumberPrompt.setOnOptionsChangedListener(new OnOptionsChangedListener() {
					
					@Override
					public void onOptionsChanged() {
						setStates();
						
					}
				});
				
				callNumberPrompt.show();				
				return true;
			}
		});	
		
		callTime.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				EditTextIntPrompt callTimePrompt = new EditTextIntPrompt(RCSettingsActivity.this, Constants.CALL_MIN_MIN, Constants.CALL_MIN_MAX,
						Constants.CALL_MIN, Constants.CALL_MIN_DEFAULT, Constants.CALL_MIN_TITLE);
				callTimePrompt.setOnOptionsChangedListener(new OnOptionsChangedListener() {
					
					@Override
					public void onOptionsChanged() {
						setStates();
						
					}
				});
				
				callTimePrompt.show();
				return true;
			}
		});
		
		whoAlerts.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				StateListsPrompt msgStatePrompt = new StateListsPrompt(RCSettingsActivity.this, alertType,
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
	}
	
	protected void setStates() {
		super.setStates();
		
		callQty.setSummary(PrefHelper.getRepeatedCallQty(RCSettingsActivity.this) + " calls");
		
		callTime.setSummary("" + PrefHelper.getRepeatedCallMins(RCSettingsActivity.this) + " minutes");
		
    	int recentState = PrefHelper.getState(getApplicationContext(), alertType);
    	if (recentState == Constants.URGENT_CALL_STATE_OFF) {
    		recentState = PrefHelper.getBackupState(getApplicationContext(), alertType);
    	}
    	
    	if (recentState == Constants.URGENT_CALL_STATE_WHITELIST) {
    		whoAlerts.setSummary("Allowed callers only");    		
    	} else if (recentState == Constants.URGENT_CALL_STATE_BLACKLIST) {
    		whoAlerts.setSummary("Everyone except blocked callers");    		
    	} else {
    		whoAlerts.setSummary("Everyone");    		
    	} 		
	}
}
