package com.mmarvick.urgentcall.background;

import java.util.List;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.data.AlertCall;
import com.mmarvick.urgentcall.data.AlertText;
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
			
			boolean shouldAlert = false;
			boolean ring = false;
			boolean vibrate = false;
			int volume = 0;
			
			List<AlertText> textAlerts = AlertText.getAlerts(context);
			
			for (AlertText alert : textAlerts) {
				if (alert.shouldAlert(incomingNumber, message)) {
					shouldAlert = true;
					if (alert.getRing()) {
						ring = true;
						if (alert.getVolume() > volume) {
							volume = alert.getVolume();
						}
					}
					if (alert.getVibrate()) {
						vibrate = true;
					}
				}
			}
			
			if (shouldAlert) {
				alertAction(context);
			}
			
		}
	}
	
	
	private void alertAction(Context context) {
		Intent alarmService = new Intent(context, MessageAlarmService.class);
		context.startService(alarmService);	
	}
	
}