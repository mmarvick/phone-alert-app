package com.mmarvick.urgentcall;

import java.util.Date;








import com.mmarvick.urgentcall.RulesDbContract.RulesEntry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallAlertBroadcastReceiver extends BroadcastReceiver {
	private final String TAG = "PhoneAlert";
	public final String SETTING_MODE = "mode";
	public final String SETTING_MODE_CHANGED = "modeChanged";
	private SharedPreferences prefs;
	SharedPreferences.Editor editor;
	RulesDbHelper dbHelper;
	AudioManager audio;

	@Override
	public void onReceive(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		editor = prefs.edit();
		dbHelper = new RulesDbHelper(context);
		audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				
		if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
			saveState(context);
			String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			
			if (isOn(context, incomingNumber)) {
				int allowedMins = allowedMins();
				int allowedCalls = allowedCalls();
				int called = timesCalled(context, incomingNumber, allowedMins);
				//TODO: this is buggy
				if (called >= allowedCalls - 1) {
					alertAction(context);
				} 					
			}
		//TODO: MAKE THIS WORK FOR WHEN CALL IS IGNORED
		} else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state) || TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
			resetAction(context);
		}
	}
	
	private boolean isOn(Context context, String incomingNumber) {
		int state = prefs.getInt(Constants.SIMPLE_STATE, Constants.SIMPLE_STATE_ON);
		String lookup = dbHelper.getLookupFromNumber(incomingNumber);
		
		switch(state) {
		
		case Constants.SIMPLE_STATE_ON:
			return true;
		case Constants.SIMPLE_STATE_OFF:
			return false;
		case Constants.SIMPLE_STATE_WHITELIST:
			if (lookup!=null && dbHelper.isInDb(lookup)) {
				return dbHelper.getStateOn(lookup);
			} else {
				return false;
			}
		case Constants.SIMPLE_STATE_BLACKLIST:
			if (lookup!=null && dbHelper.isInDb(lookup)) {
				return dbHelper.getStateOn(lookup);
			} else {
				return true;
			}
		default:
			return true;
		}
	}
	
	private int allowedCalls() {
		int defaultCalls = dbHelper.getCallsAllowed(RulesEntry.LOOKUP_DEFAULT);	
		return defaultCalls;
		
	}
	
	private int allowedMins() {
		int defaultMins = dbHelper.getCallMins(RulesEntry.LOOKUP_DEFAULT);
		return defaultMins;
	}	
	
	
	private void resetAction(Context context) {
		if (prefs.getBoolean(SETTING_MODE_CHANGED, false)) {
			audio.setRingerMode(prefs.getInt("mode", AudioManager.RINGER_MODE_NORMAL));
			editor.putBoolean(SETTING_MODE_CHANGED, false);
			editor.commit();
		}
	}
	
	private int saveState(Context context) {
		editor.putInt(SETTING_MODE, audio.getRingerMode());
		editor.putBoolean(SETTING_MODE_CHANGED, true);
		editor.commit();
		return audio.getRingerMode();
	}
	
	private void alertAction(Context context) {
		if (audio.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			audio.setStreamVolume(AudioManager.STREAM_RING, audio.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
			/*Uri toneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			
			//TODO: Fix this bug -- sometimes the phone will ring twice, with an echo
			Ringtone tone = RingtoneManager.getRingtone(context, toneUri);
			if (audio.getMode() != AudioManager.MODE_RINGTONE && !tone.isPlaying()) {
				tone.play();
				Log.d(TAG, "Trigger a ring!");
			} */
		} 
	}
	
	private int timesCalled(Context context, String incomingNumber, int minutes) {
		String time = "" + ((new Date()).getTime() - minutes * 60 * 1000);
		String selection = CallLog.Calls.NUMBER + " = ?";
		selection += " AND " + CallLog.Calls.DATE + "> ?";
		String[] selectors = {incomingNumber, time};
		Cursor calls = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, selectors, null);
		calls.moveToFirst();
		int numCalls = 0;
		while (!calls.isAfterLast()) {
			calls.moveToNext();
			numCalls += 1;
		}
		return numCalls;
	}

}
