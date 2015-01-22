package com.mmarvick.urgentcall.background;

import com.mmarvick.urgentcall.R;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;

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
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				stopSelf(startidStatic);
			}
		}, actualDuration);
		
		return Service.START_STICKY;
	}
	
}
