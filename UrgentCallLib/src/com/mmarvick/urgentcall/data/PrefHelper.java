package com.mmarvick.urgentcall.data;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class PrefHelper {
	public static int getView(Context context) {
		return getPrefs(context).getInt(Constants.VIEW, Constants.VIEW_OLD);
	}
	
	public static void setView(Context context, int view) {
		Editor editor = getPrefs(context).edit();
		editor.putInt(Constants.VIEW, view);
		editor.commit();
	}
	
	
	public static int getState(Context context, String alertType) {
		int def = Constants.URGENT_CALL_STATE_ON;
		if (alertType == RulesEntry.SC_STATE) {
			def = Constants.URGENT_CALL_STATE_OFF;
		}
		return getPrefs(context).getInt(alertType, def);
	}
	
	public static void setState(Context context, String alertType, int state) {
		Editor editor = getPrefs(context).edit();
		editor.putInt(alertType, state);
		editor.commit();
	}
	
	public static void saveBackupState(Context context, String alertType) {
		Editor editor = getPrefs(context).edit();
		editor.putInt(alertType + "_BACKUP", getState(context, alertType));
		editor.commit();
	}
	
	public static int getBackupState(Context context, String alertType) {
		int def = Constants.URGENT_CALL_STATE_ON;
		if (alertType == RulesEntry.SC_STATE) {
			def = Constants.URGENT_CALL_STATE_WHITELIST;
		}
		return getPrefs(context).getInt(alertType + "_BACKUP", def);		
	}
	
	public static String getMessageToken(Context context) {
		return getPrefs(context).getString(Constants.MSG_MESSAGE, Constants.MSG_MESSAGE_DEFAULT);
	}
	
	public static void setMessageToken(Context context, String value) {
		Editor editor = getPrefs(context).edit();
		editor.putString(Constants.MSG_MESSAGE, value);
		editor.commit();
	}
	
	public static int getRepeatedCallQty(Context context) {
		return getRepeatedCallValue(context, Constants.CALL_QTY, Constants.CALL_QTY_DEFAULT);
		
	}
	
	public static int getRepeatedCallMins(Context context) {
		return getRepeatedCallValue(context, Constants.CALL_MIN, Constants.CALL_MIN_DEFAULT);
	}	
	
	public static int getRepeatedCallValue(Context context, String name, int def) {
		String value = getPrefs(context).getString(name, "" + def);
		return Integer.parseInt(value);
	}	
	
	public static void setRepeatedCallValue(Context context, String name, int value) {
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
	
	public static boolean disclaimerCheck(Context context) {
		SharedPreferences prefs = getPrefs(context);
		int agreed = prefs.getInt(Constants.DISCLAIMER_VERSION, Constants.DISCLAIMER_DEFAULT);
		int current = context.getResources().getInteger(R.integer.disclaimer_version);
		return (agreed == current);
	}
	
	public static void disclaimerAgreed(Context context) {
		SharedPreferences prefs = getPrefs(context);
		Editor edit = prefs.edit();
		int version = context.getResources().getInteger(R.integer.disclaimer_version);
		edit.putInt(Constants.DISCLAIMER_VERSION, version);
		edit.commit();
	}
	
	public static void disclaimerSaveBackup(Context context) {
		SharedPreferences prefs = getPrefs(context);
		if (!prefs.getBoolean(Constants.DISCLAIMER_BACKUP_FLAG, false)) {
			Editor edit = prefs.edit();
			edit.putBoolean(Constants.DISCLAIMER_BACKUP_FLAG, true);
			edit.putInt(Constants.DISCLAIMER_BACKUP_MODE, getState(context, Constants.OVERALL_STATE));
			setState(context, Constants.OVERALL_STATE, Constants.URGENT_CALL_STATE_OFF);
			edit.commit();
		}
	}
	
	public static void disclaimerResumeBackup(Context context) {
		SharedPreferences prefs = getPrefs(context);
		Editor edit = prefs.edit();
		setState(context, Constants.OVERALL_STATE, prefs.getInt(Constants.DISCLAIMER_BACKUP_MODE, Constants.URGENT_CALL_STATE_ON));
		edit.putBoolean(Constants.DISCLAIMER_BACKUP_FLAG, false);
		edit.commit();
	}
	
	private static SharedPreferences getPrefs(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
