package com.mmarvick.urgentcall.settings;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.OldPrefHelper;
import com.mmarvick.urgentcall.data.OldDbContractDatabase.RulesEntryOld;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity {

	private PreferenceScreen prefScreen;
	private Preference ucState;
	private Preference msgAlert;
	private Preference rcAlert;
	private Preference scAlert;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_main);
		
		prefScreen = getPreferenceScreen();
		
		ucState = findPreference("STATUS");
		msgAlert = findPreference("MSG_ALERT");
		rcAlert = findPreference("RC_ALERT");
		scAlert = findPreference("SC_ALERT");
		
		ucState.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (OldPrefHelper.getState(getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF) {
		    		OldPrefHelper.setState(getApplicationContext(), Constants.APP_STATE, Constants.URGENT_CALL_STATE_ON);
		    	} else {
		    		OldPrefHelper.setState(getApplicationContext(), Constants.APP_STATE, Constants.URGENT_CALL_STATE_OFF);
		    	}
				
				setStates();
				return false;
			}
		});
		
		msgAlert.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(getApplicationContext(), MsgSettingsActivity.class));
				return false;
			}
		});
		
		rcAlert.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(getApplicationContext(), RCSettingsActivity.class));
				return false;
			}
		});			
		
		scAlert.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(getApplicationContext(), SCSettingsActivity.class));
				return false;
			}
		});		
	}
	
    @Override
    public void onResume() {
     setStates();
     super.onResume();
    }
    
    private void setStates() {
    	if (OldPrefHelper.getState(getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF) {
    		ucState.setSummary("All Alerts Off");
    	} else if (OldPrefHelper.isSnoozing(getApplicationContext())) {
    		ucState.setSummary("All Alerts Snoozing");
    	} else {
    		if ((OldPrefHelper.getState(getApplicationContext(), RulesEntryOld.MSG_STATE) == Constants.URGENT_CALL_STATE_OFF)
    				&& (OldPrefHelper.getState(getApplicationContext(), RulesEntryOld.RC_STATE) == Constants.URGENT_CALL_STATE_OFF)
    				&& (OldPrefHelper.getState(getApplicationContext(), RulesEntryOld.SC_STATE) == Constants.URGENT_CALL_STATE_OFF)) {
    			ucState.setSummary("All Alerts Off");
    		} else if ((OldPrefHelper.getState(getApplicationContext(), RulesEntryOld.MSG_STATE) == Constants.URGENT_CALL_STATE_OFF)
    				|| (OldPrefHelper.getState(getApplicationContext(), RulesEntryOld.RC_STATE) == Constants.URGENT_CALL_STATE_OFF)
    				|| (OldPrefHelper.getState(getApplicationContext(), RulesEntryOld.SC_STATE) == Constants.URGENT_CALL_STATE_OFF)) {
    			ucState.setSummary("Some Alerts On");
    		} else {
    			ucState.setSummary("All Alerts On");
    		}
    	}
    	
    	setStateItem(msgAlert, RulesEntryOld.MSG_STATE);
    	setStateItem(rcAlert, RulesEntryOld.RC_STATE);
    	setStateItem(scAlert, RulesEntryOld.SC_STATE);
    }
    
    private void setStateItem(Preference pref, String prefName) {
    	if (OldPrefHelper.getState(getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF
    			|| OldPrefHelper.isSnoozing(getApplicationContext())) {
    		pref.setEnabled(false);
    		pref.setSummary("");
    	} else {
    		pref.setEnabled(true);
    		
			if (OldPrefHelper.getState(getApplicationContext(), prefName) == Constants.URGENT_CALL_STATE_OFF) {
				pref.setSummary("Off");
			} else {
				pref.setSummary("On");
			}
    	}
    }
   
}
