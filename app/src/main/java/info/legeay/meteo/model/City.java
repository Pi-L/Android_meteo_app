package info.legeay.meteo.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

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

    public City(Long idDb, Integer favPosition, Long id, String name, String weatherDescription, String currentTemperature, Integer weatherIconDrawableId, double lat, double lon) {
        this.idDb = idDb;
        this.favPosition = favPosition;
        this.id = id;
        this.name = name;
        this.weatherDescription = weatherDescription;
        this.currentTemperature = currentTemperature;
        this.weatherIconDrawableId = weatherIconDrawableId;
        this.lat = lat;
        this.lon = lon;
    }

    // ignore: else Room can't pick
    @Ignore
    public City(Long id, String name, String weatherDescription, String currentTemperature, Integer weatherIconDrawableId, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.weatherDescription = weatherDescription;
        this.currentTemperature = currentTemperature;
        this.weatherIconDrawableId = weatherIconDrawableId;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weatherDescription='" + weatherDescription + '\'' +
                ", currentTemperature='" + currentTemperature + '\'' +
                ", weatherIconDrawableId='" + weatherIconDrawableId + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
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
}
