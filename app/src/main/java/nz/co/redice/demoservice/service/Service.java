package nz.co.redice.demoservice.service;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import nz.co.redice.demoservice.utils.PermissionHelper;
import nz.co.redice.demoservice.utils.PrefHelper;

@AndroidEntryPoint
public class Service extends android.app.Service {

    private AudioManager mAudioManager;
    @Inject PrefHelper mPrefHelper;
    @Inject PermissionHelper mPermissionHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPermissionHelper.getDNDPermission();
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        muteAllStreams();
    }

    void adjustSoundStreams() {
        if (!mPrefHelper.getMuteState()
                //current time in milliseconds bigger or equal to sleep time
                // but smaller than wakeup time
                && getCurrentTime() >= mPrefHelper.getSleepTime()
                && getCurrentTime() <= mPrefHelper.getWakeUpTime()
        ) {
            muteAllStreams();
        } else {
            unMuteAllStreams();
        }
    }

    private void muteAllStreams() {
        // Check for DND permissions for API 24+
//        mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
//        mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
//        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
//        mAudioManager.setStreamMute(AudioManager.STREAM_RING, true);
//        mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
//        mAudioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, true);
//        mAudioManager.setStreamMute(AudioManager.STREAM_DTMF, true);
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        //setting mute pref on
        mPrefHelper.addMuteState(true);
        Log.d("App", "setRingerMode: SILENT");
    }

    private void unMuteAllStreams() {
        // Check for DND permissions for API 24+
//        mAudioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
//        mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, false);
//        mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
//        mAudioManager.setStreamMute(AudioManager.STREAM_RING, false);
//        mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
//        mAudioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, false);
//        mAudioManager.setStreamMute(AudioManager.STREAM_DTMF, false);
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        //setting mute pref off
        mPrefHelper.addMuteState(false);
        Log.d("App", "setRingerMode: UNSILENT");
    }

    private Long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }


}
