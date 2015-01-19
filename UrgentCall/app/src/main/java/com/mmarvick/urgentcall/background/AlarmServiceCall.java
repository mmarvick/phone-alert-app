package com.mmarvick.urgentcall.background;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;

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
}
