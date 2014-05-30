package com.mmarvick.urgentcall.activities;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;
import com.mmarvick.urgentcall.widgets.EditTextIntPrompt;
import com.mmarvick.urgentcall.widgets.EditTextStringPrompt;
import com.mmarvick.urgentcall.widgets.StateListsPrompt;
import com.mmarvick.urgentcall.widgets.StateOnOffListPrompt;
import com.mmarvick.urgentcall.widgets.StateOnOffPrompt;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity 
			implements OnSharedPreferenceChangeListener {
	PreferenceScreen prefScreen;
	Preference appState;
	Preference repeatCallState;
	Preference callMins;
	Preference callQty;
	Preference singleCallState;
	Preference msgState;
	Preference msgToken;
	
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        prefScreen = getPreferenceScreen();
        
        appState = findPreference("APP_STATUS");
        repeatCallState = findPreference("RC_STATUS");
        callMins = findPreference("CALL_MIN");
        callQty = findPreference("CALL_QTY");
        singleCallState = findPreference("SC_STATUS");
        msgState = findPreference("MSG_STATUS");
        msgToken = findPreference("MSG_STRING");
        
        appState.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new StateOnOffPrompt(SettingsActivity.this, Constants.OVERALL_STATE).show();

				return true;
			}
		});
        
        repeatCallState.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new StateListsPrompt(SettingsActivity.this, RulesEntry.REPEATED_CALL_STATE).show();

				return true;
			}
		});
        
        callMins.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new EditTextIntPrompt(SettingsActivity.this, Constants.CALL_MIN_MIN, Constants.CALL_MIN_MAX,
						Constants.CALL_MIN, Constants.CALL_MIN_DEFAULT, Constants.CALL_MIN_TITLE);

				return true;
			}
		});
        
        callQty.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new EditTextIntPrompt(SettingsActivity.this, Constants.CALL_QTY_MIN, Constants.CALL_QTY_MAX,
						Constants.CALL_QTY, Constants.CALL_QTY_DEFAULT, Constants.CALL_QTY_TITLE);
				return true;
			}
		});
        
        singleCallState.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new StateOnOffListPrompt(SettingsActivity.this, RulesEntry.SINGLE_CALL_STATE).show();

				return true;
			}
		}); 
        
        msgState.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new StateListsPrompt(SettingsActivity.this, RulesEntry.MSG_STATE).show();

				return true;
			}
		}); 
        
        msgToken.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new EditTextStringPrompt(SettingsActivity.this, Constants.MSG_MESSAGE_MIN,
						Constants.MSG_MESSAGE, Constants.MSG_MESSAGE_DEFAULT, Constants.MSG_MESSAGE_TITLE);

				return true;
			}
		});
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab = getActionBar();
            ab.show();
        }
    }

    
    
    
    @Override
    public void onResume() {
    	prefScreen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        prefScreen.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }    

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
	}
   
}