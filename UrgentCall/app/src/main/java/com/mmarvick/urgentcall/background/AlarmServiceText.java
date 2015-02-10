package com.mmarvick.urgentcall.background;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.activities.TextAlertNotificationActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

public class AlarmServiceText extends AlarmService {
	private int actualDuration;
	public static final String DURATION = "DURATION";

    @Override
    public void onCreate() {
        super.onCreate();
    }

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
        super.onStartCommand(intent, flags, startid);;

		actualDuration = 1000 * intent.getIntExtra(DURATION,
                getResources().getInteger(R.integer.message_alert_time_default));

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, TextAlertNotificationActivity.class), 0);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setFullScreenIntent(pendingIntent, true);
        notificationBuilder.setContentTitle("An urgent message!");
        notificationBuilder.setContentText("Testing this");
        notificationBuilder.setSmallIcon(R.drawable.ic_action_call);
        notificationManager.notify(0, notificationBuilder.build());
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				stopSelf(startidStatic);
			}
		}, actualDuration);
		
		return Service.START_STICKY;
	}
	
}
