package com.mmarvick.urgentcall.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class AlarmServiceCall extends AlarmService {

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		super.onStartCommand(intent, flags, startid);

		if (ring) {
			audio.setStreamMute(AudioManager.STREAM_RING, true);
		}		

		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
        super.onDestroy();
		if (ring) {
			audio.setStreamMute(AudioManager.STREAM_RING, false);
		}
	}
}
