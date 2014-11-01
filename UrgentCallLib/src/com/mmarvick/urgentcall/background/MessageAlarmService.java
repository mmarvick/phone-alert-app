package com.mmarvick.urgentcall.background;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.DbContractOldDatabase.RulesEntryOld;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;

public class MessageAlarmService extends Service {
	private AudioManager audio;
	private MediaPlayer media;
	private int streamVolumeInit;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		streamVolumeInit = audio.getStreamVolume(AudioManager.STREAM_ALARM);
		media = new MediaPlayer();
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		final int startidStatic = startid;
		
		String alertHow = PrefHelper.getMessageHow(getApplicationContext(), RulesEntryOld.MSG_STATE);
		long time = PrefHelper.getMessageTime(getApplicationContext(), RulesEntryOld.MSG_STATE) * 1000;
		
		if (alertHow.equals(Constants.ALERT_HOW_RING) || alertHow.equals(Constants.ALERT_HOW_RING_AND_VIBE)) {
			Uri toneUri = PrefHelper.getMessageSound(getApplicationContext(), RulesEntryOld.MSG_STATE);
			audio.setStreamVolume(AudioManager.STREAM_ALARM, audio.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
			
			float targetVolume = (PrefHelper.getMessageVolumeValue(getApplicationContext(), RulesEntryOld.MSG_STATE)); //* audio.getStreamMaxVolume(AudioManager.STREAM_ALARM));
			
			if (media.isPlaying()) {
				media.reset();
			}
	
			try {
				media.setVolume(targetVolume, targetVolume);
				media.setDataSource(this, toneUri);
				media.setAudioStreamType(AudioManager.STREAM_ALARM);
				media.setLooping(true);
				media.prepare();
				media.start();
			} catch (Exception e) {
				// TODO Can't play media
			}
		}
		
		if (alertHow.equals(Constants.ALERT_HOW_VIBE)|| alertHow.equals(Constants.ALERT_HOW_RING_AND_VIBE)) {
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(time);
		}
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				stopSelf(startidStatic);
			}
		}, time);
		
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		media.stop();
		audio.setStreamVolume(AudioManager.STREAM_ALARM, streamVolumeInit, 0);	//TODO: Do we need to do this? If the stream is muted but the mediaplayer is not, what happens?
	}

}
