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
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Telephony;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class RingService extends Service {
	private AudioManager audio;
	private MediaPlayer media;
	private Vibrator vibrator;
	private int streamVolumeInit;
	
	public static int startidStatic;
	
	TelephonyManager telephonyManager;
	
	private int volume;
	private Uri toneUri;
	private boolean ring;
	private boolean vibrate;
	
	public static final String RING = "RING";
	public static final String VIBRATE = "VIBRATE";
	public static final String TONE = "TONE";
	public static final String VOLUME = "VOLUME";

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
		startidStatic = startid;
		
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(new TeleListener(), PhoneStateListener.LISTEN_CALL_STATE);
		
		ring = intent.getBooleanExtra(RING,  true);
		vibrate = intent.getBooleanExtra(VIBRATE, true);
		toneUri = (Uri) intent.getExtras().get(TONE);
		volume = intent.getIntExtra(VOLUME, 1000);

		if (ring) {
			
			
			audio.setStreamMute(AudioManager.STREAM_RING, true);
			
			
			audio.setStreamVolume(AudioManager.STREAM_ALARM, audio.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
			
			
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
			long[] pattern = {0, 100, 0};
			vibrator.vibrate(pattern, 0);		
		}
		
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		if (ring) {
			audio.setStreamMute(AudioManager.STREAM_RING, false);
			audio.setStreamVolume(AudioManager.STREAM_ALARM, streamVolumeInit, 0);
			media.stop();
		}
		
		if (vibrate) {
			vibrator.cancel();
		}
		
		stopSelf(startidStatic);
	}	
	
	private float getVolume(int volume) {
		return (float) (1 - (Math.log(Constants.ALERT_VOLUME_MAX - volume) / Math.log(Constants.ALERT_VOLUME_MAX)));
	}
	
	private class TeleListener extends PhoneStateListener {
		public void onCallStateChanged(int state, String incomingNumber) {
			if (state == TelephonyManager.CALL_STATE_IDLE || state == TelephonyManager.CALL_STATE_OFFHOOK) {
				RingService.this.stopSelf();
			}
		}
	}


}
