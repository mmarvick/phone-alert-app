package com.mmarvick.urgentcall;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class PrefHelper {
	public static int getState(Context context) {
		return getPrefs(context).getInt(Constants.SIMPLE_STATE, Constants.SIMPLE_STATE_ON);
	}
	
	public static int getListMode(Context context) {
		String listMode = getPrefs(context).getString(Constants.LIST_TYPE, "" + Constants.LIST_WHITELIST);
		return Integer.parseInt(listMode);
	}
	
	public static int getCallQty(Context context) {
		return getCallValue(context, Constants.CALL_QTY);
		
	}
	
	public static int getCallMins(Context context) {
		return getCallValue(context, Constants.CALL_MIN);
	}	
	
	public static int getCallValue(Context context, String name) {
		String value = getPrefs(context).getString(name, "" + Constants.CALL_MIN_DEFAULT);
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
	
	private static SharedPreferences getPrefs(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	private static SharedPreferences.Editor getEditor(SharedPreferences pref) {
		return pref.edit();
	}
}
