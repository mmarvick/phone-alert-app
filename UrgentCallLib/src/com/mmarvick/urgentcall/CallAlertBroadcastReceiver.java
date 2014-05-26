package com.mmarvick.urgentcall;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallAlertBroadcastReceiver extends BroadcastReceiver {
	public final String SETTING_VOLUME = "volume";
	public final String SETTING_VOLUME_CHANGED = "volumeChanged";
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
		
		Log.e("PHONE STATE:", "" + state);
				
		if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
			String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			
			if (isOn(context, incomingNumber)) {
				int allowedMins = PrefHelper.getCallMins(context);
				int allowedCalls = PrefHelper.getCallQty(context);;
				int called = timesCalled(context, incomingNumber, allowedMins);
				//TODO: this is buggy
				if (called >= allowedCalls - 1) {
					Log.e("Alert", "Let's try to alert!");
					alertAction(context);
				} 					
			}
		} else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state) || TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
			resetAction(context);
		}
	}
	
	private boolean isOn(Context context, String incomingNumber) {
		int state = PrefHelper.getState(context);
		String lookup = dbHelper.getLookupFromNumber(incomingNumber);
		
		if (PrefHelper.isSnoozing(context)) {
			return false;
		}
		
		switch(state) {
		
		case Constants.SIMPLE_STATE_ON:
			return true;
		case Constants.SIMPLE_STATE_OFF:
			return false;
		case Constants.SIMPLE_STATE_SOME:
			if (lookup!=null && dbHelper.isInDb(lookup)) {
				return dbHelper.getState(lookup);
			} else {
				return (PrefHelper.getState(context) == Constants.LIST_WHITELIST); 
			}
		default:
			return true;
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
	
	private void alertAction(Context context) {
		if (audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			saveState(context);
			audio.setStreamVolume(AudioManager.STREAM_RING, audio.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
		} else {
			Intent ringService = new Intent(context, RingService.class);
			context.startService(ringService);			
		}
	}	
	
	private void resetAction(Context context) {
		Intent ringService = new Intent(context, RingService.class);
		context.stopService(ringService);
		
		if (prefs.getBoolean(SETTING_VOLUME_CHANGED, false)) {
			audio.setStreamVolume(AudioManager.STREAM_RING, prefs.getInt(SETTING_VOLUME, audio.getStreamMaxVolume(AudioManager.STREAM_RING)), 0);
			editor.putBoolean(SETTING_VOLUME_CHANGED, false);
			editor.commit();
		}
	}
	
	private int saveState(Context context) {
		editor.putInt(SETTING_VOLUME, audio.getStreamVolume(AudioManager.STREAM_RING));
		editor.putBoolean(SETTING_VOLUME_CHANGED, true);
		editor.commit();
		return audio.getRingerMode();
	}
	


}
