package nz.co.redice.demoservice.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Converters {


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







    public static String convertLongValueOfLocalDateIntoString(Long longValue, String formatPattern) {
        LocalDate date = Instant.ofEpochSecond(longValue).atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
        return date.format(formatter);
    }

    public static String convertLongValueOfLocalDateTimeIntoString(Long longValue, String formatPattern) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochSecond(longValue), ZoneId.systemDefault());;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
        return date.format(formatter);
    }


}
