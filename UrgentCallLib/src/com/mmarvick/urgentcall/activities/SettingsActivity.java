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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class SettingsActivity extends PreferenceActivity {
	Preference appState;
	Preference msgSettings;
	Preference rcSettings;
	Preference scSettings;
	
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        appState = findPreference("APP_STATUS");
        msgSettings = findPreference("MSG_SETTINGS");
        rcSettings = findPreference("RC_SETTINGS");
        scSettings = findPreference("SC_SETTINGS");
        
        appState.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new StateOnOffPrompt(SettingsActivity.this, Constants.OVERALL_STATE).show();

				return true;
			}
		});
        
        msgSettings.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(SettingsActivity.this, SettingsMessageActivity.class);
				startActivity(i);

				return true;
			}
		});
        
        rcSettings.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(SettingsActivity.this, SettingsRepeatedCallActivity.class);
				startActivity(i);

				return true;
			}
		});        
        
        scSettings.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent i = new Intent(SettingsActivity.this, SettingsSingleCallActivity.class);
				startActivity(i);

				return true;
			}
		}); 
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab = getActionBar();
            ab.show();
        }
    }
   
}