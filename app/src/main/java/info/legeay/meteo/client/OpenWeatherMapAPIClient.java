package info.legeay.meteo.client;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import info.legeay.meteo.R;
import info.legeay.meteo.dto.WeatherDTO;


public class OpenWeatherMapAPIClient {

    private final Context context;
    private final RequestQueue requestQueue;

    public OpenWeatherMapAPIClient(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(this.context);

    }

    public void weather(double lat, double lon, Response.Listener<JSONObject> response) {

        String url = String.format("https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s", lat, lon, context.getString(R.string.owm_api_key));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response
                , error -> { Log.d("PIL", error.getMessage()); }
        );

        requestQueue.add(jsonObjectRequest);
    }

}
