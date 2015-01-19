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

	public static boolean getOnState(Context context) {
		return (getPrefs(context).getInt(APP_STATE, URGENT_CALL_STATE_ON)
                == URGENT_CALL_STATE_ON);
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

	public static SharedPreferences getPrefs(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
