package info.legeay.meteo.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class Time {

    public static boolean isDay(long sunrise, long sunset, long offset) {

        long currentTime = OffsetDateTime.now(ZoneOffset.ofTotalSeconds((int) offset)).toEpochSecond();

        return currentTime >= sunrise && currentTime < sunset;
    }

    public static String getTimeFormated(long offset) {
        OffsetDateTime currentTime = OffsetDateTime.now(ZoneOffset.ofTotalSeconds((int) offset));
        return currentTime.format(DateTimeFormatter.ofPattern("HH'h'mm"));
    }

    public static OffsetDateTime getOffsetDateTime(long seconds, long offset) {
        // todo: wtf?
        return OffsetDateTime.of(LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.ofTotalSeconds((int) offset)), ZoneOffset.ofTotalSeconds((int) offset));
    }

    public static String getTimeFormated(long seconds, long offset) {
        return getOffsetDateTime(seconds, offset).format(DateTimeFormatter.ofPattern("HH'h'mm"));
    }

    public static String getDayMonthFormated(long seconds, long offset) {
        return getOffsetDateTime(seconds, offset).format(DateTimeFormatter.ofPattern("dd/MM"));
    }
}
