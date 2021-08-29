package info.legeay.meteo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyWeather {

    private String temperatureAvgDay;
    private String temperatureAvgNight;
    private String temperatureMin;
    private String temperatureMax;

    private String weatherDescription;
    private Integer weatherIconDrawableId;

    private String dayMonth;
    private String sunriseTime;
    private String sunsetTime;

    @Override
    public String toString() {
        return "DailyWeather{" +
                "temperatureAvgDay='" + temperatureAvgDay + '\'' +
                ", temperatureAvgNight='" + temperatureAvgNight + '\'' +
                ", temperatureMin='" + temperatureMin + '\'' +
                ", temperatureMax='" + temperatureMax + '\'' +
                ", weatherDescription='" + weatherDescription + '\'' +
                ", weatherIconDrawableId=" + weatherIconDrawableId +
                ", dayMonth='" + dayMonth + '\'' +
                ", sunriseTime='" + sunriseTime + '\'' +
                ", sunsetTime='" + sunsetTime + '\'' +
                '}';
    }
}
