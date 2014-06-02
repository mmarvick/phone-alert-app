package com.mmarvick.urgentcall.background;

import java.util.Date;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;
import com.mmarvick.urgentcall.data.RulesDbHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

public class CallAlertBroadcastReceiver extends BroadcastReceiver {

	RulesDbHelper dbHelper;
	AudioManager audio;

	@Override
	public void onReceive(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		dbHelper = new RulesDbHelper(context);
		audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
			String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
			
			if (PrefHelper.getState(context, Constants.OVERALL_STATE) == RulesEntry.STATE_ON
					&& !PrefHelper.isSnoozing(context)) {
			
				if (repeatedAlert(context, incomingNumber) || singleAlert(context, incomingNumber)) {
					alertAction(context);					
				}
			
			}
		} else if (TelephonyManager.EXTRA_STATE_IDLE.equals(state) || TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
			resetAction(context);
		}
	}
	
	private boolean repeatedAlert(Context context, String incomingNumber) {
		if (isOn(context, RulesEntry.RC_STATE, incomingNumber)) {
			int allowedMins = PrefHelper.getRepeatedCallMins(context);
			int allowedCalls = PrefHelper.getRepeatedCallQty(context);;
			int called = timesCalled(context, incomingNumber, allowedMins);
			//TODO: this is buggy
			if (called >= allowedCalls - 1) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean singleAlert(Context context, String incomingNumber) {
		if (isOn(context, RulesEntry.SC_STATE, incomingNumber)) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isOn(Context context, String alertType, String incomingNumber) {
		String lookup = dbHelper.getLookupFromNumber(incomingNumber);
		int urgentCallState = PrefHelper.getState(context, alertType);
		int userState = dbHelper.getUserState(alertType, lookup);
		
		switch(urgentCallState) {
		
		case Constants.URGENT_CALL_STATE_ON:
			return true;
		case Constants.URGENT_CALL_STATE_OFF:
			return false;
		case Constants.URGENT_CALL_STATE_WHITELIST:
			if (userState == RulesEntry.STATE_ON) {
				return true;
			} else {
				return false;
			}
		case Constants.URGENT_CALL_STATE_BLACKLIST:
			if (userState == RulesEntry.STATE_OFF) {
				return false;
			} else {
				return true;
			}		
		default:
			return true;
		}
	}
	
	private int timesCalled(Context context, String incomingNumber, int minutes) {
		String time = "" + ((new Date()).getTime() - minutes * 60 * 1000);
		String selection = CallLog.Calls.NUMBER + " = ?";
		selection += " AND " + CallLog.Calls.DATE + "> ?";
		String[] selectors = {incomingNumber, time};
		Cursor calls = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, selection, selectors, null);
		calls.moveToFirst();
		int numCalls = 0;
		while (!calls.isAfterLast()) {
			calls.moveToNext();
			numCalls += 1;
		}
		return numCalls;
	}	
	
	private void alertAction(Context context) {
		if (audio.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			PrefHelper.saveCurrentPhoneState(context, audio);
			audio.setStreamVolume(AudioManager.STREAM_RING, audio.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
		} else {
			Intent ringService = new Intent(context, RingService.class);
			context.startService(ringService);			
		}
	}	
	
	private void resetAction(Context context) {
		Intent ringService = new Intent(context, RingService.class);
		context.stopService(ringService);
		PrefHelper.resetSavedPhoneState(context, audio);
	}
	
}