package com.mmarvick.urgentcall;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        Preference whitelistPref = findPreference("EDIT_WHITELIST");
        Intent whitelistIntent = new Intent(this, ContactListActivity.class);
        whitelistIntent.putExtra(Constants.LIST_TYPE, Constants.LIST_WHITELIST);
        whitelistPref.setIntent(whitelistIntent);
        
        Preference blacklistPref = findPreference("EDIT_BLACKLIST");
        Intent blacklistIntent = new Intent(this, ContactListActivity.class);
        blacklistIntent.putExtra(Constants.LIST_TYPE, Constants.LIST_BLACKLIST);
        blacklistPref.setIntent(blacklistIntent);
    }
   
}