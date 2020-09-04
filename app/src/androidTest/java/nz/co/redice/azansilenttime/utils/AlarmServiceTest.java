package nz.co.redice.azansilenttime.utils;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import nz.co.redice.azansilenttime.services.alarm_service.AlarmServiceImpl;
import nz.co.redice.azansilenttime.services.alarm_service.AlarmStatus;
import nz.co.redice.azansilenttime.repo.Repository;
import nz.co.redice.azansilenttime.services.alarm_service.Schedule;


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
    public void getEarliestSchedule_returnsValidSchedule() {
        ArrayList <Schedule> schedules = new ArrayList<>();
        schedules.add(new Schedule(1601574720L, false));
        schedules.add(new Schedule(1601574721L, false));
        schedules.add(new Schedule(1601574722L, true));
        schedules.add(new Schedule(1601574723L, false));
        schedules.add(new Schedule(1601574724L, true));
        schedules.add(new Schedule(1601574725L, false));

        long expected = SUT.getEarliestSchedule(schedules).epochSecond;

        Assert.assertEquals(1601574722L, expected);
    }


}