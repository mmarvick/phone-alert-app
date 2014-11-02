package com.mmarvick.urgentcall.settings;

import java.util.ArrayList;
import java.util.List;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.activities.ContactListActivity;
import com.mmarvick.urgentcall.data.OldPrefHelper;
import com.mmarvick.urgentcall.data.OldDbContractDatabase.RulesEntryOld;
import com.mmarvick.urgentcall.widgets.OnOptionsChangedListener;
import com.mmarvick.urgentcall.widgets.SliderPrompt;
import com.mmarvick.urgentcall.widgets.UpgradeDialog;

import android.content.Intent;
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
				if (alertType == RulesEntryOld.SC_STATE && !(getResources().getBoolean(R.bool.paid_version))) {
					UpgradeDialog.upgradeDialog(AlertSettingsActivity.this, getString(R.string.upgrade_body_sc));
				} else {
					if (OldPrefHelper.getState(getApplicationContext(), alertType) == Constants.URGENT_CALL_STATE_OFF) {
						int backupState = OldPrefHelper.getBackupState(getApplicationContext(), alertType);
						OldPrefHelper.setState(getApplicationContext(), alertType, backupState);
					} else {
						OldPrefHelper.saveBackupState(getApplicationContext(), alertType);
						OldPrefHelper.setState(getApplicationContext(), alertType, Constants.URGENT_CALL_STATE_OFF);
					}
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
		        if (OldPrefHelper.getState(getApplicationContext(), alertType) == Constants.URGENT_CALL_STATE_WHITELIST) {
		        	listIntent.putExtra(Constants.USER_STATE, RulesEntryOld.STATE_ON);
		        } else {
		        	listIntent.putExtra(Constants.USER_STATE, RulesEntryOld.STATE_OFF);
		        }
		        startActivity(listIntent);
				return true;
			}
		});
		
		how.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String howValue = (String) newValue;
		    	OldPrefHelper.setMessageHow(getApplicationContext(), alertType, howValue);
		    	setStates();
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
    	int mode = OldPrefHelper.getState(getApplicationContext(), alertType);
    	
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
    		recentState = OldPrefHelper.getBackupState(getApplicationContext(), alertType);
    	}
    	
    	if (recentState == Constants.URGENT_CALL_STATE_WHITELIST) {
    		whoList.setTitle("Allow List");    		
    	} else if (recentState == Constants.URGENT_CALL_STATE_BLACKLIST) {
    		whoList.setTitle("Block List");    		
    	} else {
    		whoList.setEnabled(false);
    		whoList.setTitle("Allow / Block List");    		
    	}    	
    	
    	String howValue = OldPrefHelper.getMessageHow(getApplicationContext(), alertType);
    	if (how.getValue() == null) {
    		how.setValue(howValue);
    	}
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
    	
    	Uri ringUri = OldPrefHelper.getMessageSound(getApplicationContext(), alertType);
    	sound.setSummary(RingtoneManager.getRingtone(getApplicationContext(), ringUri).getTitle(getApplicationContext()));
    	sound.setDefaultValue(ringUri);
    	
    	volume.setSummary(OldPrefHelper.getMessageVolumePercent(getApplicationContext(), alertType));
    	
    	if (mode == Constants.URGENT_CALL_STATE_OFF) {
	    	for (Preference p : prefs) {
	    		p.setEnabled(false);
	    	}
			//behaviorCategory.setShouldDisableView(true);
			//filterCategory.setShouldDisableView(true);
    	}
    }
    
}
