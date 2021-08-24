package info.legeay.meteo.dto;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;

import info.legeay.meteo.model.City;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class WeatherDTO implements Serializable {

    private Long id;
    private String name;
    private Coord coord;
    private Weather[] weather;
    private Main main;
    private Wind wind;
    private Integer cod;

    public City toCity() {

        if(cod == 404) return null;

        boolean isWeatherArrayNotInitialised = this.weather == null || this.weather.length == 0;
        String iconTemplate = "https://openweathermap.org/img/wn/%s@2x.png";
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        Long currentId = this.id == null ? 1L : this.id;
        String currentName = StringUtils.isBlank(this.name) ? "Pas de nom" : this.name;
        double currentLat = this.coord == null || this.coord.lat == null ? 0D : this.coord.lat;
        double currentLon = this.coord == null || this.coord.lon == null ? 0D : this.coord.lon;
        String currentTemperature = this.main == null || this.main.temp == null ? "21.2°C" : String.format("%s°C", decimalFormat.format(this.main.temp - 273.15));
        String currentWeatherIconUrl = isWeatherArrayNotInitialised || StringUtils.isBlank(this.weather[0].icon) ? String.format(iconTemplate,"10d") : String.format(iconTemplate,this.weather[0].icon);
        String currentWheatherDescription = isWeatherArrayNotInitialised || StringUtils.isBlank(this.weather[0].description) ? "Pas de description" : this.weather[0].description;


        return new City(currentId, currentName, currentWheatherDescription, currentTemperature, currentWeatherIconUrl, currentLat, currentLon);
    }


    @Override
    public String toString() {
        return "WeatherDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coord=" + coord +
                ", weatherArray=" + Arrays.toString(weather) +
                ", main=" + main +
                ", wind=" + wind +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @NoArgsConstructor
    public static class Coord implements Serializable {
        public Double lon;
        public Double lat;

        @Override
        public String toString() {
            return "coord{" +
                    "lon=" + lon +
                    ", lat=" + lat +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @NoArgsConstructor
    public static class Weather implements Serializable {
        public Long id;
        public String main;
        public String description;
        public String icon;

        @Override
        public String toString() {
            return "Weather{" +
                    "id=" + id +
                    ", main='" + main + '\'' +
                    ", description='" + description + '\'' +
                    ", icon='" + icon + '\'' +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @NoArgsConstructor
    public static class Main implements Serializable {
        public Double temp;
        public Double feels_like;
        public Double temp_min;
        public Double temp_max;
        public Double pressure;
        public Double humidity;

        @Override
        public String toString() {
            return "Main{" +
                    "temp=" + temp +
                    ", feels_like=" + feels_like +
                    ", temp_min=" + temp_min +
                    ", temp_max=" + temp_max +
                    ", pressure=" + pressure +
                    ", humidity=" + humidity +
                    '}';
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @NoArgsConstructor
    public static class Wind implements Serializable {
        public Double speed;
        public Double deg;

        @Override
        public String toString() {
            return "Wind{" +
                    "speed=" + speed +
                    ", deg=" + deg +
                    '}';
        }
    }
}
