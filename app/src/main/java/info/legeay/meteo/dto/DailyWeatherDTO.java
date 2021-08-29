package info.legeay.meteo.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import info.legeay.meteo.model.DailyWeather;
import info.legeay.meteo.util.Image;
import info.legeay.meteo.util.Time;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DailyWeatherDTO implements Serializable {

    @JsonProperty("lat")
    private Double lat;
    @JsonProperty("lon")
    private Double lon;
    @JsonProperty("timezone")
    private String timezone;
    @JsonProperty("timezone_offset")
    private Long timezone_offset;
    @JsonProperty("daily")
    private List<DailyOneDay> daily = null;

    @JsonProperty("minutely")
    private List<OneMinute> oneMinuteList;

    private String formatTemperature(Double temperature) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return String.format("%s°C", decimalFormat.format(temperature));
    }

    public List<DailyWeather> toDailyWeatherList() {
        if(daily == null) return new ArrayList<>();

        return daily.stream().map(dailyOneDay -> {

            boolean isWeatherSet = dailyOneDay.weather != null && !dailyOneDay.weather.isEmpty();

            String temperatureAvgDay = dailyOneDay.temp != null && dailyOneDay.temp.day != null ? formatTemperature(dailyOneDay.temp.day) : "";
            String temperatureAvgNight = dailyOneDay.temp != null && dailyOneDay.temp.night != null ? formatTemperature(dailyOneDay.temp.night) : "";
            String temperatureMin = dailyOneDay.temp != null && dailyOneDay.temp.min != null ? formatTemperature(dailyOneDay.temp.min) : "";
            String temperatureMax = dailyOneDay.temp != null && dailyOneDay.temp.max != null ? formatTemperature(dailyOneDay.temp.max) : "";

            String weatherDescription = isWeatherSet && dailyOneDay.weather.get(0).description != null ? dailyOneDay.weather.get(0).description : "";
            Integer weatherIconDrawableId = isWeatherSet && dailyOneDay.weather.get(0).id != null ? Image.getWeatherIcon(dailyOneDay.weather.get(0).id, null, null, null) : 0;

            String dayMonth = DailyWeatherDTO.this.timezone_offset != null && dailyOneDay.dt != null ? Time.getDayMonthFormated(dailyOneDay.dt, DailyWeatherDTO.this.timezone_offset) : "";
            String sunriseTime =  DailyWeatherDTO.this.timezone_offset != null && dailyOneDay.sunrise != null ? Time.getTimeFormated(dailyOneDay.sunrise, DailyWeatherDTO.this.timezone_offset) : "";
            String sunsetTime = DailyWeatherDTO.this.timezone_offset != null && dailyOneDay.sunset != null ? Time.getTimeFormated(dailyOneDay.sunset, DailyWeatherDTO.this.timezone_offset) : "";

            return new DailyWeather(temperatureAvgDay, temperatureAvgNight, temperatureMin, temperatureMax, weatherDescription, weatherIconDrawableId, dayMonth, sunriseTime, sunsetTime);
        }).collect(Collectors.toList());
    }

    public int getTotalPrecipitationMmNextHour() {
        if(oneMinuteList == null || oneMinuteList.isEmpty()) return 0;

        return oneMinuteList.stream()
                            .map(oneMinute ->  oneMinute.precipitationMm)
                            .reduce(0, Integer::sum);
    }


    @NoArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class OneMinute implements Serializable {
        @JsonProperty("dt")
        public Long dt;

        @JsonProperty("precipitation")
        public Integer precipitationMm;
    }

    @NoArgsConstructor
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class DailyOneDay implements Serializable {

        @JsonProperty("dt")
        public Long dt;
        @JsonProperty("sunrise")
        public Long sunrise;
        @JsonProperty("sunset")
        public Long sunset;
        @JsonProperty("moonrise")
        public Long moonrise;
        @JsonProperty("moonset")
        public Long moonset;
        @JsonProperty("moon_phase")
        public Double moon_phase;
        @JsonProperty("temp")
        public Temp temp;
        @JsonProperty("feels_like")
        public FeelsLike feels_like;
        @JsonProperty("pressure")
        public Long pressure;
        @JsonProperty("humidity")
        public Long humidity;
        @JsonProperty("dew_point")
        public Double dew_point;
        @JsonProperty("wind_speed")
        public Double wind_speed;
        @JsonProperty("wind_deg")
        public Long wind_deg;
        @JsonProperty("wind_gust")
        public Double wind_gust;
        @JsonProperty("weather")
        public List<Weather> weather = null;
        @JsonProperty("clouds")
        public Long clouds;
        @JsonProperty("pop")
        public Double pop;
        @JsonProperty("uvi")
        public Long uvi;
        @JsonProperty("rain")
        public Double rain;




        @NoArgsConstructor
        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class FeelsLike implements Serializable {

            @JsonProperty("day")
            public Double day;
            @JsonProperty("night")
            public Double night;
            @JsonProperty("eve")
            public Double eve;
            @JsonProperty("morn")
            public Double morn;

        }


        @NoArgsConstructor
        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class Temp implements Serializable {

            @JsonProperty("day")
            public Double day;
            @JsonProperty("min")
            public Double min;
            @JsonProperty("max")
            public Double max;
            @JsonProperty("night")
            public Double night;
            @JsonProperty("eve")
            public Double eve;
            @JsonProperty("morn")
            public Double morn;

        }

        @NoArgsConstructor
        @Getter
        @Setter
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
        public static class Weather implements Serializable {

            @JsonProperty("id")
            public Integer id;
            @JsonProperty("main")
            public String main;
            @JsonProperty("description")
            public String description;
            @JsonProperty("icon")
            public String icon;

        }
    }
}