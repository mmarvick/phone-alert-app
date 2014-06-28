package com.mmarvick.urgentcall.background;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.RulesDbContract.RulesEntry;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class MessageAlarmService extends Service {
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
		Uri toneUri = PrefHelper.getMessageSound(getApplicationContext(), RulesEntry.MSG_STATE);
		
		streamVolumeInit = audio.getStreamVolume(AudioManager.STREAM_ALARM);
		audio.setStreamVolume(AudioManager.STREAM_ALARM, audio.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
		
		float targetVolume = (PrefHelper.getMessageVolumeValue(getApplicationContext(), RulesEntry.MSG_STATE)); //* audio.getStreamMaxVolume(AudioManager.STREAM_ALARM));
		media.setVolume(targetVolume, targetVolume);
		

		try {
			media.setDataSource(this, toneUri);
			media.setAudioStreamType(AudioManager.STREAM_ALARM);
			media.setLooping(true);
			media.prepare();
			media.start();
		} catch (Exception e) {
			// TODO Can't play media
		}
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				stopSelf();
			}
		}, PrefHelper.getMessageTime(getApplicationContext(), RulesEntry.MSG_STATE) * 1000);
		
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		media.stop();
		audio.setStreamVolume(AudioManager.STREAM_ALARM, streamVolumeInit, 0);	//TODO: Do we need to do this? If the stream is muted but the mediaplayer is not, what happens?
	}

}
