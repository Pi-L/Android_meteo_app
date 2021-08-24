package info.legeay.meteo.activity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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

    private FloatingActionButton floatingActionButtonSearch;

    private OpenWeatherMapAPIClient openWeatherMapAPIClient;

    private List<City> cityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite);

        openWeatherMapAPIClient = new OpenWeatherMapAPIClient(this);

//        cityList.add(new City(1L, "Montréal", "Légères pluies", "22°C", "https://openweathermap.org/img/wn/10d@2x.png", 40.716709, -74.005698));
//        cityList.add(new City(2L, "New York", "Ensoleillé", "22°C", "https://openweathermap.org/img/wn/10d@2x.png", 40.716709, -74.005698));
//        cityList.add(new City("Paris" ,  "Nuageux" ,  "24°C" , R.drawable. weather_foggy_grey));
//        cityList.add(new City("Toulouse" ,  "Pluies modérées" ,  "20°C" , R.drawable. weather_rainy_grey));
//        cityList.add(new City("Montréal" , "Légères pluies" , "22°C" , R.drawable.weather_rainy_grey));
//        cityList.add(new City("New York" ,  "Ensoleillé" ,  "22°C" , R.drawable. weather_sunny_grey));
//        cityList.add(new City("Paris" ,  "Nuageux" ,  "24°C" , R.drawable. weather_foggy_grey));
//        cityList.add(new City("Toulouse" ,  "Pluies modérées" ,  "20°C" , R.drawable. weather_rainy_grey));
//        cityList.add(new City("Montréal" , "Légères pluies" , "22°C" , R.drawable.weather_rainy_grey));
//        cityList.add(new City("New York" ,  "Ensoleillé" ,  "22°C" , R.drawable. weather_sunny_grey));
//        cityList.add(new City("Paris" ,  "Nuageux" ,  "24°C" , R.drawable. weather_foggy_grey));
//        cityList.add(new City("Toulouse" ,  "Pluies modérées" ,  "20°C" , R.drawable. weather_rainy_grey));
//        cityList.add(new City("Montréal" , "Légères pluies" , "22°C" , R.drawable.weather_rainy_grey));
//        cityList.add(new City("New York" ,  "Ensoleillé" ,  "22°C" , R.drawable. weather_sunny_grey));
//        cityList.add(new City("Paris" ,  "Nuageux" ,  "24°C" , R.drawable. weather_foggy_grey));
//        cityList.add(new City("Toulouse" ,  "Pluies modérées" ,  "20°C" , R.drawable. weather_rainy_grey));

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


        addCityByCoord(40.716709, -74.005698);
        addCityByCoord(47.390026, 0.688891);
        addCityByCoord(23.634501, -102.552784);
        addCityByCoord(39.904211, 116.407395);
        addCityByCoord(40.4167754, -3.7037902);
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

                FavoriteActivity.this.cityList.add(new City(1L, editTextCity.getText().toString(), "Pluies modérées", "20°C", "https://openweathermap.org/img/wn/10d@2x.png", 40.716709, -74.005698));
                FavoriteActivity.this.favoriteAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            });


        });
    }

    private void addCityByCoord(Double lat, Double lon) {

        openWeatherMapAPIClient.weather(lat, lon, response -> {

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