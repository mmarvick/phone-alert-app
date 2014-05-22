package com.mmarvick.urgentcall;

import android.content.Context;
import android.content.SharedPreferences;
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
		String qty = getPrefs(context).getString(Constants.CALL_QTY, "" + Constants.CALL_QTY_DEFAULT);
		return Integer.parseInt(qty);
		
	}
	
	public static int getCallMins(Context context) {
		String min = getPrefs(context).getString(Constants.CALL_MIN, "" + Constants.CALL_MIN_DEFAULT);
		return Integer.parseInt(min);
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
