package info.legeay.meteo.model;


import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class City {

    private Long idDb;

    private Integer favPosition;
    private Long id;
    private String name;
    private String weatherDescription;
    private String currentTemperature;
    private Integer weatherIconDrawableId;

    private final double lat;
    private final double lon;



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
}
