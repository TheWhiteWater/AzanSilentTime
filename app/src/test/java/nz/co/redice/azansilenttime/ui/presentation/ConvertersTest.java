package nz.co.redice.azansilenttime.ui.presentation;


import org.junit.Assert;
import org.junit.Test;

import static nz.co.redice.azansilenttime.ui.presentation.Converters.convertTextDateAndTextTimeIntoEpoch;
import static nz.co.redice.azansilenttime.ui.presentation.Converters.convertTextDateIntoEpoch;

public class ConvertersTest {


    @Test
    public void converters_convertTextIntoEpoch_returnsValidValue() {
        String textDate = "30-08-2020";
        long result = convertTextDateIntoEpoch(textDate);
        Assert.assertEquals(1598702400L, result);
    }

    @Test
    public void converters_convertTextIntoEpoch_returnsNull_onWrongInput() {
        String textDate = "30-008-2020";
        Assert.assertNull(convertTextDateIntoEpoch(textDate));
    }

    @Test
    public void converters_convertTextIntoEpoch_returnsNull_onEmptyString() {
        String textDate = "";
        Assert.assertNull(convertTextDateIntoEpoch(textDate));
    }

    @Test
    public void converters_convertsTextDateAndTimeIntoEpoch_returnsValidValue() {
        String textDate = "30-08-2020";
        String textTime = "02:30 (NZDT)";
        long result = convertTextDateAndTextTimeIntoEpoch(textDate, textTime);
        Assert.assertEquals(1598711400L, result);
    }

    @Test
    public void converters_convertsTextDateAndTimeIntoEpoch_returnsNullOnWrongInput() {
        String textDate = "30-08-2020";
        String textTime = "02:320 (NZDT)";
        Assert.assertNull(convertTextDateAndTextTimeIntoEpoch(textDate, textTime));
    }

    @Test
    public void converters_convertEpochIntoTextDate_returnsValidResult() {
        long epochDate = 1598711400L;
        String expectedText = "30 августа 2020 г. ";
        String result = Converters.convertEpochIntoTextDate(epochDate);
        Assert.assertEquals(expectedText, result);
    }

    @Test
    public void converters_convertEpochIntoTextTime_returnsValidResult() {
        long epochDate = 1598711400L;
        String expectedText = "02:30 AM";
        String result = Converters.convertEpochIntoTextTime(epochDate);
        Assert.assertEquals(expectedText, result);
    }

}