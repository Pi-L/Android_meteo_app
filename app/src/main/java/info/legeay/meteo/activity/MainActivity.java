package info.legeay.meteo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import info.legeay.meteo.R;
import info.legeay.meteo.client.OpenWeatherMapAPIClient;
import info.legeay.meteo.databinding.ActivityMainBinding;
import info.legeay.meteo.dto.WeatherDTO;
import info.legeay.meteo.model.City;
import info.legeay.meteo.util.Network;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    public static final String KEY_PREFIX = "main_";

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private TextView textViewCityName;
    private TextView textViewCityWeatherDescription;
    private TextView textViewCityTemperature;
    private ImageView imageViewCityWeatherIcon;

    private TextView textViewInternetKO;
    private CircularProgressIndicator circularProgressIndicatorLoader;

    private LinearLayout linearlayoutMeteoDisplay;
    private EditText editTextMessage;

    private FloatingActionButton buttonFavorite;

    private Handler handler;

    private OpenWeatherMapAPIClient openWeatherMapAPIClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        this.handler = new Handler();
        this.openWeatherMapAPIClient = new OpenWeatherMapAPIClient(this);

        this.textViewCityName = findViewById(R.id.textview_main_city_name);
        this.textViewCityWeatherDescription = findViewById(R.id.textview_main_city_weather_description) ;
        this.textViewCityTemperature = findViewById(R.id.textview_main_city_temperature) ;
        this.imageViewCityWeatherIcon = findViewById(R.id.imageview_main_city_weather_icon) ;

        this.textViewInternetKO = findViewById(R.id.textview_internet_ko);
        this.circularProgressIndicatorLoader = findViewById(R.id.circularprogressindicator_main_loader);

        this.linearlayoutMeteoDisplay = findViewById(R.id.linearlayout_meteo_display);
        this.buttonFavorite = findViewById(R.id.button_favorite);
        this.editTextMessage = findViewById(R.id.edittext_message);

        this.setEvents();

        setPageVisibility();

        setActivityData();


    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setPageVisibility();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void setActivityData() {
        if(!Network.isInternetAvailable(this)) return;
        this.circularProgressIndicatorLoader.setVisibility(View.VISIBLE);

        openWeatherMapAPIClient.weatherByCityId(2972191L, response -> {

            ObjectMapper mapper = new ObjectMapper();
            try {
                WeatherDTO weatherDTO = mapper.readValue(response.toString(), WeatherDTO.class);

                if(weatherDTO != null) {
                    City city = weatherDTO.toCity();

                    MainActivity.this.handler.post(() -> {

                        MainActivity.this.textViewCityName.setText(city.getName());
                        MainActivity.this.textViewCityWeatherDescription.setText(city.getWeatherDescription());
                        MainActivity.this.textViewCityTemperature.setText(city.getCurrentTemperature());
                        Picasso.get()
                                .load(city.getWeatherIconUrl())
                                .placeholder(R.drawable.weather_rainy_grey)
                                .into(MainActivity.this.imageViewCityWeatherIcon);
                    });
                }
                else Log.d("PIL", "weatherDTO == null");

            } catch (JsonProcessingException e) {
                Log.d("PIL", e.getMessage());
            } finally {
                MainActivity.this.circularProgressIndicatorLoader.setVisibility(View.GONE);
            }
        });

        // todo: manage on error
    }

    private void setPageVisibility() {
        boolean isInternetAccessible = Network.isInternetAvailable(this);

        if(isInternetAccessible) {
            this.textViewInternetKO.setVisibility(View.GONE);
            this.linearlayoutMeteoDisplay.setVisibility(View.VISIBLE);
            return;
        }

        this.textViewInternetKO.setVisibility(View.VISIBLE);
        this.linearlayoutMeteoDisplay.setVisibility(View.GONE);

    }

    private void setEvents() {
        this.buttonFavorite.setOnClickListener(view -> {
            Intent intent = new Intent(this, FavoriteActivity.class);
            intent.putExtra(String.format("%smessage", KEY_PREFIX), this.editTextMessage.getText().toString());
            startActivity(intent);
        });

    }
}