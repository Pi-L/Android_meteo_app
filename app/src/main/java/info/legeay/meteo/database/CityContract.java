package info.legeay.meteo.database;

import android.provider.BaseColumns;

public final class CityContract {

    private CityContract() { }

    /* Inner class that defines the table contents */
    public static class CityEntry implements BaseColumns {
        public static final String TABLE_NAME = "city";
        public static final String KEY_FAV_POSITION = "fav_position";
        public static final String KEY_ID_CITY = "id_city";
        public static final String KEY_NAME = "name";
        public static final String KEY_CURRENT_TEMPERATURE = "current_temperature";
        public static final String KEY_WEATHER_DESCRITPION = "weather_description";
        public static final String KEY_ICON_ID = "icon_id";
        public static final String KEY_LAT = "lat";
        public static final String KEY_LON = "lon";

    }
}
