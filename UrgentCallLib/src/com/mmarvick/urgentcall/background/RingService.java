package com.mmarvick.urgentcall.background;

import java.io.IOException;

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
import android.os.IBinder;
import android.os.Vibrator;

public class RingService extends Service {
	private AudioManager audio;
	private MediaPlayer media;
	private Vibrator vibrator;
	private int streamVolumeInit;
	
	private float volume;
	private Uri toneUri;
	private boolean ring;
	private boolean vibrate;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startid) {
		int alertType = intent.getIntExtra(Constants.ALERT_TYPE, Constants.CALL_ALERT_TYPE_BOTH);
		
		ring = false;
		vibrate = false;
		
		float rcVolume = PrefHelper.getMessageVolumeValue(getApplicationContext(), RulesEntry.RC_STATE);
		float scVolume = PrefHelper.getMessageVolumeValue(getApplicationContext(), RulesEntry.SC_STATE);
		Uri rcToneUri = PrefHelper.getMessageSound(getApplicationContext(), RulesEntry.RC_STATE);
		Uri scToneUri = PrefHelper.getMessageSound(getApplicationContext(), RulesEntry.SC_STATE);
		
		if (alertType == Constants.CALL_ALERT_TYPE_RC) {
			volume = rcVolume;
			setHowToAlert(RulesEntry.RC_STATE);
			
		} else if (alertType == Constants.CALL_ALERT_TYPE_SC) {
			volume = scVolume;
			setHowToAlert(RulesEntry.SC_STATE);
		} else {
			
			if (rcVolume >= scVolume) {
				volume = rcVolume;				
			} else {
				volume = scVolume;			
			}
			
			setHowToAlert(RulesEntry.SC_STATE);
			setHowToAlert(RulesEntry.RC_STATE);			
		}

		if (ring) {
			audio = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
			
			audio.setStreamMute(AudioManager.STREAM_RING, true);
			
			streamVolumeInit = audio.getStreamVolume(AudioManager.STREAM_ALARM);
			audio.setStreamVolume(AudioManager.STREAM_ALARM, audio.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
			
			media = new MediaPlayer();
			try {
				media.setVolume(volume, volume);
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
			vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
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
	}	
	
	public void setHowToAlert(String alertType) {
		String howToAlert = PrefHelper.getMessageHow(getApplicationContext(), alertType);
		
		if (howToAlert.equals(Constants.ALERT_HOW_RING) || howToAlert.equals(Constants.ALERT_HOW_RING_AND_VIBE)) {
			ring = true;
			toneUri = PrefHelper.getMessageSound(getApplicationContext(), alertType);
		}
		
		if (howToAlert.equals(Constants.ALERT_HOW_VIBE) || howToAlert.equals(Constants.ALERT_HOW_RING_AND_VIBE)) {
			vibrate = true;
		}		
	}
	


}
