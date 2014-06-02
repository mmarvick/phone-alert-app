package com.mmarvick.urgentcall.background;

import com.mmarvick.urgentcall.Constants;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MessageAlarmService extends Service {
	private Ringtone tone;
	private AudioManager audio;
	private int streamVolumeInit;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		Uri toneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		tone = RingtoneManager.getRingtone(getApplicationContext(), toneUri);
		
		streamVolumeInit = audio.getStreamVolume(AudioManager.STREAM_ALARM);
		audio.setStreamVolume(AudioManager.STREAM_ALARM, audio.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);

		tone.setStreamType(AudioManager.STREAM_ALARM);
		tone.play();
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				stopSelf();
			}
		}, Constants.MSG_ALARM_TIME * 1000);
		
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		tone.stop();
		audio.setStreamVolume(AudioManager.STREAM_ALARM, streamVolumeInit, 0);
		Log.e("UrgentCall", "Self has stopped!");		
	}

}
