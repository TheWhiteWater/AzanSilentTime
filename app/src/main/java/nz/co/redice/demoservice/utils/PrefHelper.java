package nz.co.redice.demoservice.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

import static android.content.Context.MODE_PRIVATE;

@Singleton
public class PrefHelper {

    private static final String MY_PREFS = "my prefs";
    private static final String MUTE_STATE = "mute state";
    private static final String SLEEP_TIME = "sleep time";
    private static final String WAKEUP_TIME = "wakeup time";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String LOCATION_PERMISSION_STATUS = "location_permission_status";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Context mContext;

    @Inject
    public PrefHelper(@ApplicationContext Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void addMuteState(Boolean state) {
        mEditor.putBoolean(MUTE_STATE, state);
        mEditor.apply();
    }

    public Boolean getMuteState() {
        return mSharedPreferences.getBoolean(MUTE_STATE, false);
    }

    public Long getSleepTime() {
        return mSharedPreferences.getLong(SLEEP_TIME, Calendar.getInstance().getTimeInMillis());
    }

    public void setSleepTime(Long time) {
        mEditor.putLong(SLEEP_TIME, time).apply();
    }

    public Long getWakeUpTime() {
        return mSharedPreferences.getLong(WAKEUP_TIME, Calendar.getInstance().getTimeInMillis());
    }

    public void setWakeUpTime(Long time) {
        mEditor.putLong(WAKEUP_TIME, time).apply();
    }

    public float getLongitude() {
        return mSharedPreferences.getFloat(LONGITUDE, 175.61F);
    }

    public void setLongitude(Float longitude) {
        mEditor.putFloat(LONGITUDE, longitude).apply();
    }

    public float getLatitude() {
        return mSharedPreferences.getFloat(LATITUDE, -40.3596F);
    }

    public void setLatitude(Float longitude) {
        mEditor.putFloat(LATITUDE, longitude).apply();
    }

    public boolean getLocationPermissionStatus() {
        return mSharedPreferences.getBoolean(LOCATION_PERMISSION_STATUS, false);
    }

    public void setLocationPermissionStatus(Boolean status) {
        mEditor.putBoolean(LOCATION_PERMISSION_STATUS, status).apply();
    }
}
