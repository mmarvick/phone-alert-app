package com.mmarvick.urgentcall.background;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;

import com.mmarvick.urgentcall.R;

public class AlarmServiceCall extends AlarmService {

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		super.onStartCommand(intent, flags, startid);
		if (mRing) {
			mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
		}		

		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		if (mRing) {
			mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
		}
        super.onDestroy();
	}

    @Override
    protected String getNotificationTitle() {
        return getString(R.string.notification_call_title);
    }

    @Override
    protected String getNotificationText() {
        return getString(R.string.notification_call_text);
    }

    @Override
    protected int getNotificationId() {
        return NOTIFICATION_ID_CALL_AFTER;
    }
}
