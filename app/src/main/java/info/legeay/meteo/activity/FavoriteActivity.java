package info.legeay.meteo.activity;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import info.legeay.meteo.R;
import info.legeay.meteo.adapter.FavoriteAdapter;
import info.legeay.meteo.model.City;

public class FavoriteActivity extends AppCompatActivity {

    public static final String KEY_PREFIX = "fav_";

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;

    private FloatingActionButton floatingActionButtonSearch;

    private List<City> cityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite);

        cityList.add(new City("Montréal" , "Légères pluies" , "22°C" , R.drawable.weather_rainy_grey));
        cityList.add(new City("New York" ,  "Ensoleillé" ,  "22°C" , R.drawable. weather_sunny_grey));
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
    }

    private void setEvents() {
        this.floatingActionButtonSearch.setOnClickListener(view -> {

            View viewAddDialog = LayoutInflater.from(this).inflate(R.layout.favorite_add_dialog , null);
            final EditText editTextCity = viewAddDialog.findViewById(R.id.edittext_favorite_dialog_input);

            final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Ajout d'une ville")
                .setView(viewAddDialog)
                .setPositiveButton("Ajouter", (dialog, which) -> {})
                .setNegativeButton("Annuler", (dialog, which) -> dialog.cancel())
                .create();

            alertDialog.show();

            // https://stackoverflow.com/a/15619098/11852990
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                Editable editableCityName = editTextCity.getText();

                if(editableCityName == null || StringUtils.isBlank(editableCityName.toString())) {
                    editTextCity.setHint("Annulez ou saisissez un nom de ville !");
                    editTextCity.setHintTextColor(getColor(R.color.colorPrimary));
                    editTextCity.setBackground(getDrawable(R.drawable.background_error));
                    return;
                }

                FavoriteActivity.this.cityList.add(new City(editTextCity.getText().toString(),  "Pluies modérées" ,  "20°C" , R.drawable. weather_rainy_grey));
                FavoriteActivity.this.favoriteAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            });


        });
    }
}