package info.legeay.meteo.model;


import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

import info.legeay.meteo.util.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

// https://developer.android.com/codelabs/android-room-with-a-view

// https://developer.android.com/training/data-storage/room/relationships

@Entity(tableName = "city")
public class City {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long idDb;

    @ColumnInfo(name = "fav_position")
    private Integer favPosition;

    @ColumnInfo(name = "id_city")
    private Long id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "current_temperature")
    private String weatherDescription;
    @ColumnInfo(name = "weather_description")
    private String currentTemperature;
    @ColumnInfo(name = "icon_id")
    private Integer weatherIconDrawableId;

    @ColumnInfo(name = "lat")
    private double lat;
    @ColumnInfo(name = "lon")
    private double lon;

    @ColumnInfo(name = "sunrise_ts_seconds")
    private Long sunriseTsSeconds;
    @ColumnInfo(name = "sunset_ts_seconds")
    private Long sunsetTsSeconds;
    @ColumnInfo(name = "utc_offset_seconds")
    private Integer utcOffsetSeconds;


    public boolean isDayTime() {

        Log.d("PILMETEOAPP", "isDayTime: "+sunriseTsSeconds+" "+sunsetTsSeconds+" "+utcOffsetSeconds);

        if(sunriseTsSeconds == null || sunsetTsSeconds  == null  || utcOffsetSeconds == null) return true;
        return Time.isDay(sunriseTsSeconds, sunsetTsSeconds, utcOffsetSeconds);
    }

    public City(Long idDb, Integer favPosition, Long id, String name, String weatherDescription, String currentTemperature, Integer weatherIconDrawableId, double lat, double lon, Long sunriseTsSeconds, Long sunsetTsSeconds, Integer utcOffsetSeconds) {
        this.idDb = idDb;
        this.favPosition = favPosition;
        this.id = id;
        this.name = name;
        this.weatherDescription = weatherDescription;
        this.currentTemperature = currentTemperature;
        this.weatherIconDrawableId = weatherIconDrawableId;
        this.lat = lat;
        this.lon = lon;
        this.sunriseTsSeconds = sunriseTsSeconds;
        this.sunsetTsSeconds = sunsetTsSeconds;
        this.utcOffsetSeconds = utcOffsetSeconds;
    }

    // ignore: else Room can't pick
    @Ignore
    public City(Long id, String name, String weatherDescription, String currentTemperature, Integer weatherIconDrawableId, double lat, double lon, Long sunriseTsSeconds, Long sunsetTsSeconds, Integer utcOffsetSeconds) {
        this.id = id;
        this.name = name;
        this.weatherDescription = weatherDescription;
        this.currentTemperature = currentTemperature;
        this.weatherIconDrawableId = weatherIconDrawableId;
        this.lat = lat;
        this.lon = lon;
        this.sunriseTsSeconds = sunriseTsSeconds;
        this.sunsetTsSeconds = sunsetTsSeconds;
        this.utcOffsetSeconds = utcOffsetSeconds;
    }

    @Override
    public String toString() {
        return "City{" +
                "idDb=" + idDb +
                ", favPosition=" + favPosition +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", weatherDescription='" + weatherDescription + '\'' +
                ", currentTemperature='" + currentTemperature + '\'' +
                ", weatherIconDrawableId=" + weatherIconDrawableId +
                ", lat=" + lat +
                ", lon=" + lon +
                ", sunriseTsSeconds=" + sunriseTsSeconds +
                ", sunsetTsSeconds=" + sunsetTsSeconds +
                ", utcOffsetSeconds=" + utcOffsetSeconds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Double.compare(city.lat, lat) == 0 && Double.compare(city.lon, lon) == 0 && Objects.equals(id, city.id) && Objects.equals(name, city.name) && Objects.equals(weatherDescription, city.weatherDescription) && Objects.equals(currentTemperature, city.currentTemperature) && Objects.equals(weatherIconDrawableId, city.weatherIconDrawableId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, weatherDescription, currentTemperature, weatherIconDrawableId, lat, lon);
    }


    public Long getIdDb() {
        return idDb;
    }

    public void setIdDb(Long idDb) {
        this.idDb = idDb;
    }

    public Integer getFavPosition() {
        return favPosition;
    }

    public void setFavPosition(Integer favPosition) {
        this.favPosition = favPosition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(String currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public Integer getWeatherIconDrawableId() {
        return weatherIconDrawableId;
    }

    public void setWeatherIconDrawableId(Integer weatherIconDrawableId) {
        this.weatherIconDrawableId = weatherIconDrawableId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public Long getSunriseTsSeconds() {
        return sunriseTsSeconds;
    }

    public void setSunriseTsSeconds(Long sunriseTsSeconds) {
        this.sunriseTsSeconds = sunriseTsSeconds;
    }

    public Long getSunsetTsSeconds() {
        return sunsetTsSeconds;
    }

    public void setSunsetTsSeconds(Long sunsetTsSeconds) {
        this.sunsetTsSeconds = sunsetTsSeconds;
    }

    public Integer getUtcOffsetSeconds() {
        return utcOffsetSeconds;
    }

    public void setUtcOffsetSeconds(Integer utcOffsetSeconds) {
        this.utcOffsetSeconds = utcOffsetSeconds;
    }
}
