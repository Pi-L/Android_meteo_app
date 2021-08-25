package info.legeay.meteo.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import info.legeay.meteo.R;

public class Image {

    /*
     * Méthode qui initialise l'icon blanc présent dans la MainActivity
     * */
    public static int getWeatherIcon(int actualId, long sunrise, long sunset, long offset) {

        int id = actualId / 100;
        int icon = R.drawable.weather_sunny_white;

        long currentTime;
        if(offset > 0) {
            currentTime = OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(offset).toEpochSecond();
        } else {
            currentTime = OffsetDateTime.now(ZoneOffset.UTC).minusSeconds(offset).toEpochSecond();
        }

        boolean isDay = currentTime >= sunrise && currentTime < sunset;

        if (actualId == 800) {
            if (isDay) return R.drawable.weather_sunny_white;
            return R.drawable.weather_clear_night_white;
        }

        switch (id) {
            case 2:
                icon = R.drawable.weather_thunder_white;
                break;
            case 3:
                icon = R.drawable.weather_drizzle_white;
                break;
            case 7:
                icon = R.drawable.weather_foggy_white;
                break;
            case 8:
                icon = R.drawable.weather_cloudy_white;
                break;
            case 6:
                icon = R.drawable.weather_snowy_white;
                break;
            case 5:
                icon = R.drawable.weather_rainy_white;
                break;
        }

        return icon;
    }
}
