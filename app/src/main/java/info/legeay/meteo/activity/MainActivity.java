package info.legeay.meteo.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import info.legeay.meteo.R;
import info.legeay.meteo.client.OpenWeatherMapAPIClient;
import info.legeay.meteo.dao.CityDAO;
import info.legeay.meteo.database.MeteoDatabase;
import info.legeay.meteo.dto.DailyWeatherDTO;
import info.legeay.meteo.dto.WeatherDTO;
import info.legeay.meteo.model.City;
import info.legeay.meteo.model.DailyWeather;
import info.legeay.meteo.util.Network;
import info.legeay.meteo.util.Permission;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String KEY_PREFIX = "main_";
    public static final int REQUEST_CODE = 321463541;

    private AppBarConfiguration appBarConfiguration;

    private TextView textViewCityName;
    private TextView textViewCityWeatherDescription;
    private TextView textViewCityTemperature;
    private ImageView imageViewCityWeatherIcon;
    private MenuItem itemLocationMainMenu;

    private TextView textViewInternetKO;
    private CircularProgressIndicator circularProgressIndicatorLoader;

    private LinearLayout linearlayoutMeteoDisplay;
//    private EditText editTextMessage;

    private FloatingActionButton buttonFavorite;

    private Handler handler;

    private OpenWeatherMapAPIClient openWeatherMapAPIClient;

    private MeteoDatabase meteoDatabase;
    private CityDAO cityDAO;

    private SharedPreferences preferences;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private Permission locationPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(myToolbar);


        this.handler = new Handler();
        this.openWeatherMapAPIClient = new OpenWeatherMapAPIClient(this);

        this.textViewCityName = findViewById(R.id.textview_main_city_name);
        this.textViewCityWeatherDescription = findViewById(R.id.textview_main_city_weather_description);
        this.textViewCityTemperature = findViewById(R.id.textview_main_city_temperature);
        this.imageViewCityWeatherIcon = findViewById(R.id.imageview_main_city_weather_icon);

        this.textViewInternetKO = findViewById(R.id.textview_internet_ko);
        this.circularProgressIndicatorLoader = findViewById(R.id.circularprogressindicator_main_loader);

        this.linearlayoutMeteoDisplay = findViewById(R.id.linearlayout_meteo_display);
        this.buttonFavorite = findViewById(R.id.button_favorite);

        this.setEvents();

        //setPageVisibility();

        this.meteoDatabase = MeteoDatabase.getDatabase(this);

        this.cityDAO = meteoDatabase.cityDAO();

//        this.locationPermission = new Permission(this, "coarse_location");
        this.setLocationListener();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("PILMETEOAPP","main onstart : enter ");
        if (Network.isInternetAvailable(MainActivity.this)) MainActivity.this.textViewInternetKO.setVisibility(View.GONE);
        else MainActivity.this.textViewInternetKO.setVisibility(View.VISIBLE);

        this.cityDAO.getFirstFavorite().subscribeOn(MeteoDatabase.dbScheduler)
                .subscribe(this::setActivityData,
                        e -> {
                            Log.d("PILMETEOAPP","main onstart : getFirstFavorite error : "+e.getMessage());
                            if(!Network.isInternetAvailable(MainActivity.this)) {
                                MainActivity.this.setPageVisibility();
                                return;
                            }
                            setDefault();
//                            requestPosition();
                        });
        
    }


    private void setActivityData(@NonNull City favCity) {

        MainActivity.this.handler.post(() -> {
            setUiData(favCity);
        });


        if(!Network.isInternetAvailable(MainActivity.this)) {
            MainActivity.this.handler.post(() -> {
                MainActivity.this.circularProgressIndicatorLoader.setVisibility(View.GONE);
            });
            return;
        }

        openWeatherMapAPIClient.weatherByCityId(favCity.getId(), response -> {

            ObjectMapper mapper = new ObjectMapper();
            try {
                WeatherDTO weatherDTO = mapper.readValue(response.toString(), WeatherDTO.class);

                if(weatherDTO != null) {
                    City city = weatherDTO.toCity();
                    MainActivity.this.setUiData(city);
                }
                else Log.d("PILMETEOAPP", "weatherDTO == null");

            } catch (JsonProcessingException e) {
                Log.d("PILMETEOAPP", e.getMessage());
            }
        });

        openWeatherMapAPIClient.dailyByCoord(favCity.getLat(), favCity.getLon(), 
                response -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        DailyWeatherDTO dailyWeatherDTO = mapper.readValue(response.toString(), DailyWeatherDTO.class);
                        int nextHourPrecipitationMm = dailyWeatherDTO.getTotalPrecipitationMmNextHour();

                        if(dailyWeatherDTO != null) {
                            List<DailyWeather> dailyWeatherList = dailyWeatherDTO.toDailyWeatherList();
                            
                            if(dailyWeatherList == null || dailyWeatherList.isEmpty()) return;
                            MainActivity.this.setUiDailyForecast(dailyWeatherList, nextHourPrecipitationMm);
                        }
                        else Log.d("PILMETEOAPP", "dailyWeatherDTO == null");

                    } catch (JsonProcessingException e) {
                        Log.d("PILMETEOAPP", e.getMessage());
                    }
                },
                error -> {
                    Log.d("PILMETEOAPP", error.getMessage());
                }
            );
    }

    private void setUiData(City city) {
        Log.d("PILMETEOAPP", "setUiData: enter");

        MainActivity.this.handler.post(() -> {
            MainActivity.this.textViewCityName.setText(city.getName());
            MainActivity.this.textViewCityWeatherDescription.setText(city.getWeatherDescription());
            MainActivity.this.textViewCityTemperature.setText(city.getCurrentTemperature());
            MainActivity.this.imageViewCityWeatherIcon.setImageResource(city.getWeatherIconDrawableId());
            MainActivity.this.circularProgressIndicatorLoader.setVisibility(View.GONE);
        });
    }

    private void setUiDailyForecast(List<DailyWeather> dailyWeatherList, int nextHourPrecipitationMm) {
        Log.d("PILMETEOAPP", "setUiDailyForecast: enter");

        MainActivity.this.handler.post(() -> {
            int[] tab = {R.id.item_forecast_1, R.id.item_forecast_2, R.id.item_forecast_3, R.id.item_forecast_4, R.id.item_forecast_5, R.id.item_forecast_6};

            for (int i = 0; i < 6 && i < dailyWeatherList.size(); i++) {
                DailyWeather dailyWeather = dailyWeatherList.get(i);
                View view = findViewById(tab[i]);
                ((TextView) view.findViewById(R.id.textview_forecast_item_date)).setText(dailyWeather.getDayMonth());
                ((ImageView) view.findViewById(R.id.imageview_forecast_item_weather_icon)).setImageDrawable(getDrawable(dailyWeather.getWeatherIconDrawableId()));
                ((TextView) view.findViewById(R.id.textview_forecast_item_temperature_avg_day)).setText(dailyWeather.getTemperatureAvgDay());
                ((TextView) view.findViewById(R.id.textview_forecast_item_temperature_max)).setText(dailyWeather.getTemperatureMax());
                ((TextView) view.findViewById(R.id.textview_forecast_item_temperature_min)).setText(dailyWeather.getTemperatureMin());
                ((TextView) view.findViewById(R.id.textview_forecast_item_sunrise)).setText(dailyWeather.getSunriseTime());
                ((TextView) view.findViewById(R.id.textview_forecast_item_sunset)).setText(dailyWeather.getSunsetTime());
            }

            ImageView imageviewWaterdrop1 = findViewById(R.id.imageview_main_waterdrop_1);
            ImageView imageviewWaterdrop2 = findViewById(R.id.imageview_main_waterdrop_2);
            ImageView imageviewWaterdrop3 = findViewById(R.id.imageview_main_waterdrop_3);

            Log.d("PILMETEOAPP", "RAIIIINNNNNN: "+nextHourPrecipitationMm);


            if(nextHourPrecipitationMm > 0) imageviewWaterdrop1.setVisibility(View.VISIBLE);
            else imageviewWaterdrop1.setVisibility(View.GONE);

            if(nextHourPrecipitationMm > 100) imageviewWaterdrop2.setVisibility(View.VISIBLE);
            else imageviewWaterdrop2.setVisibility(View.GONE);

            if(nextHourPrecipitationMm > 400) imageviewWaterdrop3.setVisibility(View.VISIBLE);
            else imageviewWaterdrop3.setVisibility(View.GONE);

        });
    }

    private void setDefault() {
        MainActivity.this.handler.post(() -> {
            MainActivity.this.textViewCityName.setText("Se localiser ou choisir un favoris");
            MainActivity.this.textViewCityWeatherDescription.setText("");
            MainActivity.this.textViewCityTemperature.setText("");
            MainActivity.this.imageViewCityWeatherIcon.setImageResource(android.R.color.transparent);
        });
    }

    private void setPageVisibility() {

        MainActivity.this.handler.post(() -> {
            if (Network.isInternetAvailable(MainActivity.this)) {
                MainActivity.this.linearlayoutMeteoDisplay.setVisibility(View.VISIBLE);
                MainActivity.this.buttonFavorite.setVisibility(View.VISIBLE);
                return;
            }

            MainActivity.this.linearlayoutMeteoDisplay.setVisibility(View.GONE);
            MainActivity.this.buttonFavorite.setVisibility(View.GONE);
        });
    }


    private void setMainCityByCoord(Double lat, Double lon) {

        Log.d("PILMETEOAPP", "setMainCityByCoord: "+lat+" "+lon);

        openWeatherMapAPIClient.weatherByCoord(
                lat, lon,
                response -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        WeatherDTO weatherDTO = mapper.readValue(response.toString(), WeatherDTO.class);
                        City city = weatherDTO != null ? weatherDTO.toCity() : null;

                        Log.d("PILMETEOAPP", "setMainCityByCoord: dto conversion -> "+((city == null) ? "null" : city.getName()));

                        if(city == null) return;

                        MainActivity.this.cityDAO.getAllSortedByFavPositionAsc()
                                .subscribeOn(MeteoDatabase.dbScheduler)
                                .subscribe(
                                        currentCityList -> {
                                            Log.d("PILMETEOAPP", "setMainCityByCoord: currentCityList.size - "+currentCityList.size());

                                            if(currentCityList.stream().anyMatch(currentCity -> currentCity.getId().equals(city.getId()))) return;

                                            city.setFavPosition(0);
                                            setUiData(city);

                                            currentCityList.forEach(currentCity -> currentCity.setFavPosition(currentCity.getFavPosition() + 1));

                                            MainActivity.this.cityDAO.updateAll(currentCityList).subscribeOn(MeteoDatabase.dbScheduler).subscribe(
                                                    () -> MainActivity.this.cityDAO.insert(city).subscribeOn(MeteoDatabase.dbScheduler).subscribe(
                                                            () -> Log.d("PILMETEOAPP", "setMainCityByCoord error: MainActivity.this.cityDAO.insert - SUCCES"),
                                                            errorInsert -> Log.d("PILMETEOAPP", "setMainCityByCoord error: MainActivity.this.cityDAO.insert - "+errorInsert.getMessage())
                                                    ),
                                                    errorUpdate -> Log.d("PILMETEOAPP", "setMainCityByCoord error: MainActivity.this.cityDAO.updateAll - "+errorUpdate.getMessage())
                                            );

                                        },
                                        e -> Log.d("PILMETEOAPP", "setMainCityByCoord error: MainActivity.this.cityDAO.getAllSortedByFavPositionAsc - "+e.getMessage())
                                );

                    } catch (JsonProcessingException e) {
                        Log.d("PILMETEOAPP", e.getMessage());
                    } finally {
        //                Log.d("PILMETEOAPP", FavoriteActivity.this.cityList.toString());
                    }
                },
                error -> Log.d("PILMETEOAPP", String.format("MainActivity - addCityByName error: %s", error.getMessage()))
        );
    }

    private void setEvents() {
        this.buttonFavorite.setOnClickListener(view -> {
            Intent intent = new Intent(this, FavoriteActivity.class);
            startActivity(intent);
        });
    }

    private void setLocationListener() {
        this.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // onLocationChanged
        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d("PILMETEOAPP", "onLocationChanged: "+location);

                MainActivity.this.locationManager.removeUpdates(MainActivity.this.locationListener);

                double lat = location.getLatitude();
                double lon = location.getLongitude();

                MainActivity.this.setMainCityByCoord(lat, lon);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {

            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Log.d("PILMETEOAPP", "onProviderDisabled: ----------------------------------------------");
                MainActivity.this.locationManager.removeUpdates(MainActivity.this.locationListener);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        Log.d("PILMETEOAPP", "setLocationListener: perm value : "+ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION));
    }

    private void requestPosition() {
        MainActivity.this.handler.post(() -> {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

                //            Log.d("PILMETEOAPP", "locationPermission. : "+MainActivity.this.locationPermission.getName()+" "+MainActivity.this.locationPermission.getStatus().name());

                //            if(MainActivity.this.locationPermission.isRefused()) return;

                ActivityCompat.requestPermissions ( MainActivity.this, new String[]{ android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_CODE);
                return;
            }

            if(MainActivity.this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                MainActivity.this.circularProgressIndicatorLoader.setVisibility(View.VISIBLE);
                MainActivity.this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this.locationListener);

            } else if (MainActivity.this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                MainActivity.this.circularProgressIndicatorLoader.setVisibility(View.VISIBLE);
                MainActivity.this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this.locationListener);
            } else {
                Toast.makeText(MainActivity.this, "Veuillez activer la localisation svp", Toast.LENGTH_LONG).show();
            }


        });
    }

    @Override
    public void onRequestPermissionsResult (int requestCode,
                                            @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {

        if(requestCode != REQUEST_CODE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        };

        if(Arrays.stream(grantResults).anyMatch(perm -> perm ==  PackageManager.PERMISSION_GRANTED)) {
//            this.locationPermission.setStatus(Permission.PermissionEnum.ACCEPTED);
            requestPosition();
            return;
        }

//        this.locationPermission.setStatus(Permission.PermissionEnum.REFUSED);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_location) {
            Log.d("PILMETEOAPP", "onOptionsItemSelected: location button clicked ");
            requestPosition();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.itemLocationMainMenu = menu.findItem(R.id.action_location);
        if (Network.isInternetAvailable(MainActivity.this)) this.itemLocationMainMenu.setVisible(true);
        else this.itemLocationMainMenu.setVisible(false);
        return true;
    }

}