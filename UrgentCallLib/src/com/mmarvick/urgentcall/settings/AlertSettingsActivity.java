package com.mmarvick.urgentcall.settings;

import java.util.ArrayList;
import java.util.List;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.activities.ContactListActivity;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbContract;
import com.mmarvick.urgentcall.data.RulesDbHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;
import com.mmarvick.urgentcall.widgets.EditTextIntPrompt;
import com.mmarvick.urgentcall.widgets.EditTextStringPrompt;
import com.mmarvick.urgentcall.widgets.OnOptionsChangedListener;
import com.mmarvick.urgentcall.widgets.SliderPrompt;
import com.mmarvick.urgentcall.widgets.StateListsPrompt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.util.Log;

public class AlertSettingsActivity extends PreferenceActivity {

	private PreferenceScreen prefScreen;
	private Preference onState;
	
	protected PreferenceCategory filterCategory;
	protected PreferenceCategory behaviorCategory;
	
	protected List<Preference> prefs;
	
	protected Preference whoList;
	protected ListPreference how;
	protected Preference noise;
	protected RingtonePreference sound;
	protected Preference volume;
	
	protected int xml;
	protected String alertType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadPrefs();
		startPrefListeners();	
	}
	
    @Override
    public void onResume() {
	     setStates();
	     super.onResume();
    }	
	
	protected void loadPrefs() {
		addPreferencesFromResource(xml);
		
		prefScreen = getPreferenceScreen();
		
		behaviorCategory = (PreferenceCategory) findPreference(alertType + "_BEHAVIOR_CATEGORY");
		filterCategory = (PreferenceCategory) findPreference(alertType + "_FILTER_CATEGORY");
		
		onState = findPreference(alertType + "_STATUS");
		
		whoList = findPreference(alertType + "_FILTER_USERS");
		how = (ListPreference) findPreference(alertType + "_HOW");
		sound = (RingtonePreference) findPreference(alertType + "_SOUND");
		volume = findPreference(alertType + "_VOLUME");
		
		prefs = new ArrayList<Preference>();
		prefs.add(whoList);
		prefs.add(how);
		prefs.add(sound);
		prefs.add(volume);
	}
	
	protected void startPrefListeners() {
		onState.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (PrefHelper.getState(getApplicationContext(), alertType) == Constants.URGENT_CALL_STATE_OFF) {
					int backupState = PrefHelper.getBackupState(getApplicationContext(), alertType);
					PrefHelper.setState(getApplicationContext(), alertType, backupState);
				} else {
					PrefHelper.saveBackupState(getApplicationContext(), alertType);
					PrefHelper.setState(getApplicationContext(), alertType, Constants.URGENT_CALL_STATE_OFF);
				}
				setStates();
				return true;
			}
		});
		

		
		whoList.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
		        Intent listIntent = new Intent(getApplicationContext(), ContactListActivity.class);
		        listIntent.putExtra(Constants.ALERT_TYPE, alertType);
		        if (PrefHelper.getState(getApplicationContext(), alertType) == Constants.URGENT_CALL_STATE_WHITELIST) {
		        	listIntent.putExtra(Constants.USER_STATE, RulesEntry.STATE_ON);
		        } else {
		        	listIntent.putExtra(Constants.USER_STATE, RulesEntry.STATE_OFF);
		        }
		        startActivity(listIntent);
				return true;
			}
		});
		
		how.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String howValue = (String) newValue;
		    	if (howValue.equals(Constants.ALERT_HOW_RING)) {
		    		how.setSummary("Ring");
		    	} else if (howValue.equals(Constants.ALERT_HOW_RING_AND_VIBE)) {
		    		how.setSummary("Ring and vibrate");
		    	} else {
		    		how.setSummary("Vibrate");
		    	}
				return true;
			}
		});			
		
		volume.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SliderPrompt volumePrompt = new SliderPrompt(AlertSettingsActivity.this, Constants.ALERT_VOLUME_DEFAULT,
						alertType + Constants.ALERT_VOLUME, Constants.ALERT_VOLUME_MAX, "Volume", R.drawable.ic_action_ring_volume);
				volumePrompt.setOnOptionsChangedListener(new OnOptionsChangedListener() {
					
					@Override
					public void onOptionsChanged() {
						setStates();
						
					}
				});
				
				volumePrompt.show();
				return true;
			}
		});			
	}
    
    protected void setStates() {
    	int mode = PrefHelper.getState(getApplicationContext(), alertType);
    	
    	if (mode != Constants.URGENT_CALL_STATE_OFF) {
	    	for (Preference p : prefs) {
	    		p.setEnabled(true);
	    	}
	    	//behaviorCategory.setEnabled(true);
	    	//filterCategory.setEnabled(true);
			//behaviorCategory.setShouldDisableView(false);
			//filterCategory.setShouldDisableView(false);
			
    	}
    	
    	if (mode == Constants.URGENT_CALL_STATE_OFF) {
    		onState.setSummary("Off");
    	} else {
    		onState.setSummary("On");
    	}
    	
    	if (mode == Constants.URGENT_CALL_STATE_ON) {
    		
    		whoList.setTitle("Allow / Block List");
    	}

    	
    	int recentState = mode;
    	if (mode == Constants.URGENT_CALL_STATE_OFF) {
    		recentState = PrefHelper.getBackupState(getApplicationContext(), alertType);
    	}
    	
    	if (recentState == Constants.URGENT_CALL_STATE_WHITELIST) {
    		whoList.setTitle("Allow List");    		
    	} else if (recentState == Constants.URGENT_CALL_STATE_BLACKLIST) {
    		whoList.setTitle("Block List");    		
    	} else {
    		whoList.setEnabled(false);
    		whoList.setTitle("Allow / Block List");    		
    	}    	
    	
    	String howValue = PrefHelper.getMessageHow(getApplicationContext(), alertType);
    	if (howValue.equals(Constants.ALERT_HOW_RING)) {
    		how.setSummary("Ring");
    		sound.setEnabled(true);
    		volume.setEnabled(true);
    	} else if (howValue.equals(Constants.ALERT_HOW_RING_AND_VIBE)) {
    		how.setSummary("Ring and vibrate");
    		sound.setEnabled(true);
    		volume.setEnabled(true);    		
    	} else {
    		how.setSummary("Vibrate");
    		sound.setEnabled(false);
    		volume.setEnabled(false);    		
    	}	
    	
    	Uri ringUri = PrefHelper.getMessageSound(getApplicationContext(), alertType);
    	sound.setSummary(RingtoneManager.getRingtone(getApplicationContext(), ringUri).getTitle(getApplicationContext()));
    	
    	volume.setSummary(PrefHelper.getMessageVolumePercent(getApplicationContext(), alertType));
    	
    	if (mode == Constants.URGENT_CALL_STATE_OFF) {
	    	for (Preference p : prefs) {
	    		p.setEnabled(false);
	    	}
			//behaviorCategory.setShouldDisableView(true);
			//filterCategory.setShouldDisableView(true);
    	}
    }
    
}
