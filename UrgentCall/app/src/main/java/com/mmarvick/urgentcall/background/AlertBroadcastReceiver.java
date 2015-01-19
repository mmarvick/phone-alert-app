package com.mmarvick.urgentcall.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.mmarvick.urgentcall.data.Alert;
import com.mmarvick.urgentcall.helpers.DisclaimerHelper;
import com.mmarvick.urgentcall.helpers.PrefHelper;

import java.util.List;

/**
 * Created by michael on 1/19/15.
 */
public abstract class AlertBroadcastReceiver extends BroadcastReceiver {
    protected Context mContext;
    protected String phoneNumber;
    protected boolean ring = false;
    protected boolean vibrate = false;
    protected Uri tone = null;
    protected int volume = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
    }

    protected void checkAlerts() {
        if (PrefHelper.getOnState(mContext)
                && !PrefHelper.isSnoozing(mContext)
                && DisclaimerHelper.disclaimerIsApproved(mContext)) {

            List<? extends Alert> alerts = getAlerts();

            for (Alert alert : alerts) {
                if (shouldAlert(alert)) {
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
                    noteAlertSpecificProperties(alert);
                }
            }

            if (ring || vibrate) {
                alert();
            }
        }
    }

    protected void alert() {
        Intent alertService = getAlarmServiceIntent();
        alertService.putExtra(AlarmServiceCall.RING, ring);
        alertService.putExtra(AlarmServiceCall.VIBRATE, vibrate);
        alertService.putExtra(AlarmServiceCall.TONE, tone);
        alertService.putExtra(AlarmServiceCall.VOLUME, volume);
        mContext.startService(alertService);
    }

    protected abstract void noteAlertSpecificProperties(Alert alert);
    protected abstract List<? extends Alert> getAlerts();
    protected abstract boolean shouldAlert(Alert alert);
    protected abstract Intent getAlarmServiceIntent();

}
