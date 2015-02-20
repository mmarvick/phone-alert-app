package com.mmarvick.urgentcall.background;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.mmarvick.urgentcall.R;

import java.util.ArrayList;
import java.util.List;

public class AlarmServiceText extends AlarmService {
	private int actualDuration;
    private List<Integer> arrayIds;
	public static final String DURATION = "DURATION";
    public static final String ACTION_CANCEL_ALERTS = "CANCEL_ALERTS";

    @Override
    public void onCreate() {
        super.onCreate();
        arrayIds = new ArrayList<>();
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
        if (TextUtils.equals(intent.getAction(), ACTION_CANCEL_ALERTS)) {
            stopSelf();
            return Service.START_STICKY;
        }

        super.onStartCommand(intent, flags, startid);

        //TODO: Include references to who's texting
        arrayIds.add(0);

		actualDuration = 1000 * intent.getIntExtra(DURATION,
                getResources().getInteger(R.integer.message_alert_time_default));

        Intent cancelAlertIntent = new Intent(this, AlarmServiceText.class);
        cancelAlertIntent.setAction(ACTION_CANCEL_ALERTS);
        PendingIntent cancelAlertPendingIntent = PendingIntent.getService(this, 0, cancelAlertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle("Urgent Text " + getResources().getQuantityString(R.plurals.plural_alert_capialized, arrayIds.size()) + " incoming");
        notificationBuilder.setContentText(arrayIds.size() + " " + getResources().getQuantityString(R.plurals.plural_text, arrayIds.size()));
        notificationBuilder.setOngoing(false);
        notificationBuilder.setSmallIcon(R.drawable.ic_notify);
        notificationBuilder.setDeleteIntent(cancelAlertPendingIntent);
        notificationBuilder.addAction(R.drawable.ic_action_cancel, getResources().getString(R.string.notification_alert_stop), cancelAlertPendingIntent);
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        mNotificationManager.notify(NOTIFICATION_ID_TEXT_ONGING, notificationBuilder.build());

        final int startidFinal = startid;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
                stopSelf(startidFinal);
			}
		}, actualDuration);
		
		return Service.START_STICKY;
	}

    @Override
    public void onDestroy() {
        mNotificationManager.cancel(NOTIFICATION_ID_TEXT_ONGING);
        arrayIds.clear();
        super.onDestroy();
    }

    @Override
    protected String getNotificationTitle() {
        return getString(R.string.notification_text_title);
    }

    @Override
    protected String getNotificationText() {
        return getString(R.string.notification_text_text);
    }

    @Override
    protected int getNotificationId() {
        return NOTIFICATION_ID_TEXT_AFTER;
    }

}
