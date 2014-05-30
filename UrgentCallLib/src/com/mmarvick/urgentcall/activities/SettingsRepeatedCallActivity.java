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

public class SettingsRepeatedCallActivity extends PreferenceActivity {
	PreferenceScreen prefScreen;
	Preference repeatCallState;
	Preference callMins;
	Preference callQty;
	
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_repeat_call);
        
        repeatCallState = findPreference("RC_STATUS");
        callMins = findPreference("CALL_MIN");
        callQty = findPreference("CALL_QTY");
        
        repeatCallState.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new StateListsPrompt(SettingsRepeatedCallActivity.this, RulesEntry.REPEATED_CALL_STATE).show();

				return true;
			}
		});
        
        callMins.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new EditTextIntPrompt(SettingsRepeatedCallActivity.this, Constants.CALL_MIN_MIN, Constants.CALL_MIN_MAX,
						Constants.CALL_MIN, Constants.CALL_MIN_DEFAULT, Constants.CALL_MIN_TITLE);

				return true;
			}
		});
        
        callQty.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				new EditTextIntPrompt(SettingsRepeatedCallActivity.this, Constants.CALL_QTY_MIN, Constants.CALL_QTY_MAX,
						Constants.CALL_QTY, Constants.CALL_QTY_DEFAULT, Constants.CALL_QTY_TITLE);
				return true;
			}
		});
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab = getActionBar();
            ab.show();
        }
    }
   
}