package info.legeay.meteo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import info.legeay.meteo.R;
import info.legeay.meteo.adapter.FavoriteAdapter;
import info.legeay.meteo.client.OpenWeatherMapAPIClient;
import info.legeay.meteo.dto.WeatherDTO;
import info.legeay.meteo.model.City;

public class FavoriteActivity extends AppCompatActivity {

    public static final String KEY_PREFIX = "fav_";

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private Handler handler;

    private FloatingActionButton floatingActionButtonSearch;

    private OpenWeatherMapAPIClient openWeatherMapAPIClient;

    private List<City> cityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite);

        this.handler = new Handler();
        openWeatherMapAPIClient = new OpenWeatherMapAPIClient(this);

        this.recyclerView = findViewById(R.id.favorite_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);
        this.favoriteAdapter = new FavoriteAdapter(this, cityList);
        this.recyclerView.setAdapter(this.favoriteAdapter);

        this.floatingActionButtonSearch = findViewById(R.id.floatingactionbutton_favorite_search);


//        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_favorite);
//        myToolbar.setTitleTextColor(Color.WHITE);
//        setSupportActionBar(myToolbar);
        this.setEvents();

//        addCityById(4717560L);
//        addCityById(2972191L);
//        addCityById(5128581L);
//        addCityById(2193733L);

    }

    private void setEvents() {
        this.floatingActionButtonSearch.setOnClickListener(view -> {

            View viewAddDialog = LayoutInflater.from(this).inflate(R.layout.favorite_add_dialog, null);
            final EditText editTextCity = viewAddDialog.findViewById(R.id.edittext_favorite_dialog_input);

            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Ajout d'une ville")
                    .setView(viewAddDialog)
                    .setPositiveButton("Ajouter", (dialog, which) -> {
                    })
                    .setNegativeButton("Annuler", (dialog, which) -> dialog.cancel())
                    .create();

            alertDialog.show();

            // https://stackoverflow.com/a/15619098/11852990
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                Editable editableCityName = editTextCity.getText();

                if (editableCityName == null || StringUtils.isBlank(editableCityName.toString())) {
                    editTextCity.setHint("Annulez ou saisissez un nom de ville !");
                    editTextCity.setHintTextColor(getColor(R.color.colorPrimary));
                    editTextCity.setBackground(getDrawable(R.drawable.background_error));
                    return;
                }

                FavoriteActivity.this.addCityByName(editTextCity.getText().toString());

                alertDialog.dismiss();
            });


        });
    }

    private void addCityByCoord(Double lat, Double lon) {

        openWeatherMapAPIClient.weatherByCoord(lat, lon, response -> {

            ObjectMapper mapper = new ObjectMapper();
            try {
                WeatherDTO weatherDTO = mapper.readValue(response.toString(), WeatherDTO.class);

                if(weatherDTO != null) {
                    FavoriteActivity.this.cityList.add(weatherDTO.toCity());
                    FavoriteActivity.this.favoriteAdapter.notifyDataSetChanged();
                }
                else Log.d("PIL", "weatherDTO == null");

            } catch (JsonProcessingException e) {
                Log.d("PIL", e.getMessage());
            } finally {
                Log.d("PIL", FavoriteActivity.this.cityList.toString());
            }
        });
    }

    private void addCityByName(String cityName) {

        openWeatherMapAPIClient.weatherByCityName(
                cityName,
                response -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        WeatherDTO weatherDTO = mapper.readValue(response.toString(), WeatherDTO.class);
                        City city = weatherDTO != null ? weatherDTO.toCity() : null;

                        if(city != null) {
                            Log.d("PIL", "weatherDTO != null");
                            FavoriteActivity.this.cityList.add(city);
                            FavoriteActivity.this.favoriteAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("PIL", "weatherDTO == null");
                            FavoriteActivity.this.handler.post(() ->
                                Toast.makeText(FavoriteActivity.this, String.format("Ville %s non trouvée", cityName), Toast.LENGTH_LONG).show());
                        }

                    } catch (JsonProcessingException e) {
                        Log.d("PIL", "##### addCityByName #### JsonProcessingException" + e.getMessage());
                    }
                },
                error -> {
                    Log.d("PIL", String.format("FavoriteActivity - addCityByName error: %s", error.getMessage()));
                    FavoriteActivity.this.handler.post(() ->
                            Toast.makeText(FavoriteActivity.this, String.format("Ville %s non trouvée", cityName), Toast.LENGTH_LONG).show());

                }
        );
    }

    private void addCityById(Long id) {

        openWeatherMapAPIClient.weatherByCityId(id, response -> {

            ObjectMapper mapper = new ObjectMapper();
            try {
                WeatherDTO weatherDTO = mapper.readValue(response.toString(), WeatherDTO.class);

                if(weatherDTO != null) {
                    FavoriteActivity.this.cityList.add(weatherDTO.toCity());
                    FavoriteActivity.this.favoriteAdapter.notifyDataSetChanged();
                }
                else Log.d("PIL", "weatherDTO == null");

            } catch (JsonProcessingException e) {
                Log.d("PIL", e.getMessage());
            } finally {
                Log.d("PIL", FavoriteActivity.this.cityList.toString());
            }
        });
    }
}