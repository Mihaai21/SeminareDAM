package ro.ase.semdam.date;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class Converters {

    @TypeConverter
    public static String dinLocalDate(LocalDate date) {
        return date == null ? null : date.toString();
    }

    @TypeConverter
    public static LocalDate inLocalDate(String value) {
        return value == null ? null : LocalDate.parse(value);
    }
}