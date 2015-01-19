package com.mmarvick.urgentcall.background;

import java.util.List;

import com.mmarvick.urgentcall.data.AlertText;
import com.mmarvick.urgentcall.helpers.PrefHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class TextAlertBroadcastReceiver extends BroadcastReceiver {

	AudioManager audio;

	@Override
	public void onReceive(Context context, Intent intent) {
		
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
			
		if (PrefHelper.getOnState(context)
				&& !PrefHelper.isSnoozing(context)) {
			
			boolean shouldAlert = false;
			boolean ring = false;
			boolean vibrate = false;
			Uri tone = null;
			int volume = 0;
			int duration = 0;
			
			List<AlertText> textAlerts = AlertText.getAlerts(context);
			
			for (AlertText alert : textAlerts) {
				if (alert.shouldAlert(incomingNumber, message)) {
					shouldAlert = true;
					if (alert.getRing()) {
						ring = true;
						if (alert.getVolume() > volume) {
							volume = alert.getVolume();
							tone = alert.getTone();
						}
					}
					if (alert.getVibrate()) {
						vibrate = true;
					}
					if (alert.getAlertDuration() > duration) {
						duration = alert.getAlertDuration();
					}
				}
			}
			
			if (shouldAlert) {
				Intent alarmService = new Intent(context, AlarmServiceText.class);
				alarmService.putExtra(AlarmServiceText.RING, ring);
				alarmService.putExtra(AlarmServiceText.VIBRATE, vibrate);
				alarmService.putExtra(AlarmServiceText.TONE, tone);
				alarmService.putExtra(AlarmServiceText.VOLUME, volume);
				alarmService.putExtra(AlarmServiceText.DURATION, duration);
				context.startService(alarmService);				
			}
			
		}
	}
	
}