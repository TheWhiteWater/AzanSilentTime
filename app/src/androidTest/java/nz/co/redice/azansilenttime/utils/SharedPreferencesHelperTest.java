package nz.co.redice.azansilenttime.utils;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SharedPreferencesHelperTest {

    private static final boolean DEFAULT_DND_FRIDAYS_ONLY = false;
    private static final float DEFAULT_LATITUDE = -40.3596F;
    private static final float DEFAULT_LONGITUDE = 123.23F;
    private static final String DEFAULT_LOCATION = "XXX";
    private static final boolean DEFAULT_REGULAR_TABLE_UPDATE_STATUS = false;
    private static final boolean DEFAULT_FRIDAY_TABLE_UPDATE_STATUS = false;
    private static final int DEFAULT_DND_PERIOD = 10;
    private static final int DEFAULT_CALCULATION_SCHOOL = 0;
    private static final int DEFAULT_CALCULATION_METHOD = 4;
    private static final int DEFAULT_MIDNIGHT_MODE = 0;
    private SharedPreferencesHelper SUT;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getContext();
        SUT = new SharedPreferencesHelper(context);
        SUT.getSharedPreferences().edit().clear().commit();
    }


    //Latitude
    @Test
    public void sharedPreferencesHelper_getLatitude_returnsValidDefaultValue() {
        float actual = SUT.getLatitude();
        assertEquals((long) DEFAULT_LATITUDE, (long) actual);
    }

    @Test
    public void sharedPreferencesHelper_getLatitude_returnsValidSavedValue() {
        SUT.setLatitude(123.23F);
        float actual = SUT.getLatitude();
        assertEquals((long) DEFAULT_LONGITUDE, (long) actual);
    }

    //Longitude
    @Test
    public void sharedPreferencesHelper_getLongitude_returnsValidDefaultValue() {
        float actual = SUT.getLongitude();
        float expected = 175.61F;
        assertEquals((long) expected, (long) actual);
    }

    @Test
    public void sharedPreferencesHelper_getLongitude_returnsValidSavedValue() {
        float expected = 123.23F;
        SUT.setLongitude(expected);
        float actual = SUT.getLongitude();
        assertEquals((long) expected, (long) actual);
    }

    //DndForFridaysOnly
    @Test
    public void sharedPreferencesHelper_isDndForFridaysOnly_returnsValidDefaultValue() {
        boolean actual = SUT.isFridaysOnlyModeActive();
        assertEquals(DEFAULT_DND_FRIDAYS_ONLY, actual);
    }

    @Test
    public void sharedPreferencesHelper_isDndForFridaysOnly_returnsValidSavedValue() {
        boolean expected = true;
        SUT.setFridaysOnlyModeActive(expected);
        boolean actual = SUT.isFridaysOnlyModeActive();
        assertEquals(expected, actual);
    }

    //LocationText
    @Test
    public void sharedPreferencesHelper_getLocationText_returnsValidDefaultValue() {
        String actual = SUT.getLocationText();
        assertEquals(DEFAULT_LOCATION, actual);
    }

    @Test
    public void sharedPreferencesHelper_getLocationText_returnsValidSavedValue() {
        String expected = "Moscow, Russia";
        SUT.setLocationText(expected);
        String actual = SUT.getLocationText();
        assertEquals(expected, actual);
    }

    //RegularTableToBePopulated
    @Test
    public void sharedPreferencesHelper_isRegularTableToBePopulated_returnsValidSavedValue() {
        boolean expected = true;
        SUT.setRegularTableToBePopulated(expected);
        boolean actual = SUT.isAzanTimingsToBePopulated();
        assertEquals(expected, actual);
    }

    @Test
    public void sharedPreferencesHelper_isRegularTableToBePopulated_returnsValidDefaultValue() {
        boolean actual = SUT.isAzanTimingsToBePopulated();
        assertEquals(DEFAULT_REGULAR_TABLE_UPDATE_STATUS, actual);
    }

    //FridayTableToBePopulated
    @Test
    public void sharedPreferencesHelper_isFridayTableToBePopulated_returnsValidSavedValue() {
        boolean expected = true;
        SUT.setAlarmSchedulesToBeCreated(expected);
        boolean actual = SUT.isAlarmSchedulesToBeCreated();
        assertEquals(expected, actual);
    }

    @Test
    public void sharedPreferencesHelper_isFridayTableToBePopulated_returnsValidDefaultValue() {
        boolean actual = SUT.isAlarmSchedulesToBeCreated();
        assertEquals(DEFAULT_FRIDAY_TABLE_UPDATE_STATUS, actual);
    }


    //getDndPeriod
    @Test
    public void sharedPreferencesHelper_getDndPeriod_returnsValidSavedValue() {
        int expected = 99;
        SUT.setDndPeriod("99");
        int actual = SUT.getDndPeriod();
        assertEquals(expected, actual);
    }

    @Test
    public void sharedPreferencesHelper_getDndPeriod_returnsValidDefaultValue() {
        int actual = SUT.getDndPeriod();
        assertEquals(DEFAULT_DND_PERIOD, actual);
    }

    //getCalculationSchool
    @Test
    public void sharedPreferencesHelper_getCalculationSchool_returnsValidSavedValue() {
        int expected = 30;
        SUT.setCalculationSchool("30");
        int actual = SUT.getCalculationSchool();
        assertEquals(expected, actual);
    }

    @Test
    public void sharedPreferencesHelper_getCalculationSchool_returnsValidDefaultValue() {
        int actual = SUT.getCalculationSchool();
        assertEquals(DEFAULT_CALCULATION_SCHOOL, actual);
    }


    //getCalculationMethod
    @Test
    public void sharedPreferencesHelper_getCalculationMethod_returnsValidSavedValue() {
        int expected = 20;
        SUT.setCalculationMethod("20");
        int actual = SUT.getCalculationMethod();
        assertEquals(expected, actual);
    }

    @Test
    public void sharedPreferencesHelper_getCalculationMethod_returnsValidDefaultValue() {
        int actual = SUT.getCalculationMethod();
        assertEquals(DEFAULT_CALCULATION_METHOD, actual);
    }


    //getMidnightMode
    @Test
    public void sharedPreferencesHelper_getMidnightMode_returnsValidSavedValue() {
        int expected = 10;
        SUT.setMidnightMode("10");
        int actual = SUT.getMidnightMode();
        assertEquals(expected, actual);
    }

    @Test
    public void sharedPreferencesHelper_getMidnightMode_returnsValidDefaultValue() {
        int actual = SUT.getMidnightMode();
        assertEquals(DEFAULT_MIDNIGHT_MODE, actual);
    }


}