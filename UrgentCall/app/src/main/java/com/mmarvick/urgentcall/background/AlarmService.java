package com.mmarvick.urgentcall.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.mmarvick.urgentcall.R;
import com.mmarvick.urgentcall.helpers.SoundHelper;

/**
 * Created by michael on 1/19/15.
 */
public abstract class AlarmService extends Service {
    protected AudioManager mAudioManager;
    protected MediaPlayer mMediaPlayer;
    protected Vibrator mVibrator;
    protected int mInitAlarmVolume;
    protected int mInitRingVolume;

    protected int mVolume;
    protected Uri mToneUri;
    protected boolean mRing;
    protected boolean mVibrate;

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
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mInitAlarmVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        mInitRingVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        mMediaPlayer = new MediaPlayer();
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        startidStatic = startid;
        mRing = intent.getBooleanExtra(RING,  true);
        mVibrate = intent.getBooleanExtra(VIBRATE, true);
        mToneUri = (Uri) intent.getExtras().get(TONE);
        mVolume = intent.getIntExtra(VOLUME, 1000);

        if (mRing) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);

            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.reset();
            }

            try {
                mMediaPlayer.setVolume(getVolume(mVolume), getVolume(mVolume));
                mMediaPlayer.setDataSource(this, mToneUri);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (Exception e) {
                // TODO Can't play media
            }
        }

        if (mVibrate) {
            long[] pattern = {0, 100, 0};
            mVibrator.vibrate(pattern, 0);
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, mInitAlarmVolume, 0);
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mVibrator.cancel();
        stopSelf(startidStatic);
    }

    protected float getVolume(int volume) {
        float alertVolume = SoundHelper.getLogVolume(volume, getResources().getInteger(R.integer.volume_max));
        float ringVolume = SoundHelper.getLogVolume(mInitRingVolume, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        Log.e("UC", "Ring volume: " + ringVolume);
        return Math.max(alertVolume, ringVolume);
    }



}
