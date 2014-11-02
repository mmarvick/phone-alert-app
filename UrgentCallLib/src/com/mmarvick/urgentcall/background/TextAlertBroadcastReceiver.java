package com.mmarvick.urgentcall.background;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.data.OldPrefHelper;
import com.mmarvick.urgentcall.data.OldDbContractDatabase.RulesEntryOld;
import com.mmarvick.urgentcall.data.OldRulesDbHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class TextAlertBroadcastReceiver extends BroadcastReceiver {

	OldRulesDbHelper dbHelper;
	AudioManager audio;

	@Override
	public void onReceive(Context context, Intent intent) {
		dbHelper = new OldRulesDbHelper(context);
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
			
		if (OldPrefHelper.getState(context, Constants.APP_STATE) == RulesEntryOld.STATE_ON
				&& !OldPrefHelper.isSnoozing(context)) {
			
			if (messageAlert(context, incomingNumber, message)) {
				alertAction(context);					
			}
			
		}
	}
	
	
	private boolean isOn(Context context, String alertType, String incomingNumber) {
		String lookup = dbHelper.getLookupFromNumber(incomingNumber);
		int urgentCallState = OldPrefHelper.getState(context, alertType);
		int userState = dbHelper.getUserState(alertType, lookup);
		
		switch(urgentCallState) {
		
		case Constants.URGENT_CALL_STATE_ON:
			return true;
		case Constants.URGENT_CALL_STATE_OFF:
			return false;
		case Constants.URGENT_CALL_STATE_WHITELIST:
			if (userState == RulesEntryOld.STATE_ON) {
				return true;
			} else {
				return false;
			}
		case Constants.URGENT_CALL_STATE_BLACKLIST:
			if (userState == RulesEntryOld.STATE_OFF) {
				return false;
			} else {
				return true;
			}		
		default:
			return true;
		}
	}
	
	private boolean messageAlert(Context context, String incomingNumber, String message) {
		if (isOn(context, RulesEntryOld.MSG_STATE, incomingNumber)) {
			if (message.toLowerCase().contains(OldPrefHelper.getMessageToken(context).toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}
	
	private void alertAction(Context context) {
		Intent alarmService = new Intent(context, MessageAlarmService.class);
		context.startService(alarmService);	
	}
	
}