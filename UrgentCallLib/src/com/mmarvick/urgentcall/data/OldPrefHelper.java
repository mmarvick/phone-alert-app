package com.mmarvick.urgentcall.data;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.data.OldDbContractDatabase.RulesEntryOld;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class OldPrefHelper {

	public static int getState(Context context, String alertType) {
		int def = Constants.URGENT_CALL_STATE_ON;
		if (alertType == RulesEntryOld.SC_STATE) {
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
		editor.putInt(alertType + Constants.ALERT_BACKUP, getState(context, alertType));
		editor.commit();
	}
	
	public static int getBackupState(Context context, String alertType) {
		int def = Constants.URGENT_CALL_STATE_ON;
		if (alertType == RulesEntryOld.SC_STATE) {
			def = Constants.URGENT_CALL_STATE_WHITELIST;
		}
		return getPrefs(context).getInt(alertType + Constants.ALERT_BACKUP, def);		
	}
	
	public static Uri getMessageSound(Context context, String alertType) {
		String def = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE).toString();
		if (alertType == RulesEntryOld.MSG_STATE) {
			def = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();;
		}
		return Uri.parse(getPrefs(context).getString(alertType + Constants.ALERT_SOUND, def));
	}
	
	public static void setMessageSound(Context context, String alertType, Uri sound) {
		Editor editor = getPrefs(context).edit();
		editor.putString(alertType + Constants.ALERT_SOUND, sound.toString());
		editor.commit();
	}	
	
	public static void setMessageHow(Context context, String alertType, String how) {
		Editor editor = getPrefs(context).edit();
		editor.putString(alertType + Constants.ALERT_HOW, how);
		editor.commit();
	}		
	
	public static String getMessageVolumePercent(Context context, String alertType) {
		int volume = getPrefs(context).getInt(alertType + Constants.ALERT_VOLUME, Constants.ALERT_VOLUME_DEFAULT);
		int percent = volume * 100 / Constants.ALERT_VOLUME_DEFAULT;
		if (volume > 0 && percent == 0) {
			percent = 1;
		}
		return percent + "%";
	}
	
	public static float getMessageVolumeValue(Context context, String alertType) {
		int volume = getPrefs(context).getInt(alertType + Constants.ALERT_VOLUME, Constants.ALERT_VOLUME_DEFAULT);
		return (float) (1 - (Math.log(Constants.ALERT_VOLUME_MAX - volume) / Math.log(Constants.ALERT_VOLUME_MAX)));
	}
	
	public static String getMessageHow(Context context, String alertType) {
		return getPrefs(context).getString(alertType + Constants.ALERT_HOW, Constants.ALERT_HOW_DEFAULT);			
	}	
	
	public static int getMessageTime(Context context, String alertType) {
		return getPrefs(context).getInt(alertType + Constants.ALERT_TIME, 10);			
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
		return getIntValue(context, Constants.CALL_QTY, Constants.CALL_QTY_DEFAULT);
		
	}
	
	public static int getRepeatedCallMins(Context context) {
		return getIntValue(context, Constants.CALL_MIN, Constants.CALL_MIN_DEFAULT);
	}	
	
	public static int getIntValue(Context context, String name, int def) {
		return getPrefs(context).getInt(name, def);
	}	
	
	public static void setIntValue(Context context, String name, int value) {
		Editor editor = getPrefs(context).edit();
		editor.putInt(name, value);
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
