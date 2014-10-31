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
import android.util.Log;

public class CallAlertBroadcastReceiver extends BroadcastReceiver {

	RulesDbHelper dbHelper;
	AudioManager audio;

	@Override
	public void onReceive(Context context, Intent intent) {
		String callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		dbHelper = new RulesDbHelper(context);
		audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		Log.e("UC", "SOMETHING HAPPENED WITH THE PHONE");
		
		// If the phone is ringing...
		if (callState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
			
			String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER); // phone number
			
			// ... and the app isn't snoozing or turne off ...
			if (PrefHelper.getState(context, Constants.APP_STATE) == RulesEntry.STATE_ON
					&& !PrefHelper.isSnoozing(context)) {
			
				// ... and either a repeated call alert or single call alert criteria has been met ...
				// ... trigger an alert!
				if (repeatedAlert(context, incomingNumber) && singleAlert(context, incomingNumber)) {
					alertAction(context, Constants.CALL_ALERT_TYPE_BOTH);					
				} else if (repeatedAlert(context, incomingNumber)) {
					alertAction(context, Constants.CALL_ALERT_TYPE_RC);	
				} else if (singleAlert(context, incomingNumber)) {
					alertAction(context, Constants.CALL_ALERT_TYPE_SC);	
				}
			
			}
		} 
	}

	private boolean singleAlert(Context context, String incomingNumber) {
		if (isOn(context, RulesEntry.SC_STATE, incomingNumber)) {
			return true;
		} else {
			return false;
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
	
	// Returns whether an alert is turned on for the given alert type and phone number
	private boolean isOn(Context context, String alertType, String incomingNumber) {
		String lookup = dbHelper.getLookupFromNumber(incomingNumber); // gets lookup for caller
		int alertState = PrefHelper.getState(context, alertType); // gets the phone's setting for this alert
		int userState = dbHelper.getUserState(alertType, lookup); // gets the user's setting for this alert (if whitelist/blacklist)
		
		switch(alertState) {
		
		case Constants.URGENT_CALL_STATE_ON:
			return true;
		case Constants.URGENT_CALL_STATE_OFF:
			return false;
		case Constants.URGENT_CALL_STATE_WHITELIST:
			// Alert is only on if the user state is explicitly on for that alert
			if (userState == RulesEntry.STATE_ON) {
				return true;
			} else {
				return false;
			}
		case Constants.URGENT_CALL_STATE_BLACKLIST:
			// Alert is only off if the user state is explicitly off for that alert
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
	
	private void alertAction(Context context, int alertType) {
		Intent ringService = new Intent(context, RingService.class);
		ringService.putExtra(Constants.ALERT_TYPE, alertType);
		context.startService(ringService);			
	}	
	
}