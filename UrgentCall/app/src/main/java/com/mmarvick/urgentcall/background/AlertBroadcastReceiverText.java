package com.mmarvick.urgentcall.background;

import java.util.List;

import com.mmarvick.urgentcall.data.Alert;
import com.mmarvick.urgentcall.data.AlertText;

import android.content.Context;
import android.content.Intent;;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class AlertBroadcastReceiverText extends AlertBroadcastReceiver {
    private int duration;
    private String message;

	@Override
	public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
		
		Bundle bundle = intent.getExtras();
		message = "";
		if (bundle != null) {
			Object[] pdus = (Object[]) bundle.get("pdus");
			for (int i = 0; i < pdus.length; i++) {
				message += SmsMessage.createFromPdu((byte[]) pdus[i]).getMessageBody().toString();
			}
			
			phoneNumber = SmsMessage.createFromPdu((byte[]) pdus[0]).getOriginatingAddress();
			
		}

        checkAlerts();
	}

    protected void noteAlertSpecificProperties(Alert alert) {
        AlertText alertText = (AlertText) alert;
        if (alertText.getAlertDuration() > duration) {
            duration = alertText.getAlertDuration();
        }
    }

    protected List<? extends Alert> getAlerts() {
        return AlertText.getAlerts(mContext);
    }

    protected boolean shouldAlert(Alert alert) {
        AlertText alertText = (AlertText) alert;
        return alertText.shouldAlert(phoneNumber, message);
    }

    protected Intent getAlarmServiceIntent() {
        Intent ringService = new Intent(mContext, AlarmServiceText.class);
        ringService.putExtra(AlarmServiceText.DURATION, duration);
        return ringService;
    }
	
}