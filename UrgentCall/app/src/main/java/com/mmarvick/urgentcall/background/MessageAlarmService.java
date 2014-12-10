package com.mmarvick.urgentcall.background;

import com.mmarvick.urgentcall.Constants;
import com.mmarvick.urgentcall.data.PrefHelper;
import com.mmarvick.urgentcall.data.OldDbContractDatabase.RulesEntryOld;

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
	private Vibrator vibrator;
	private int streamVolumeInit;

	private int volume;
	private int actualDuration;
	private Uri toneUri;
	private boolean ring;
	private boolean vibrate;
	
	public static final String RING = "RING";
	public static final String VIBRATE = "VIBRATE";
	public static final String TONE = "TONE";
	public static final String VOLUME = "VOLUME";
	public static final String DURATION = "DURATION";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
		streamVolumeInit = audio.getStreamVolume(AudioManager.STREAM_ALARM);
		media = new MediaPlayer();
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		final int startidStatic = startid;
		
		ring = intent.getBooleanExtra(RING,  true);
		vibrate = intent.getBooleanExtra(VIBRATE, true);
		toneUri = (Uri) intent.getExtras().get(TONE);
		volume = intent.getIntExtra(VOLUME, 1000);
		actualDuration = 1000 * intent.getIntExtra(DURATION, 10);
		
		if (ring) {
			audio.setStreamVolume(AudioManager.STREAM_ALARM, audio.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
			
			if (media.isPlaying()) {
				media.reset();
			}
	
			try {
				media.setVolume(getVolume(volume), getVolume(volume));
				media.setDataSource(this, toneUri);
				media.setAudioStreamType(AudioManager.STREAM_ALARM);
				media.setLooping(true);
				media.prepare();
				media.start();
			} catch (Exception e) {
				// TODO Can't play media
			}
		}
		
		if (vibrate) {
			vibrator.vibrate(actualDuration);
		}
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				stopSelf(startidStatic);
			}
		}, actualDuration);
		
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		media.stop();
		audio.setStreamVolume(AudioManager.STREAM_ALARM, streamVolumeInit, 0);	//TODO: Do we need to do this? If the stream is muted but the mediaplayer is not, what happens?
	}

	private float getVolume(int volume) {
		return (float) (1 - (Math.log(Constants.ALERT_VOLUME_MAX - volume) / Math.log(Constants.ALERT_VOLUME_MAX)));
	}	
	
}
