package nz.co.redice.demoservice.utils;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converters {
//    @TypeConverter
        public static Long setDate(String date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date returnDate = new Date();
            try {
                returnDate = dateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return returnDate.getTime();
        }

//        @TypeConverter
        public static Long setTime(String date, String time) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm (zzzz)");
            Date convertedDate = null;
            try {
                convertedDate = dateFormat.parse(date + " " + time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date result = new Date(convertedDate.getTime());
            return result.getTime();
        }


}
