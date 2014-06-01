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

public class SettingsSingleCallActivity extends PreferenceActivity {
	Preference singleCallState;
	
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_single_call);
        
        singleCallState = findPreference("SC_STATUS");
        
        singleCallState.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new StateOnOffListPrompt(SettingsSingleCallActivity.this, RulesEntry.SC_STATE).show();

				return true;
			}
		}); 
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab = getActionBar();
            ab.show();
        }
    }
   
}