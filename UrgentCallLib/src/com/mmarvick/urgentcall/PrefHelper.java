package com.mmarvick.urgentcall;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class PrefHelper {
	public static int getState(Context context) {
		return getPrefs(context).getInt(Constants.SIMPLE_STATE, Constants.SIMPLE_STATE_ON);
	}
	
	public static void setState(Context context, int state) {
		Editor editor = getPrefs(context).edit();
		editor.putInt(Constants.SIMPLE_STATE, state);
		editor.commit();
	}
	
	public static int getListMode(Context context) {
		String listMode = getPrefs(context).getString(Constants.LIST_TYPE, "" + Constants.LIST_NONE);
		return Integer.parseInt(listMode);
	}
	
	public static int getCallQty(Context context) {
		return getCallValue(context, Constants.CALL_QTY, Constants.CALL_QTY_DEFAULT);
		
	}
	
	public static int getCallMins(Context context) {
		return getCallValue(context, Constants.CALL_MIN, Constants.CALL_MIN_DEFAULT);
	}	
	
	public static int getCallValue(Context context, String name, int def) {
		String value = getPrefs(context).getString(name, "" + def);
		return Integer.parseInt(value);
	}	
	
	public static void setCallValue(Context context, String name, int value) {
		Editor editor = getPrefs(context).edit();
		editor.putString(name, "" + value);
		editor.commit();
	}
	
	public static void setSnoozeTime(Context context, long remaining) {
		Editor editor = getPrefs(context).edit();
		editor.putLong(Constants.SNOOZE_TIME, SystemClock.elapsedRealtime() + remaining);
		editor.commit();
	}
	
	public static boolean isSnoozing(Context context) {
		return (snoozeRemaining(context) > 0);
	}
	
	public static long snoozeRemaining(Context context) {
		long snoozeTime = getPrefs(context).getLong(Constants.SNOOZE_TIME, SystemClock.elapsedRealtime());
		long clockTime = SystemClock.elapsedRealtime();
		return snoozeTime - clockTime;
	}	
	
	public static void saveCurrentPhoneState(Context context, AudioManager audio) {
		Editor editor = getPrefs(context).edit();
		editor.putInt(Constants.SETTING_VOLUME, audio.getStreamVolume(AudioManager.STREAM_RING));
		editor.putBoolean(Constants.SETTING_VOLUME_CHANGED, true);
		editor.commit();
	}
	
	public static void resetSavedPhoneState(Context context, AudioManager audio) {
		SharedPreferences prefs = getPrefs(context);
		Editor editor = prefs.edit();
		if (prefs.getBoolean(Constants.SETTING_VOLUME_CHANGED, false)) {
			audio.setStreamVolume(AudioManager.STREAM_RING, prefs.getInt(Constants.SETTING_VOLUME, audio.getStreamMaxVolume(AudioManager.STREAM_RING)), 0);
			editor.putBoolean(Constants.SETTING_VOLUME_CHANGED, false);
			editor.commit();
		}
	}
	
	private static SharedPreferences getPrefs(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
