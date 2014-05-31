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
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class TextAlertBroadcastReceiver extends BroadcastReceiver {

	RulesDbHelper dbHelper;
	AudioManager audio;

	@Override
	public void onReceive(Context context, Intent intent) {
		dbHelper = new RulesDbHelper(context);
		audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		Bundle bundle = intent.getExtras();
		String message = "";
		String incomingNumber = "";
		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			for (int i = 0; i < pdus.length; i++) {
				message += SmsMessage.createFromPdu((byte[]) pdus[i]).getMessageBody().toString();
			}
			
			incomingNumber = SmsMessage.createFromPdu((byte[]) pdus[0]).getOriginatingAddress();
			
		}
			
		if (PrefHelper.getState(context, Constants.OVERALL_STATE) == RulesEntry.STATE_ON
				&& !PrefHelper.isSnoozing(context)) {
			Toast.makeText(context, "State is on!", Toast.LENGTH_SHORT).show();
			
			if (messageAlert(context, incomingNumber, message)) {
				alertAction(context);					
			}
			
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
	
	private boolean messageAlert(Context context, String incomingNumber, String message) {
		if (isOn(context, RulesEntry.MSG_STATE, incomingNumber)) {
			if (message.toLowerCase().contains(PrefHelper.getMessageToken(context).toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}
	
	private void alertAction(Context context) {
		Toast.makeText(context, "Text alert!", Toast.LENGTH_SHORT).show();
		Intent alarmService = new Intent(context, MessageAlarmService.class);
		context.startService(alarmService);	
	}
	
}