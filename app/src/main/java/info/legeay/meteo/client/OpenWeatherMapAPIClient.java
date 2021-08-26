package info.legeay.meteo.client;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Locale;

import info.legeay.meteo.R;


public class OpenWeatherMapAPIClient {

//    https://openweathermap.org/current
    private static final String BASE_WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?";

    private final Context context;
    private final RequestQueue requestQueue;
    private final String weatherUrlTemplate;

    public OpenWeatherMapAPIClient(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(this.context);

        String languageCode = Locale.getDefault().getLanguage();
        this.weatherUrlTemplate = BASE_WEATHER_URL + "%s&units=metric&lang="+languageCode+"&appid=" + context.getString(R.string.owm_api_key);
    }

    private void weather(String urlParams, Response.Listener<JSONObject> response, Response.ErrorListener errorListener) {
        String url = String.format(weatherUrlTemplate, urlParams);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response,
                errorListener);

        requestQueue.add(jsonObjectRequest);
    }

    public void weatherByCoord(double lat, double lon, Response.Listener<JSONObject> response) {
        weatherByCoord(lat, lon, response, error -> { Log.d("PILMETEOAPP", error.getMessage()); });
    }

    public void weatherByCoord(double lat, double lon, Response.Listener<JSONObject> response, Response.ErrorListener errorListener) {
        String urlParams = String.format("lat=%s&lon=%s", lat, lon);
        weather(urlParams, response, errorListener);
    }

    public void weatherByCityName(String cityName, Response.Listener<JSONObject> response) {
        weatherByCityName(cityName, response, error -> { Log.d("PILMETEOAPP", "##### weatherByCityName #### JsonProcessingException" + error.getMessage()); });
    }

    public void weatherByCityName(String cityName, Response.Listener<JSONObject> response, Response.ErrorListener errorListener) {
        String urlParams = String.format("q=%s", cityName);
        weather(urlParams, response, errorListener);
    }

    public void weatherByCityId(Long cityId, Response.Listener<JSONObject> response) {
        weatherByCityId(cityId, response, error -> { Log.d("PILMETEOAPP", error.getMessage()); });
    }

    public void weatherByCityId(Long cityId, Response.Listener<JSONObject> response, Response.ErrorListener errorListener) {
        String urlParams = String.format("id=%d", cityId);
        weather(urlParams, response, errorListener);
    }

}
