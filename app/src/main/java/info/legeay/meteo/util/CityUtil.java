package info.legeay.meteo.util;

import java.util.List;

import info.legeay.meteo.model.City;

public class CityUtil {
    
    public static void setCitiesPosition(List<City> cityList) {
        for (int i = 0; i < cityList.size(); i++) {
            cityList.get(i).setFavPosition(i);
        }
    }
}
