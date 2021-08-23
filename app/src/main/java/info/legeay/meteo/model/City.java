package info.legeay.meteo.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class City {

//    private Long id;
    private String name;
    private String description;
    private String temperature;
    private int weatherIcon;

//    private String xGpsPos;
//    private String yGpsPos;

}
