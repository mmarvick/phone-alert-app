package com.mmarvick.urgentcall.helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;

import com.crashlytics.android.Crashlytics;
import com.mmarvick.urgentcall.R;

public class SoundHelper {
    public static void beep(Context context, int volume) {
        ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_ALARM, volume/10);
        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
    }

    public static float getLogVolume(int actualVolume, int maxVolume) {
        int numOfIncrements = maxVolume + 1;
        return (float) (1 - (Math.log(numOfIncrements - actualVolume) / Math.log(numOfIncrements)));
    }
}
