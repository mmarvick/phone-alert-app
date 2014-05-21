package com.mmarvick.urgentcall;

import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;

public class SnoozeHelper {
	public static boolean isSnoozing(SharedPreferences pref) {
		return (snoozeRemaining(pref) > 0);
	}
	
	public static long snoozeRemaining(SharedPreferences pref) {
		long snoozeTime = pref.getLong(Constants.SNOOZE_TIME, SystemClock.elapsedRealtime());
		long clockTime = SystemClock.elapsedRealtime();
		Log.e("Snooze Time:", "" + snoozeTime);
		Log.e("Clock Time:", "" + clockTime);
		return snoozeTime - clockTime;
	}
}
