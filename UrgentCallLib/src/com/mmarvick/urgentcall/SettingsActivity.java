package com.mmarvick.urgentcall;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends PreferenceActivity 
			implements OnSharedPreferenceChangeListener {
	PreferenceScreen prefScreen;
	Preference listSelect;
	Preference whitelistPref;
	Preference blacklistPref;
	
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        prefScreen = getPreferenceScreen();
        
        whitelistPref = findPreference("EDIT_WHITELIST");
        Intent whitelistIntent = new Intent(this, ContactListActivity.class);
        whitelistIntent.putExtra(Constants.LIST_TYPE, Constants.LIST_WHITELIST);
        whitelistPref.setIntent(whitelistIntent);
        
        blacklistPref = findPreference("EDIT_BLACKLIST");
        Intent blacklistIntent = new Intent(this, ContactListActivity.class);
        blacklistIntent.putExtra(Constants.LIST_TYPE, Constants.LIST_BLACKLIST);
        blacklistPref.setIntent(blacklistIntent);
        
        listSelect = findPreference("LIST_TYPE");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab = getActionBar();
            ab.show();
        }
    }
    
    @Override
    public void onResume() {
    	setWhiteBlackList();
    	prefScreen.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    	super.onResume();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        prefScreen.getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }    
    
    private void setWhiteBlackList() {
    	if (!(getResources().getBoolean(R.bool.paid_version))) {
    		blacklistPref.setEnabled(false);
    		whitelistPref.setEnabled(false);
    		listSelect.setEnabled(false);
    	}
    	if (PrefHelper.getListMode(getApplicationContext()) == Constants.LIST_WHITELIST) {
    		prefScreen.removePreference(blacklistPref);
    		prefScreen.addPreference(whitelistPref);
    	} else {
    		prefScreen.removePreference(whitelistPref);
    		prefScreen.addPreference(blacklistPref);    		
    	}
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		setWhiteBlackList();
	}
   
}