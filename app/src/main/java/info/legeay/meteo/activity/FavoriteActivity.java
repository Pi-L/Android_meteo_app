package info.legeay.meteo.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import info.legeay.meteo.R;
import info.legeay.meteo.adapter.FavoriteAdapter;
import info.legeay.meteo.model.City;

public class FavoriteActivity extends AppCompatActivity {

    public static final String KEY_PREFIX = "fav_";

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;

    private List<City> cityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite);

        cityList.add(new City("Montréal" , "Légères pluies" , "22°C" , R.drawable.weather_rainy_grey));
        cityList.add(new City("New York" ,  "Ensoleillé" ,  "22°C" , R.drawable. weather_sunny_grey));
        cityList.add(new City("Paris" ,  "Nuageux" ,  "24°C" , R.drawable. weather_foggy_grey));
        cityList.add(new City("Toulouse" ,  "Pluies modérées" ,  "20°C" , R.drawable. weather_rainy_grey));
        cityList.add(new City("Montréal" , "Légères pluies" , "22°C" , R.drawable.weather_rainy_grey));
        cityList.add(new City("New York" ,  "Ensoleillé" ,  "22°C" , R.drawable. weather_sunny_grey));
        cityList.add(new City("Paris" ,  "Nuageux" ,  "24°C" , R.drawable. weather_foggy_grey));
        cityList.add(new City("Toulouse" ,  "Pluies modérées" ,  "20°C" , R.drawable. weather_rainy_grey));
        cityList.add(new City("Montréal" , "Légères pluies" , "22°C" , R.drawable.weather_rainy_grey));
        cityList.add(new City("New York" ,  "Ensoleillé" ,  "22°C" , R.drawable. weather_sunny_grey));
        cityList.add(new City("Paris" ,  "Nuageux" ,  "24°C" , R.drawable. weather_foggy_grey));
        cityList.add(new City("Toulouse" ,  "Pluies modérées" ,  "20°C" , R.drawable. weather_rainy_grey));
        cityList.add(new City("Montréal" , "Légères pluies" , "22°C" , R.drawable.weather_rainy_grey));
        cityList.add(new City("New York" ,  "Ensoleillé" ,  "22°C" , R.drawable. weather_sunny_grey));
        cityList.add(new City("Paris" ,  "Nuageux" ,  "24°C" , R.drawable. weather_foggy_grey));
        cityList.add(new City("Toulouse" ,  "Pluies modérées" ,  "20°C" , R.drawable. weather_rainy_grey));

        this.recyclerView = findViewById(R.id.favorite_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);
        this.favoriteAdapter = new FavoriteAdapter(this, cityList);
        this.recyclerView.setAdapter(this.favoriteAdapter);


//        this.textViewMessage = findViewById(R.id.textview_favorite_message);
//
//        Bundle extras = getIntent().getExtras();
//        String message = extras.getString(String.format("%smessage", MainActivity.KEY_PREFIX));

//        Log.d("PIL", "fav message: "+message);

//        if(message == null || message.isEmpty()) this.textViewMessage.setVisibility(View.GONE);
//        else this.textViewMessage.setText(String.format("Message : %s", message));

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_favorite);
//        myToolbar.setTitleTextColor(Color.WHITE);
//        setSupportActionBar(myToolbar);


    }
}