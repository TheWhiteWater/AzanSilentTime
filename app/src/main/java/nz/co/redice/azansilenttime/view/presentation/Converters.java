package nz.co.redice.azansilenttime.view.presentation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Converters {
    private static final String READABLE_DATE_FORMAT = "EEE, dd MMMM uuuu";
    private static final String READABLE_TIME_FORMAT = "HH:mm a";

    public static Long getLocalDateIntoLong(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
    }

    public static Long getLocalTimeIntoLong(String date, String time) {
        String[] mytime = time.split(" ");
        return LocalDateTime.parse(date + " " + mytime[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                .atZone(ZoneId.systemDefault())
                .toEpochSecond();
    }

    public static String setDateFromLong(Long longValue) {
        if (longValue != null) {
            LocalDate date = Instant.ofEpochSecond(longValue).atZone(ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(READABLE_DATE_FORMAT);
            return date.format(formatter);
        } else {
            return "0";
        }
    }

    public static String setTimeFromLong(Long longValue) {
        if (longValue != null) {
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(longValue), ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(READABLE_TIME_FORMAT);
            return date.format(formatter);
        } else {
            return "0";
        }

    }

}
