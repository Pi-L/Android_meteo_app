package info.legeay.meteo.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class City {

    private Long id;
    private String name;
    private String weatherDescription;
    private String currentTemperature;
    private String weatherIconUrl;

    private final double lat;
    private final double lon;

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weatherDescription='" + weatherDescription + '\'' +
                ", currentTemperature='" + currentTemperature + '\'' +
                ", weatherIconUrl='" + weatherIconUrl + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
