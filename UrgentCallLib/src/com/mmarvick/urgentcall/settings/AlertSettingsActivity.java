package com.mmarvick.urgentcall.settings;

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
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.util.Log;

public class AlertSettingsActivity extends PreferenceActivity {

	private PreferenceScreen prefScreen;
	private Preference onState;
	
	private Preference[] prefs;
	private Preference keyword;
	private Preference whoAlerts;
	private Preference whoList;
	private ListPreference how;
	private Preference noise;
	private Preference time;
	private RingtonePreference sound;
	private Preference volume;
	
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
	
	private void loadPrefs() {
		addPreferencesFromResource(R.xml.pref_msg);
		
		prefScreen = getPreferenceScreen();
		
		onState = findPreference("msg_state_STATUS");
		keyword = findPreference("msg_state_KEY");
		whoAlerts = findPreference("msg_state_FILTER");
		whoList = findPreference("msg_state_FILTER_USERS");
		how = (ListPreference) findPreference("msg_state_HOW");
		time = findPreference("msg_state_TIME");
		sound = (RingtonePreference) findPreference("msg_state_SOUND");
		volume = findPreference("msg_state_VOLUME");
		
		prefs = new Preference[] {onState, keyword, whoAlerts, whoList, how, time, sound, volume};
	}
	
	private void startPrefListeners() {
		onState.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_OFF) {
					int backupState = PrefHelper.getBackupState(getApplicationContext(), RulesEntry.MSG_STATE);
					PrefHelper.setState(getApplicationContext(), RulesEntry.MSG_STATE, backupState);
				} else {
					PrefHelper.saveBackupState(getApplicationContext(), RulesEntry.MSG_STATE);
					PrefHelper.setState(getApplicationContext(), RulesEntry.MSG_STATE, Constants.URGENT_CALL_STATE_OFF);
				}
				setStates();
				return true;
			}
		});
		
		keyword.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				EditTextStringPrompt msgKeyPrompt = new EditTextStringPrompt(AlertSettingsActivity.this, Constants.MSG_MESSAGE_MIN,
						Constants.MSG_MESSAGE, Constants.MSG_MESSAGE_DEFAULT, Constants.MSG_MESSAGE_TITLE);
				msgKeyPrompt.setOnOptionsChangedListener(new OnOptionsChangedListener() {
					
					@Override
					public void onOptionsChanged() {
						setStates();
						
					}
				});
				
				msgKeyPrompt.show();
				return true;
			}
		});
		
		whoAlerts.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				StateListsPrompt msgStatePrompt = new StateListsPrompt(AlertSettingsActivity.this, RulesEntry.MSG_STATE,
						getApplicationContext().getString(R.string.state_change_dialog_title_msg), false);
				msgStatePrompt.setOnOptionsChangedListener(new OnOptionsChangedListener() {
						
						@Override
						public void onOptionsChanged() {
							setStates();
							
						}
					});
				msgStatePrompt.show();
				return true;
			}
		});
		
		whoList.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
		        Intent listIntent = new Intent(getApplicationContext(), ContactListActivity.class);
		        listIntent.putExtra(Constants.ALERT_TYPE, RulesEntry.MSG_STATE);
		        if (PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_WHITELIST) {
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

		time.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				EditTextIntPrompt msgTimePrompt = new EditTextIntPrompt(AlertSettingsActivity.this, 1, 60,
						RulesEntry.MSG_STATE + Constants.ALERT_TIME, 10, "How long? (seconds)");
				msgTimePrompt.setOnOptionsChangedListener(new OnOptionsChangedListener() {
					
					@Override
					public void onOptionsChanged() {
						setStates();
						
					}
				});
				
				msgTimePrompt.show();
				return true;
			}
		});		
		
		volume.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				SliderPrompt volumePrompt = new SliderPrompt(AlertSettingsActivity.this, Constants.ALERT_VOLUME_DEFAULT,
						RulesEntry.MSG_STATE + Constants.ALERT_VOLUME, Constants.ALERT_VOLUME_MAX, "Volume", R.drawable.ic_action_ring_volume);
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
    
    private void setStates() {
    	if (PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_OFF) {
    		onState.setSummary("Off");
    		keyword.setEnabled(false);
    	} else {
    		onState.setSummary("On");
    		keyword.setEnabled(true);
    	}
    	
    	keyword.setSummary(PrefHelper.getMessageToken(getApplicationContext()));
    	
    	if (PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_ON) {
    		setWhoTitles(Constants.URGENT_CALL_STATE_ON);
    		whoAlerts.setEnabled(true);
    		whoList.setEnabled(false);
    	} else if (PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_WHITELIST) {
    		setWhoTitles(Constants.URGENT_CALL_STATE_WHITELIST);
    		whoAlerts.setEnabled(true);
    		whoList.setEnabled(true);
    	} else if (PrefHelper.getState(getApplicationContext(), RulesEntry.MSG_STATE) == Constants.URGENT_CALL_STATE_BLACKLIST) {
    		setWhoTitles(Constants.URGENT_CALL_STATE_BLACKLIST);
    		whoAlerts.setEnabled(true);
    		whoList.setEnabled(true);    		
    	} else {
    		setWhoTitles(PrefHelper.getBackupState(getApplicationContext(), RulesEntry.MSG_STATE));
    		whoAlerts.setEnabled(false);
    		whoList.setEnabled(false);      		
    	}
    	
    	String howValue = PrefHelper.getMessageHow(getApplicationContext(), RulesEntry.MSG_STATE);
    	if (howValue.equals(Constants.ALERT_HOW_RING)) {
    		how.setSummary("Ring");
    	} else if (howValue.equals(Constants.ALERT_HOW_RING_AND_VIBE)) {
    		how.setSummary("Ring and vibrate");
    	} else {
    		how.setSummary("Vibrate");
    	}
    	
    	
    	time.setSummary(PrefHelper.getMessageTime(getApplicationContext(), RulesEntry.MSG_STATE) + " seconds");
    	
    	Uri ringUri = PrefHelper.getMessageSound(getApplicationContext(), RulesEntry.MSG_STATE);
    	Log.e("Test", ringUri.toString());
    	sound.setSummary(RingtoneManager.getRingtone(getApplicationContext(), ringUri).getTitle(getApplicationContext()));
    	
    	volume.setSummary(PrefHelper.getMessageVolumePercent(getApplicationContext(), RulesEntry.MSG_STATE));
    	
    }
    
    private void setWhoTitles(int state) {
    	if (state == Constants.URGENT_CALL_STATE_WHITELIST) {
    		whoAlerts.setSummary("Allowed users only");
    		whoList.setTitle("Allow List");    		
    	} else if (state == Constants.URGENT_CALL_STATE_BLACKLIST) {
    		whoAlerts.setSummary("Everyone but blocked users");
    		whoList.setTitle("Block List");    		
    	} else {
    		whoAlerts.setSummary("All users");
    		whoList.setTitle("Allow / Block List");    		
    	}
    }
    
}
