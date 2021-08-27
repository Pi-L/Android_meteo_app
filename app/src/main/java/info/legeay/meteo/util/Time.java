package info.legeay.meteo.util;

import java.time.OffsetDateTime;
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
}
