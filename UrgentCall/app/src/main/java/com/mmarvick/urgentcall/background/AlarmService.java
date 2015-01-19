package com.mmarvick.urgentcall.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;

import com.mmarvick.urgentcall.R;

/**
 * Created by michael on 1/19/15.
 */
public class AlarmService extends Service {
    protected AudioManager audio;
    protected MediaPlayer media;
    protected Vibrator vibrator;
    protected int streamVolumeInit;

    protected int volume;
    protected Uri toneUri;
    protected boolean ring;
    protected boolean vibrate;

    public static final String RING = "RING";
    public static final String VIBRATE = "VIBRATE";
    public static final String TONE = "TONE";
    public static final String VOLUME = "VOLUME";

    protected static int startidStatic;

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
        ring = intent.getBooleanExtra(RING,  true);
        vibrate = intent.getBooleanExtra(VIBRATE, true);
        toneUri = (Uri) intent.getExtras().get(TONE);
        volume = intent.getIntExtra(VOLUME, 1000);

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
            long[] pattern = {0, 100, 0};
            vibrator.vibrate(pattern, 0);
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        audio.setStreamVolume(AudioManager.STREAM_ALARM, streamVolumeInit, 0);
        media.stop();
        media.release();
        vibrator.cancel();
        stopSelf(startidStatic);
    }

    protected float getVolume(int volume) {
        int maxVolume = getResources().getInteger(R.integer.volume_max);
        return (float) (1 - (Math.log(maxVolume - volume) / Math.log(maxVolume)));
    }

}
