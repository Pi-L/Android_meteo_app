package info.legeay.meteo.dto;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Arrays;

import info.legeay.meteo.R;
import info.legeay.meteo.model.City;
import info.legeay.meteo.util.Image;
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
    private Sys sys;
    private Integer timezone;
    private Integer cod;

    public City toCity() {

        if(cod == 404) return null;

        boolean isWeatherArrayInitialised = this.weather != null && this.weather.length > 0;

//        String iconTemplate = "https://openweathermap.org/img/wn/%s@2x.png";
        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        Long currentId = this.id == null ? 1L : this.id;
        String currentCountry = this.sys == null || StringUtils.isBlank(this.sys.country) ? "" : String.format(" (%s)", this.sys.country);
        String currentName = StringUtils.isBlank(this.name) ? "Pas de nom" : String.format("%s%s", this.name, currentCountry);
        double currentLat = this.coord == null || this.coord.lat == null ? 0D : this.coord.lat;
        double currentLon = this.coord == null || this.coord.lon == null ? 0D : this.coord.lon;
        String currentTemperature = this.main == null || this.main.temp == null ? "21.2°C" : String.format("%s°C", decimalFormat.format(this.main.temp));

        int currentWeatherIconDrawableId = R.drawable.weather_thunder_grey;


        if(isWeatherArrayInitialised && weather[0].id != null && sys != null && sys.sunrise != null && sys.sunset != null && timezone != null) {
            currentWeatherIconDrawableId = Image.getWeatherIcon(weather[0].id, sys.sunrise, sys.sunset, timezone);
        }

        String currentWheatherDescription = !isWeatherArrayInitialised || StringUtils.isBlank(this.weather[0].description) ? "Pas de description" : this.weather[0].description;

        return new City(currentId, currentName, currentWheatherDescription, currentTemperature, currentWeatherIconDrawableId, currentLat, currentLon);
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
        public Integer id;
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @NoArgsConstructor
    public static class Sys implements Serializable {
        public String country;
        public Long sunrise;
        public Long sunset;

        @Override
        public String toString() {
            return "Sys{" +
                    "country='" + country + '\'' +
                    ", sunrise=" + sunrise +
                    ", sunset=" + sunset +
                    '}';
        }
    }
}
