package nz.co.redice.demoservice.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static android.content.Context.MODE_PRIVATE;

@Singleton
public class PrefHelper {

    private static final String MY_PREFS = "my prefs";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String LOCATION_PERMISSION_STATUS = "location_permission_status";
    private static final String MUTE_ON_FRIDAYS_ONLY = "mute on fridays only";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Subject<Boolean> fridayOnly = PublishSubject.create();
    private Context mContext;

    public Observable<Boolean> getFridayOnly() {
        return fridayOnly;
    }

    @Inject
    public PrefHelper(@ApplicationContext Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
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

    public void setDndOnFridaysOnly(boolean b) {
        mEditor.putBoolean(MUTE_ON_FRIDAYS_ONLY, b).apply();
        fridayOnly.onNext(getDndOnFridaysOnly());
    }

    public boolean getDndOnFridaysOnly() {
        return mSharedPreferences.getBoolean(MUTE_ON_FRIDAYS_ONLY, false);
    }
}
