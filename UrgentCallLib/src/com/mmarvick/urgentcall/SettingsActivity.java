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
	Preference listSelect;
	Preference listSelectLite;
	Preference whitelistPref;
	Preference blacklistPref;
	
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	
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
        listSelectLite = findPreference("LIST_TYPE_LITE");
        
        if (getResources().getBoolean(R.bool.paid_version)) {
        	prefScreen.removePreference(listSelectLite);
        }
        else {
        	prefScreen.removePreference(listSelect);
        	
        	listSelectLite.setOnPreferenceClickListener(new OnPreferenceClickListener() {
        		
				@Override
				public boolean onPreferenceClick(Preference preference) {
					UpgradeDialog.upgradeDialog(SettingsActivity.this, 
							"Users of Urgent Call Pro can filter users with a whitelist or blacklist.\n\n"
									+ "Users of Urgent Call Lite must leave the app on or off for all users.");
					return true;
				}
			});
        }
        
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
    	int mode = PrefHelper.getListMode(getApplicationContext());
    	
    	if (mode == Constants.LIST_NONE) {
    		blacklistPref.setEnabled(false);
    		whitelistPref.setEnabled(false);
    	} else {
    		blacklistPref.setEnabled(true);
    		whitelistPref.setEnabled(true);
    		
    	}
    	
    	if (mode == Constants.LIST_BLACKLIST) {
    		prefScreen.removePreference(whitelistPref);
    		prefScreen.addPreference(blacklistPref);
    	} else {
    		prefScreen.removePreference(blacklistPref);
    		prefScreen.addPreference(whitelistPref);
    	}
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		setWhiteBlackList();
	}
   
}