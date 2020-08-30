package nz.co.redice.azansilenttime.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

import static android.content.Context.MODE_PRIVATE;

@Singleton
public class SharedPreferencesHelper {

    public static final String DND_FRIDAYS_ONLY = "dnd_fridays_only";
    private static final String MY_PREFS = "my prefs";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String LOCATION_STATUS = "location_status";
    private static final String LOCATION_TEXT = "location_text";
    private static final String DND_PERIOD = "dnd_period";
    private static final String CALCULATION_METHOD = "calculation_method";
    private static final String CALCULATION_SCHOOL = "calculation_school";
    private static final String MIDNIGHT_MODE = "midnight_mode";
    private static final String REGULAR_TABLE_UPDATE_STATUS = "regular_table_update_status";
    private static final String FRIDAY_TABLE_UPDATE_STATUS = "friday_table_update_status";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    @SuppressLint("CommitPrefEdits")
    @Inject
    public SharedPreferencesHelper(@ApplicationContext Context context) {
        mSharedPreferences = context.getSharedPreferences(MY_PREFS, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
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

    public boolean isDndForFridaysOnly() {
        return mSharedPreferences.getBoolean(DND_FRIDAYS_ONLY, false);
    }

    public void setDndForFridaysOnly(boolean b) {
        mEditor.putBoolean(DND_FRIDAYS_ONLY, b).apply();
    }

    public String getLocationText() {
        return mSharedPreferences.getString(LOCATION_TEXT, "XXX");
    }

    public void setLocationText(String addressText) {
        mEditor.putString(LOCATION_TEXT, addressText).apply();
    }

    public Boolean isRegularTableToBePopulated() {
        return mSharedPreferences.getBoolean(REGULAR_TABLE_UPDATE_STATUS, false);
    }

    public void setRegularTableToBePopulated(Boolean status) {
        mEditor.putBoolean(REGULAR_TABLE_UPDATE_STATUS, status).apply();
    }

    public Boolean isFridayTableToBePopulated() {
        return mSharedPreferences.getBoolean(FRIDAY_TABLE_UPDATE_STATUS, false);
    }

    public void setFridayTableToBePopulated(Boolean status) {
        mEditor.putBoolean(FRIDAY_TABLE_UPDATE_STATUS, status).apply();
    }


    public int getDndPeriod() {
        return mSharedPreferences.getInt(DND_PERIOD, 10);
    }

    public void setDndPeriod(String value) {
        mEditor.putInt(DND_PERIOD, Integer.parseInt(value)).apply();
    }

    public int getCalculationSchool() {
        return mSharedPreferences.getInt(CALCULATION_SCHOOL, 0);
    }

    public void setCalculationSchool(String value) {
        mEditor.putInt(CALCULATION_SCHOOL, Integer.parseInt(value)).apply();
    }

    public int getCalculationMethod() {
        return mSharedPreferences.getInt(CALCULATION_METHOD, 4);
    }

    public void setCalculationMethod(String value) {
        mEditor.putInt(CALCULATION_METHOD, Integer.parseInt(value)).apply();
    }

    public int getMidnightMode() {
        return mSharedPreferences.getInt(MIDNIGHT_MODE, 0);
    }

    public void setMidnightMode(String value) {
        mEditor.putInt(MIDNIGHT_MODE, Integer.parseInt(value)).apply();
    }
}
