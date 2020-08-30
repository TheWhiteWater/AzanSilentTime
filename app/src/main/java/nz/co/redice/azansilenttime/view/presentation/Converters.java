package nz.co.redice.azansilenttime.view.presentation;

import android.util.Log;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Converters {
    private static final String DATE_FORMAT = "EEE,  dd MMM uuuu ";
    private static final String DAY_OF_THE_WEEK_FORMAT = "EEEE";
    private static final String READABLE_TIME_FORMAT = "HH:mm a";

    public static Long convertTextDateIntoEpoch(String date) {
        if (date.matches("(\\d{2})-(\\d{2})-(\\d{4})"))
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    .atStartOfDay(ZoneId.systemDefault())
                    .toEpochSecond();
        else
            return null;
    }

    public static Long convertTextDateAndTextTimeIntoEpoch(String date, String time) {
        String[] mytime = time.split(" ");
        if (mytime[0].matches("(\\d{2}):(\\d{2})")) {
        return LocalDateTime.parse(date + " " + mytime[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();
        } else
            return null;
    }

    public static String convertEpochIntoTextDate(Long longValue) {
        if (longValue != null) {
            LocalDate date = Instant.ofEpochSecond(longValue).atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
            return date.format(formatter) + " ";
        } else {
            return null;
        }
    }

    public static String convertEpochIntoTextTime(Long longValue) {
        if (longValue != null) {
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(longValue), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(READABLE_TIME_FORMAT);
            return date.format(formatter);
        } else {
            return null;
        }

    }

}
