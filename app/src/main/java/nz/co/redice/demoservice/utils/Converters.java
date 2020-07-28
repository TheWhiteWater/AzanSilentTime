package nz.co.redice.demoservice.utils;

import android.content.Context;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Converters {

    public static LocalDate getLocalDateFromLong(Long longValue, String timeZone) {
        return Instant.ofEpochSecond(longValue).atZone(ZoneId.of(timeZone)).toLocalDate();
    }

    public static LocalDateTime getLocalDateTimeFromLong(Long longValue, String timeZone) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(longValue), ZoneId.of(timeZone));
    }

    public static Long getLocalDateFromStringIntoLong(String date, ZoneId zoneId) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                .atStartOfDay(zoneId)
                .toEpochSecond();
    }

    public static Long getLocalDateTimeFromStringIntoLong(String date, String time, ZoneId zoneId) {
        String[] mytime = time.split(" ");
        return LocalDateTime.parse(date + " " + mytime[0], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                .atZone(zoneId)
                .toEpochSecond();
    }

    public static String convertLocalDateToString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        return date.format(formatter);
    }

    public static String convertLongValueOfLocalDateIntoString(Long longValue, String timeZone, String formatPattern) {
        LocalDate date = getLocalDateFromLong(longValue, timeZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
        return date.format(formatter);
    }

    public static String convertLongValueOfLocalDateTimeIntoString(Long longValue, String timeZone, String formatPattern ) {
        LocalDateTime date = getLocalDateTimeFromLong(longValue, timeZone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
        return date.format(formatter);
    }


}
