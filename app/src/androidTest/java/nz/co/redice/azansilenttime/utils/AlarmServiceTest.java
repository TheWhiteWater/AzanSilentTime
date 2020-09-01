package nz.co.redice.azansilenttime.utils;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import nz.co.redice.azansilenttime.services.alarm_service.AlarmServiceImpl;
import nz.co.redice.azansilenttime.services.alarm_service.AlarmStatus;
import nz.co.redice.azansilenttime.repo.Repository;


@RunWith(MockitoJUnitRunner.class)
public class AlarmServiceTest {


    private AlarmServiceImpl SUT;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Repository repository = Mockito.mock(Repository.class);
        AlarmStatus alarmStatus = new AlarmStatus();
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
        SUT = new AlarmServiceImpl(context, sharedPreferencesHelper, repository, alarmStatus);
    }

    @Test
    public void alarmManagerHelper_setAlarmManager_setsValidAlarm() {
        Long timing = LocalDateTime.now().minusMinutes(10).toEpochSecond(ZoneOffset.UTC);
        int requestCode = 777;
        SUT.scheduleMuteAlarm(timing, requestCode);
        Log.d("Test", "alarmManagerHelper: " + SUT.getScheduledAlarmList().get(0));
        Log.d("Test", "alarmManagerHelper: " + timing);
    }


}