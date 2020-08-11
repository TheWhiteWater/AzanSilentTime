package nz.co.redice.demoservice.utils;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import nz.co.redice.demoservice.R;

import static android.content.Context.MODE_PRIVATE;

@Singleton
public class PrefHelper {

    private static final String MY_PREFS = "my prefs";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String LOCATION_STATUS = "location_status";
    private static final String DND_FRIDAYS_ONLY = "dnd_fridays_only";
    private static final String LOCATION_TEXT = "location_text";
    private static final String DND_PERIOD = "dnd_period";
    private static final String CALCULATION_METHOD = "calculation_method";
    private static final String CALCULATION_SCHOOL = "calculation_school";
    private static final String MIDNIGHT_MODE = "midnight_mode";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Subject<Boolean> dndForFridayOnly = PublishSubject.create();
    private Context mContext;

    @Inject
    public PrefHelper(@ApplicationContext Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public Observable<Boolean> getDndForFridayOnly() {
        return dndForFridayOnly;
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

    public boolean getLocationStatus() {
        return mSharedPreferences.getBoolean(LOCATION_STATUS, false);
    }

    public void setLocationStatus(Boolean status) {
        mEditor.putBoolean(LOCATION_STATUS, status).apply();
    }

    public boolean getDndOnFridaysOnly() {
        return mSharedPreferences.getBoolean(DND_FRIDAYS_ONLY, false);
    }

    public void setDndOnFridaysOnly(boolean b) {
        mEditor.putBoolean(DND_FRIDAYS_ONLY, b).apply();
        dndForFridayOnly.onNext(getDndOnFridaysOnly());
    }

    public String getLocationText() {
        return mSharedPreferences.getString(LOCATION_TEXT, "XXX");
    }

    public void setLocationText(String addressText) {
        mEditor.putString(LOCATION_TEXT, addressText);

    }


    private int getPrefIndex(String[] array, String value) {
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (value.equals(array[i]))
                index = i;
        }
        return index;
    }

    private int getPrefValue(String[] array, String value) {
        int index = 0;
        for (int i = 0; i < array.length; i++) {
            if (value.equals(array[i]))
                index = i;
        }
        return Integer.parseInt(array[index]);
    }

    public void setDndPeriod(String value) {
        String[] values = mContext.getResources().getStringArray(R.array.dnd_period_values);
        mEditor.putInt(DND_PERIOD, getPrefValue(values, value)).apply();
    }

    public int getDndPeriod() {
        return mSharedPreferences.getInt(DND_PERIOD, 10);
    }


    public void setCalculationSchool(String value) {
        String[] values = mContext.getResources().getStringArray(R.array.school_values);
        mEditor.putInt(CALCULATION_SCHOOL, getPrefValue(values, value)).apply();
    }

    public int getCalculationSchool() {
        return mSharedPreferences.getInt(CALCULATION_SCHOOL, 0);
    }

    public void setCalculationMethod(String value) {
        String[] values = mContext.getResources().getStringArray(R.array.calculation_method_values);
        mEditor.putInt(CALCULATION_METHOD, getPrefValue(values, value)).apply();
    }

    public int getCalculationMethod() {
        return mSharedPreferences.getInt(CALCULATION_METHOD, 4);
    }


    public void setMidnightMode(String value) {
        String[] values = mContext.getResources().getStringArray(R.array.midnight_mode_values);
        mEditor.putInt(MIDNIGHT_MODE, getPrefValue(values, value)).apply();
    }

    public int getMidnightMode() {
        return mSharedPreferences.getInt(MIDNIGHT_MODE, 0);
    }
}
