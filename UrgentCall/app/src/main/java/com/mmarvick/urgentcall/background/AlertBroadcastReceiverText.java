package com.mmarvick.urgentcall.background;

import java.util.List;

import com.mmarvick.urgentcall.data.base.Alert;
import com.mmarvick.urgentcall.data.text.TextAlert;

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
        TextAlert textAlert = (TextAlert) alert;
        if (textAlert.getAlertDuration() > duration) {
            duration = textAlert.getAlertDuration();
        }
    }

    protected List<? extends Alert> getAlerts() {
        return TextAlert.getAlerts(mContext);
    }

    protected boolean shouldAlert(Alert alert) {
        TextAlert textAlert = (TextAlert) alert;
        return textAlert.shouldAlert(phoneNumber, message);
    }

    protected Intent getAlarmServiceIntent() {
        Intent ringService = new Intent(mContext, AlarmServiceText.class);
        ringService.putExtra(AlarmServiceText.DURATION, duration);
        return ringService;
    }
	
}