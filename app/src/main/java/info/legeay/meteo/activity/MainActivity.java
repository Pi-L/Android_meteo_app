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
import info.legeay.meteo.databinding.ActivityMainBinding;
import info.legeay.meteo.dto.WeatherDTO;
import info.legeay.meteo.model.City;
import info.legeay.meteo.util.Network;
import info.legeay.meteo.util.Permission;
import io.reactivex.rxjava3.core.Single;

import android.os.Handler;
import android.os.Looper;
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

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String KEY_PREFIX = "main_";
    public static final int REQUEST_CODE = 321463541;

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private TextView textViewCityName;
    private TextView textViewCityWeatherDescription;
    private TextView textViewCityTemperature;
    private ImageView imageViewCityWeatherIcon;

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
                MainActivity.this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this.locationListener);

            } else if (MainActivity.this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                MainActivity.this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this.locationListener);
            }


        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //setPageVisibility();

        this.cityDAO.getFirstFavorite().subscribeOn(MeteoDatabase.dbScheduler)
                .subscribe(favCity -> {
                                setActivityData(favCity);
                                requestPosition();
                        },
                        e -> {
                            Log.d("PILMETEOAPP","main onCreate: "+e.getMessage());
                            if(!Network.isInternetAvailable(MainActivity.this)) {
                                MainActivity.this.handler.post(MainActivity.this::setPageVisibility);
                                return;
                            }
                            requestPosition();
                        });

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

    private void setActivityData(@NonNull City favCity) {

        MainActivity.this.handler.post(() -> {
            MainActivity.this.circularProgressIndicatorLoader.setVisibility(View.VISIBLE);
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

                    setUiData(city);
                }
                else Log.d("PILMETEOAPP", "weatherDTO == null");

            } catch (JsonProcessingException e) {
                Log.d("PILMETEOAPP", e.getMessage());
            } finally {
                MainActivity.this.handler.post(() -> {
                    MainActivity.this.circularProgressIndicatorLoader.setVisibility(View.GONE);
                });
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
//            intent.putExtra(String.format("%smessage", KEY_PREFIX), this.editTextMessage.getText().toString());
            startActivity(intent);
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

    private void setUiData(City city) {
        Log.d("PILMETEOAPP", "setUiData: enter");

        MainActivity.this.handler.post(() -> {
            MainActivity.this.textViewCityName.setText(city.getName());
            MainActivity.this.textViewCityWeatherDescription.setText(city.getWeatherDescription());
            MainActivity.this.textViewCityTemperature.setText(city.getCurrentTemperature());
            MainActivity.this.imageViewCityWeatherIcon.setImageResource(city.getWeatherIconDrawableId());
        });
    }
}