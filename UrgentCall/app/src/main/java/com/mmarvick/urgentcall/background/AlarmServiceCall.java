package com.mmarvick.urgentcall.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class AlarmServiceCall extends AlarmService {

	TelephonyManager telephonyManager;

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		super.onStartCommand(intent, flags, startid);
		
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new TeleListener(), PhoneStateListener.LISTEN_CALL_STATE);

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
	
	private class TeleListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String incomingNumber) {
			if (state == TelephonyManager.CALL_STATE_IDLE || state == TelephonyManager.CALL_STATE_OFFHOOK) {
				AlarmServiceCall.this.stopSelf();
			}
		}
	}


}
