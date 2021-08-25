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
    private Integer weatherIconDrawableId;

    private final double lat;
    private final double lon;

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
}
