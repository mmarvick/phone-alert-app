package com.mmarvick.urgentcall.settings;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;
import com.mmarvick.urgentcall.widgets.EditTextStringPrompt;
import com.mmarvick.urgentcall.widgets.OnOptionsChangedListener;
import com.mmarvick.urgentcall.widgets.StateListsPrompt;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

public class MsgSettingsActivity extends AlertSettingsActivity {
	protected Preference keyword;
	protected Preference whoAlerts;
	
	protected void onCreate(Bundle savedInstanceState) {
		xml = R.xml.pref_msg;
		alertType = RulesEntry.MSG_STATE;
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void loadPrefs() {
		super.loadPrefs();
		keyword = findPreference(alertType + "_KEY");
		whoAlerts = findPreference(alertType + "_FILTER");
		prefs.add(keyword);
		prefs.add(whoAlerts);
	}
	
	protected void startPrefListeners() {
		super.startPrefListeners();
		
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
	}
	
	protected void setStates() {
		super.setStates();
		keyword.setSummary(PrefHelper.getMessageToken(getApplicationContext()));
		
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
