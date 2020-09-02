package nz.co.redice.azansilenttime.services.audio_service;

import android.content.Context;
import android.media.AudioManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class AudioServiceImpl implements AudioService {

    private AudioManager mAudioManager;

    @Inject
    public AudioServiceImpl(@ApplicationContext Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

    }

    @Override
    public void turnDndOn() {
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    @Override
    public void turnDndOff() {
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }
}
