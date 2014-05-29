package com.mmarvick.urgentcall.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

public class RingService extends Service {
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
		Uri toneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		tone = RingtoneManager.getRingtone(getApplicationContext(), toneUri);
		
		streamVolumeInit = audio.getStreamVolume(AudioManager.STREAM_ALARM);
		audio.setStreamVolume(AudioManager.STREAM_ALARM, audio.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);

		tone.setStreamType(AudioManager.STREAM_ALARM);
		tone.play();
		
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		tone.stop();
		audio.setStreamVolume(AudioManager.STREAM_ALARM, streamVolumeInit, 0);
	}

}
