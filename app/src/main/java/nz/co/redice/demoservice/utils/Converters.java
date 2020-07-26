package nz.co.redice.demoservice.utils;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.inject.Inject;

public class Converters {


    @TypeConverter
    public static LocalDate getLocalDateFromLong(Long longValue, String zoneId) {
        return Instant.ofEpochSecond(longValue).atZone(ZoneId.of(zoneId)).toLocalDate();
    }
    @TypeConverter
    public static LocalDateTime getLocalDateTimeFromLong(Long longValue, String zoneId) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(longValue), ZoneId.of(zoneId));
    }

}
