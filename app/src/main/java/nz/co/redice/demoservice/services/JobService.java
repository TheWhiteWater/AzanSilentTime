package nz.co.redice.demoservice.services;

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
public class JobService extends android.app.Service {

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
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

        //setting mute pref on
        mPrefHelper.addMuteState(true);
        Log.d("App", "setRingerMode: SILENT");
    }

    private void unMuteAllStreams() {
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        //setting mute pref off
        mPrefHelper.addMuteState(false);
        Log.d("App", "setRingerMode: UNSILENT");
    }

    private Long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }


}
