package com.mmarvick.urgentcall.background;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

public class RingService extends Service {
	private AudioManager audio;
	private MediaPlayer media;
	private int streamVolumeInit;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		media = new MediaPlayer();
		Uri toneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
		
		streamVolumeInit = audio.getStreamVolume(AudioManager.STREAM_ALARM);
		audio.setStreamVolume(AudioManager.STREAM_ALARM, audio.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);

		try {
			media.setDataSource(this, toneUri);
			media.setAudioStreamType(AudioManager.STREAM_ALARM);
			media.setLooping(true);
			media.prepare();
			media.start();
		} catch (Exception e) {
			// TODO Can't play media
		}
		
		
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		media.stop();
		audio.setStreamVolume(AudioManager.STREAM_ALARM, streamVolumeInit, 0);
	}

}
