package com.mmarvick.urgentcall.data;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class PrefHelper {

	public static int getState(Context context, String alertType) {
		int def = Constants.URGENT_CALL_STATE_ON;
		return getPrefs(context).getInt(alertType, def);
	}

	public static void setState(Context context, String alertType, int state) {
		Editor editor = getPrefs(context).edit();
		editor.putInt(alertType, state);
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
			edit.putInt(Constants.DISCLAIMER_BACKUP_MODE, getState(context, Constants.APP_STATE));
			setState(context, Constants.APP_STATE, Constants.URGENT_CALL_STATE_OFF);
			edit.commit();
		}
	}
	
	public static void disclaimerResumeBackup(Context context) {
		SharedPreferences prefs = getPrefs(context);
		Editor edit = prefs.edit();
		setState(context, Constants.APP_STATE, prefs.getInt(Constants.DISCLAIMER_BACKUP_MODE, Constants.URGENT_CALL_STATE_ON));
		edit.putBoolean(Constants.DISCLAIMER_BACKUP_FLAG, false);
		edit.commit();
	}
	
	private static SharedPreferences getPrefs(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
