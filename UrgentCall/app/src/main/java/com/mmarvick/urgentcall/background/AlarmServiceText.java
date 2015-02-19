package com.mmarvick.urgentcall.background;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import com.mmarvick.urgentcall.R;

import java.util.ArrayList;
import java.util.List;

public class AlarmServiceText extends AlarmService {
	private int actualDuration;
    private List<Integer> arrayIds;
	public static final String DURATION = "DURATION";
    public static final String EXTRA_CANCEL_ALERTS = "CANCEL_ALERTS";

    @Override
    public void onCreate() {
        super.onCreate();
        arrayIds = new ArrayList<>();
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
        if (intent.getBooleanExtra(EXTRA_CANCEL_ALERTS, false)) {
            stopSelf();
            return Service.START_STICKY;
        }

        super.onStartCommand(intent, flags, startid);

        //TODO: Include references to who's texting
        arrayIds.add(0);

		actualDuration = 1000 * intent.getIntExtra(DURATION,
                getResources().getInteger(R.integer.message_alert_time_default));

        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, TextAlertNotificationActivity.class), 0);

        Intent cancelAlertIntent = new Intent(this, AlarmServiceText.class);
        cancelAlertIntent.putExtra(EXTRA_CANCEL_ALERTS, true);
        cancelAlertIntent.setAction("random"); // this is needed to pass the extra
        PendingIntent cancelAlertPendingIntent = PendingIntent.getService(this, 0, cancelAlertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        //notificationBuilder.setFullScreenIntent(pendingIntent, true);
        notificationBuilder.setContentTitle("Urgent Text " + getResources().getQuantityString(R.plurals.plural_alert_capialized, arrayIds.size()) + " incoming");
        notificationBuilder.setContentText(arrayIds.size() + " " + getResources().getQuantityString(R.plurals.plural_text, arrayIds.size()));
        notificationBuilder.setOngoing(true);
        notificationBuilder.setSmallIcon(R.drawable.ic_notify);
        notificationBuilder.addAction(R.drawable.ic_action_discard, "Cancel Alert", cancelAlertPendingIntent);
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        mNotificationManager.notify(ALARM_ID_TEXT_ONGING, notificationBuilder.build());

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
        mNotificationManager.cancel(ALARM_ID_TEXT_ONGING);
        arrayIds.clear();
        super.onDestroy();
    }
	
}
