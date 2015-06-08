package com.mmarvick.urgentcall.background;

import java.util.List;

import com.mmarvick.urgentcall.data.base.Alert;
import com.mmarvick.urgentcall.data.call.CallAlert;
import com.mmarvick.urgentcall.data.call.CallAlertStore;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AlertBroadcastReceiverCall extends AlertBroadcastReceiver {
    private static String mLastState;

	@Override
	public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

		String callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Log.i("UC", "mLastState: " + mLastState);
        Log.i("UC", "callState: " + callState);
        if (!callState.equals(mLastState)) {
            mLastState = callState;
            if (callState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER); // phone number
                checkAlerts();
            } else {
                Intent ringService = getAlarmServiceIntent();
                mContext.stopService(ringService);
            }
        }
    }

    @Override
    protected void noteAlertSpecificProperties(Alert alert) {}

    @Override
    protected List<? extends Alert> getAlerts() {
        CallAlertStore alertStore = CallAlertStore.getInstance(mContext);
        return alertStore.getAlerts();
    }

    @Override
    protected boolean shouldAlert(Alert alert) {
        CallAlert callAlert = (CallAlert) alert;
        return callAlert.shouldAlert(mContext, phoneNumber);
    }

    @Override
    protected Intent getAlarmServiceIntent() {
        Intent ringService = new Intent(mContext, AlarmServiceCall.class);
        return ringService;
    }
	
}