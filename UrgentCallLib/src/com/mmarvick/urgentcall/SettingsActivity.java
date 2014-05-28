package com.mmarvick.urgentcall;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
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
	Preference state;
	Preference callMins;
	Preference callQty;
	
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        prefScreen = getPreferenceScreen();
        
        state = findPreference("STATUS");
        callMins = findPreference("CALL_MIN");
        callQty = findPreference("CALL_QTY");
        
        state.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new StatePrompt(SettingsActivity.this);

				return true;
			}
		});
        
        callMins.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new EditTextPrompt(SettingsActivity.this, Constants.CALL_MIN_MIN, Constants.CALL_MIN_MAX,
						Constants.CALL_MIN, Constants.CALL_MIN_DEFAULT, Constants.CALL_MIN_TITLE);

				return true;
			}
		});
        
        callQty.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new EditTextPrompt(SettingsActivity.this, Constants.CALL_QTY_MIN, Constants.CALL_QTY_MAX,
						Constants.CALL_QTY, Constants.CALL_QTY_DEFAULT, Constants.CALL_QTY_TITLE);
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