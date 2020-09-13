package nz.co.redice.azansilenttime.ui.presentation;

import android.annotation.SuppressLint;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class Converters {
    private static final String READABLE_TIME_FORMAT = "HH:mm";
    private static final String REGULAR_DATE_EXPRESSION = "(\\d{2})-(\\d{2})-(\\d{4})";
    private static final String REGULAR_TIME_EXPRESSION = "(\\d{2}):(\\d{2})";

    public static Long convertTextDateIntoEpoch(String dateText) {
        if (dateText.matches(REGULAR_DATE_EXPRESSION))
            return LocalDate.parse(dateText, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    .atStartOfDay(ZoneId.systemDefault())
                    .toEpochSecond();
        else
            return null;
    }

    public static Long convertTextDateAndTextTimeIntoEpoch(String dateText, String timeText) {
        String[] mytime = timeText.split(" ");
        if (mytime[0].matches(REGULAR_TIME_EXPRESSION)) {
            return LocalDateTime.parse(dateText + " " + mytime[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond();
        } else
            return null;
    }

    public static String convertEpochIntoTextDate(Long epochSeconds) {
        if (epochSeconds != null) {
            LocalDate date = Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
            return date.format(formatter) + " ";
        } else {
            return null;
        }
    }

    public static String convertEpochIntoTextTime(Long epochSeconds) {
        if (epochSeconds != null) {
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(READABLE_TIME_FORMAT);
            return date.format(formatter);
        } else {
            return null;
        }
    }


    public static Long convertEpochSecondsIntoTimeMillis(Long epochSeconds) {
        return epochSeconds * 1000;
    }


    @SuppressLint("DefaultLocale")
    public static String convertDndPeriodIntoText(int period) {
        return period >= 60 ? String.format("- %d hour/s", period / 60) : String.format("- %d min", period);
    }


}
