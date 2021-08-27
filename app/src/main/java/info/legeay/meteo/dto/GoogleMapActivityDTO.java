package info.legeay.meteo.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@NoArgsConstructor
@AllArgsConstructor
public class GoogleMapActivityDTO implements Serializable {

    public String name;
    public Double lat;
    public Double lon;
    public Boolean isDayTime;

    @Override
    public String toString() {
        return "GoogleMapActivityDTO{" +
                "name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", isDayTime=" + isDayTime +
                '}';
    }
}
