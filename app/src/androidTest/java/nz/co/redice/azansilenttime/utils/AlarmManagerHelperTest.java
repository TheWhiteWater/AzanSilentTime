package nz.co.redice.azansilenttime.utils;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import nz.co.redice.azansilenttime.R;

@RunWith(MockitoJUnitRunner.class)
public class AlarmManagerHelperTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                    android.Manifest.permission.FOREGROUND_SERVICE,
                    android.Manifest.permission.WAKE_LOCK);
    private AlarmManagerHelper SUT;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SUT = new AlarmManagerHelper(context);

    }

    @Test
    public void alarmManagerHelper_setAlarmManager_setsValidAlarm() {
        Long timing = System.currentTimeMillis() / 1000;
        int requestCode = 777;
        boolean toBeActivated = true;
        SUT.setAlarmManager(timing, requestCode, toBeActivated);
    }


}