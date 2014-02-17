package marvick.phonealert;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallAlert extends BroadcastReceiver {
	private final String TAG = "PhoneAlert";
	//private final int MINUTES = 10;
	//private final int NUM_CALLS = 2;
	public final String SETTING_MODE = "mode";
	public final String SETTING_MODE_CHANGED = "modeChanged";
	public final String SETTING_CALL_QTY = "callQty";
	public final String SETTING_CALL_TIME = "callTime";

	@Override
	public void onReceive(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
			saveState(context);
			String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			int called = timesCalled(context, incomingNumber, prefs.getInt(SETTING_CALL_TIME, 15));
			if (called >= prefs.getInt(SETTING_CALL_QTY, 3) - 1) {
				alertAction(context);
			}
		} else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
			resetAction(context);
		}

	}
	
	private void resetAction(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		Toast.makeText(context,  ("" + prefs.getInt("mode", AudioManager.RINGER_MODE_NORMAL)),  Toast.LENGTH_LONG).show();
		if (prefs.getBoolean(SETTING_MODE_CHANGED, false)) {
			audio.setRingerMode(prefs.getInt("mode", AudioManager.RINGER_MODE_NORMAL));
			editor.putBoolean(SETTING_MODE_CHANGED, false);
			editor.commit();
		}
	}
	
	private void saveState(Context context) {
		AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();	
		editor.putInt(SETTING_MODE, audio.getRingerMode());
		editor.putBoolean(SETTING_MODE_CHANGED, true);
		editor.commit();
		Toast.makeText(context,  ("" + prefs.getInt("mode", AudioManager.RINGER_MODE_NORMAL)),  Toast.LENGTH_LONG).show();
	}
	
	private void alertAction(Context context) {
		AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		Log.d(TAG, "Ring mode: " + audio.getMode());
		audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		Log.d(TAG, "Ring mode: " + audio.getMode());
		Uri toneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		
		//TODO: Fix this bug -- sometimes the phone will ring twice, with an echo
		Ringtone tone = RingtoneManager.getRingtone(context, toneUri);
		if (audio.getMode() != AudioManager.MODE_RINGTONE && !tone.isPlaying()) {
			tone.play();
			Log.d(TAG, "Trigger a ring!");
		}
		Log.d(TAG, "Ring mode: " + audio.getMode());
	}
	
	private int timesCalled(Context context, String incomingNumber, int minutes) {
		String time = "" + ((new Date()).getTime() - minutes * 60 * 1000);
		String selection = CallLog.Calls.NUMBER + " = " + incomingNumber;
		selection += " AND " + CallLog.Calls.DATE + ">" + time;
		Cursor calls = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, null, null);
		calls.moveToFirst();
		int numCalls = 0;
		while (!calls.isAfterLast()) {
			calls.moveToNext();
			numCalls += 1;
		}
		return numCalls;
	}

}