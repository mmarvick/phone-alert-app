package com.mmarvick.urgentcall.settings;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbContract;
import com.mmarvick.urgentcall.data.RulesDbHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
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
				if (PrefHelper.getState(getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF) {
		    		PrefHelper.setState(getApplicationContext(), Constants.APP_STATE, Constants.URGENT_CALL_STATE_ON);
		    	} else {
		    		PrefHelper.setState(getApplicationContext(), Constants.APP_STATE, Constants.URGENT_CALL_STATE_OFF);
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
	}
	
    @Override
    public void onResume() {
     setStates();
     super.onResume();
    }
    
    private void setStates() {
    	if (PrefHelper.getState(getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF) {
    		ucState.setSummary("All Alerts Off");
    	} else if (PrefHelper.isSnoozing(getApplicationContext())) {
    		ucState.setSummary("All Alerts Snoozing");
    	} else {
    		if ((PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_OFF)
    				&& (PrefHelper.getState(getApplicationContext(), RulesEntry.RC_STATE) == Constants.URGENT_CALL_STATE_OFF)
    				&& (PrefHelper.getState(getApplicationContext(), RulesEntry.SC_STATE) == Constants.URGENT_CALL_STATE_OFF)) {
    			ucState.setSummary("All Alerts Off");
    		} else if ((PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_OFF)
    				|| (PrefHelper.getState(getApplicationContext(), RulesEntry.RC_STATE) == Constants.URGENT_CALL_STATE_OFF)
    				|| (PrefHelper.getState(getApplicationContext(), RulesEntry.SC_STATE) == Constants.URGENT_CALL_STATE_OFF)) {
    			ucState.setSummary("Some Alerts On");
    		} else {
    			ucState.setSummary("All Alerts On");
    		}
    	}
    	
    	setStateItem(msgAlert, RulesEntry.MSG_STATE);
    	setStateItem(rcAlert, RulesEntry.RC_STATE);
    	setStateItem(scAlert, RulesEntry.SC_STATE);
    }
    
    private void setStateItem(Preference pref, String prefName) {
    	if (PrefHelper.getState(getApplicationContext(), Constants.APP_STATE) == Constants.URGENT_CALL_STATE_OFF
    			|| PrefHelper.isSnoozing(getApplicationContext())) {
    		pref.setEnabled(false);
    		pref.setSummary("");
    	} else {
    		pref.setEnabled(true);
    		
			if (PrefHelper.getState(getApplicationContext(), prefName) == Constants.URGENT_CALL_STATE_OFF) {
				pref.setSummary("Off");
			} else {
				pref.setSummary("On");
			}
    	}
    }
   
}
