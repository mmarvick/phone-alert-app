package com.mmarvick.urgentcall.helpers;

import com.mmarvick.urgentcall.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class PrefHelper {
    // STATE FOR WHETHER URGENT CALL IS ON OR OFF
    public final static String APP_STATE = "STATE";
    public final static int URGENT_CALL_STATE_OFF = 0;
    public final static int URGENT_CALL_STATE_ON = 1;

    // VALUE FOR TIME THAT SNOOZE IS COMPLETE
    public final static String SNOOZE_TIME = "SNOOZE";

    // SETTINGS FOR THE DISCLAIMER VERSION MOST RECENTLY AGREED TO
    public final static String DISCLAIMER_VERSION = "DISCLAIMER_VERSION";
    public final static int DISCLAIMER_DEFAULT = 0;

	public static boolean getOnState(Context context) {
		int def = URGENT_CALL_STATE_ON;
		if (getPrefs(context).getInt(APP_STATE, def) == URGENT_CALL_STATE_ON) {
            return true;
        } else {
            return false;
        }
	}

	public static void toggleOnState(Context context) {
		Editor editor = getPrefs(context).edit();
        boolean newState = !getOnState(context);
        if (newState) {
            editor.putInt(APP_STATE, URGENT_CALL_STATE_ON);
        } else {
            editor.putInt(APP_STATE, URGENT_CALL_STATE_OFF);
        }
		editor.commit();
	}
	
	public static void setSnoozeTime(Context context, long remaining) {
		Editor editor = getPrefs(context).edit();
		editor.putLong(SNOOZE_TIME, SystemClock.elapsedRealtime() + remaining);
		editor.commit();
	}
	
	public static boolean isSnoozing(Context context) {
		return (snoozeRemaining(context) > 0);
	}
	
	public static long snoozeRemaining(Context context) {
		long snoozeTime = getPrefs(context).getLong(SNOOZE_TIME, SystemClock.elapsedRealtime());
		long clockTime = SystemClock.elapsedRealtime();
		return snoozeTime - clockTime;
	}	
	
	public static boolean disclaimerCheck(Context context) {
		SharedPreferences prefs = getPrefs(context);
		int agreed = prefs.getInt(DISCLAIMER_VERSION, DISCLAIMER_DEFAULT);
		int current = context.getResources().getInteger(R.integer.disclaimer_version);
		return (agreed == current);
	}
	
	public static void disclaimerAgreed(Context context) {
		SharedPreferences prefs = getPrefs(context);
		Editor edit = prefs.edit();
		int version = context.getResources().getInteger(R.integer.disclaimer_version);
		edit.putInt(DISCLAIMER_VERSION, version);
		edit.commit();
	}
	
	private static SharedPreferences getPrefs(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
