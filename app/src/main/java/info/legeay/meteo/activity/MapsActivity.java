package info.legeay.meteo.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.StringUtils;

import info.legeay.meteo.R;
import info.legeay.meteo.dto.GoogleMapActivityDTO;
import info.legeay.meteo.dto.WeatherDTO;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng latLng;
    private String cityName = "Probably not where you are";
    private boolean isDayTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Toolbar myToolbar = findViewById(R.id.toolbar_maps);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar == null) Log.d("PILMETEOAPP", "action bar is null: ");
        else {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(this.cityName);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
//            actionBar.setHomeAsUpIndicator();
        }

        this.latLng = new LatLng(-34, 151);


        Bundle extras = getIntent().getExtras();
        String coord = extras.getString(String.format("%scoord", FavoriteActivity.KEY_PREFIX));

        if(!StringUtils.isBlank(coord)) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                GoogleMapActivityDTO googleMapActivityDTO = mapper.readValue(coord, GoogleMapActivityDTO.class);
                this.latLng = new LatLng(googleMapActivityDTO.lat, googleMapActivityDTO.lon);
                this.cityName = googleMapActivityDTO.name;
                this.isDayTime = googleMapActivityDTO.isDayTime;
                if(actionBar != null) actionBar.setTitle(googleMapActivityDTO.name);

            } catch (JsonProcessingException e) {
                Log.d("PILMETEOAPP", "mapactivity create mapper.readValue: "+e.getMessage());
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if(this.isDayTime) mMap.setMapStyle(getMapStyleDayOptions());
        else mMap.setMapStyle(getMapStyleNightOptions());

        mMap.addMarker(new MarkerOptions().position(this.latLng).title(this.cityName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.latLng, 8f));

    }

    /**
     * https://mapstyle.withgoogle.com/
     * @return MapStyleOptions
     */
    private MapStyleOptions getMapStyleDayOptions() {
        return new MapStyleOptions(
    "[" +
            "  {" +
            "    \"elementType\": \"geometry\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#ebe3cd\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"elementType\": \"labels.text.fill\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#523735\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"elementType\": \"labels.text.stroke\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#f5f1e6\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"administrative\"," +
            "    \"elementType\": \"geometry.stroke\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#c9b2a6\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"administrative.land_parcel\"," +
            "    \"elementType\": \"geometry.stroke\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#dcd2be\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"administrative.land_parcel\"," +
            "    \"elementType\": \"labels\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"visibility\": \"off\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"administrative.land_parcel\"," +
            "    \"elementType\": \"labels.text.fill\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#ae9e90\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"landscape.natural\"," +
            "    \"elementType\": \"geometry\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#dfd2ae\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"poi\"," +
            "    \"elementType\": \"geometry\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#dfd2ae\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"poi\"," +
            "    \"elementType\": \"labels.text\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"visibility\": \"off\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"poi\"," +
            "    \"elementType\": \"labels.text.fill\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#93817c\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"poi.business\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"visibility\": \"off\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"poi.park\"," +
            "    \"elementType\": \"geometry.fill\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#a5b076\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"poi.park\"," +
            "    \"elementType\": \"labels.text\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"visibility\": \"off\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"poi.park\"," +
            "    \"elementType\": \"labels.text.fill\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#447530\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"road\"," +
            "    \"elementType\": \"geometry\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#f5f1e6\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"road.arterial\"," +
            "    \"elementType\": \"geometry\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#fdfcf8\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"road.highway\"," +
            "    \"elementType\": \"geometry\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#f8c967\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"road.highway\"," +
            "    \"elementType\": \"geometry.stroke\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#e9bc62\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"road.highway.controlled_access\"," +
            "    \"elementType\": \"geometry\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#e98d58\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"road.highway.controlled_access\"," +
            "    \"elementType\": \"geometry.stroke\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#db8555\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"road.local\"," +
            "    \"elementType\": \"labels\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"visibility\": \"off\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"road.local\"," +
            "    \"elementType\": \"labels.text.fill\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#806b63\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"transit.line\"," +
            "    \"elementType\": \"geometry\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#dfd2ae\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"transit.line\"," +
            "    \"elementType\": \"labels.text.fill\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#8f7d77\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"transit.line\"," +
            "    \"elementType\": \"labels.text.stroke\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#ebe3cd\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"transit.station\"," +
            "    \"elementType\": \"geometry\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#dfd2ae\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"water\"," +
            "    \"elementType\": \"geometry.fill\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#b9d3c2\"" +
            "      }" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"featureType\": \"water\"," +
            "    \"elementType\": \"labels.text.fill\"," +
            "    \"stylers\": [" +
            "      {" +
            "        \"color\": \"#92998d\"" +
            "      }" +
            "    ]" +
            "  }" +
            "]"
        );
    }

    /**
     * https://mapstyle.withgoogle.com/
     * @return MapStyleOptions
     */
    private MapStyleOptions getMapStyleNightOptions() {
        return new MapStyleOptions(
                "[" +
                        "  {" +
                        "    \"elementType\": \"geometry\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#1d2c4d\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"elementType\": \"labels.text.fill\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#8ec3b9\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"elementType\": \"labels.text.stroke\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#1a3646\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"administrative.country\"," +
                        "    \"elementType\": \"geometry.stroke\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#4b6878\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"administrative.land_parcel\"," +
                        "    \"elementType\": \"labels.text.fill\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#64779e\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"administrative.province\"," +
                        "    \"elementType\": \"geometry.stroke\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#4b6878\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"landscape.man_made\"," +
                        "    \"elementType\": \"geometry.stroke\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#334e87\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"landscape.natural\"," +
                        "    \"elementType\": \"geometry\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#023e58\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"poi\"," +
                        "    \"elementType\": \"geometry\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#283d6a\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"poi\"," +
                        "    \"elementType\": \"labels.text.fill\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#6f9ba5\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"poi\"," +
                        "    \"elementType\": \"labels.text.stroke\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#1d2c4d\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"poi.park\"," +
                        "    \"elementType\": \"geometry.fill\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#023e58\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"poi.park\"," +
                        "    \"elementType\": \"labels.text.fill\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#3C7680\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"road\"," +
                        "    \"elementType\": \"geometry\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#304a7d\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"road\"," +
                        "    \"elementType\": \"labels.text.fill\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#98a5be\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"road\"," +
                        "    \"elementType\": \"labels.text.stroke\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#1d2c4d\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"road.highway\"," +
                        "    \"elementType\": \"geometry\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#2c6675\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"road.highway\"," +
                        "    \"elementType\": \"geometry.stroke\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#255763\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"road.highway\"," +
                        "    \"elementType\": \"labels.text.fill\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#b0d5ce\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"road.highway\"," +
                        "    \"elementType\": \"labels.text.stroke\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#023e58\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"transit\"," +
                        "    \"elementType\": \"labels.text.fill\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#98a5be\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"transit\"," +
                        "    \"elementType\": \"labels.text.stroke\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#1d2c4d\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"transit.line\"," +
                        "    \"elementType\": \"geometry.fill\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#283d6a\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"transit.station\"," +
                        "    \"elementType\": \"geometry\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#3a4762\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"water\"," +
                        "    \"elementType\": \"geometry\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#0e1626\"" +
                        "      }" +
                        "    ]" +
                        "  }," +
                        "  {" +
                        "    \"featureType\": \"water\"," +
                        "    \"elementType\": \"labels.text.fill\"," +
                        "    \"stylers\": [" +
                        "      {" +
                        "        \"color\": \"#4e6d70\"" +
                        "      }" +
                        "    ]" +
                        "  }" +
                        "]"
        );
    }
}