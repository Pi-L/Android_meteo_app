package info.legeay.meteo.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import info.legeay.meteo.R;
import info.legeay.meteo.adapter.FavoriteAdapter;
import info.legeay.meteo.client.OpenWeatherMapAPIClient;
import info.legeay.meteo.database.DataBaseHelper;
import info.legeay.meteo.dto.WeatherDTO;
import info.legeay.meteo.model.City;
import info.legeay.meteo.util.CityUtil;

public class FavoriteActivity extends AppCompatActivity {

    public static final String KEY_PREFIX = "fav_";

    private DataBaseHelper dataBaseHelper;

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private Handler handler;

    private FloatingActionButton floatingActionButtonSearch;
    private CircularProgressIndicator circularProgressIndicatorLoader;

    private OpenWeatherMapAPIClient openWeatherMapAPIClient;

    private final List<City> cityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite);
//        this.deleteDatabase(DataBaseHelper.DATABASE_NAME);
        this.dataBaseHelper = new DataBaseHelper(this);
        this.handler = new Handler();
        openWeatherMapAPIClient = new OpenWeatherMapAPIClient(this);

        this.recyclerView = findViewById(R.id.favorite_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);
        this.favoriteAdapter = new FavoriteAdapter(this, cityList);
        this.recyclerView.setAdapter(this.favoriteAdapter);

        this.floatingActionButtonSearch = findViewById(R.id.floatingactionbutton_favorite_search);
        this.circularProgressIndicatorLoader = findViewById(R.id.circularprogressindicator_favorite_loader);

        this.setEvents();

        this.attachItemTouchHelperToRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.cityList.clear();
        this.cityList.addAll(this.dataBaseHelper.getCityList());
        updateCityList();
        this.favoriteAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.dataBaseHelper.removeAll();
        this.dataBaseHelper.insertAll(this.cityList);
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

    private void attachItemTouchHelperToRecyclerView() {

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN|ItemTouchHelper.UP,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull
                            RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                        Log.d("PIL", "onMove: ");

                        int fromPosition = viewHolder.getBindingAdapterPosition();
                        int toPosition = target.getBindingAdapterPosition();
                        Collections.swap(FavoriteActivity.this.cityList, fromPosition, toPosition);

                        FavoriteActivity.this.cityList.get(fromPosition).setFavPosition(fromPosition);
                        FavoriteActivity.this.cityList.get(toPosition).setFavPosition(toPosition);

                        FavoriteActivity.this.favoriteAdapter.notifyItemMoved(fromPosition, toPosition);

                        return true;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                        FavoriteAdapter.ViewHolder faViewHolder = (FavoriteAdapter.ViewHolder) viewHolder;

                        if(ItemTouchHelper.RIGHT == direction) {
                            FavoriteActivity.this.favoriteAdapter.notifyDataSetChanged();
                            return;
                        }

                        City city = faViewHolder.city;
                        int position = faViewHolder.getBindingAdapterPosition();

                        FavoriteActivity.this.cityList.remove(position);

                        for (int i = position; i < FavoriteActivity.this.cityList.size(); i++) {
                            City tempCity =  FavoriteActivity.this.cityList.get(i);
                            tempCity.setFavPosition(tempCity.getFavPosition() - 1);
                        }

                        FavoriteActivity.this.favoriteAdapter.notifyItemRemoved(position);

                        Snackbar.make(
                                findViewById(R.id.coordinatorlayout_favorite_container),
                                city.getName()+" est supprimée", Snackbar.LENGTH_LONG)
                                .setAction("Annuler", view -> {

                                    FavoriteActivity.this.cityList.add(position, city);

                                    for (int i = position + 1; i < FavoriteActivity.this.cityList.size(); i++) {
                                        City tempCity =  FavoriteActivity.this.cityList.get(i);
                                        tempCity.setFavPosition(tempCity.getFavPosition() + 1);
                                    }

                                    FavoriteActivity.this.favoriteAdapter.notifyItemInserted(position);
                                })
                                .show();
                    }
                });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void addCityByName(String cityName) {
        this.circularProgressIndicatorLoader.setVisibility(View.VISIBLE);

        openWeatherMapAPIClient.weatherByCityName(
                cityName,
                response -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        WeatherDTO weatherDTO = mapper.readValue(response.toString(), WeatherDTO.class);
                        City city = weatherDTO != null ? weatherDTO.toCity() : null;

                        if(city == null) {
                            Log.d("PIL", "weatherDTO == null");
                            FavoriteActivity.this.handler.post(() ->
                                    Toast.makeText(FavoriteActivity.this, String.format("Ville %s non trouvée", cityName), Toast.LENGTH_LONG).show());
                            return;
                        }

                        if(this.cityList.stream().anyMatch(currentCity -> currentCity.getId().equals(city.getId()))) {
                            FavoriteActivity.this.handler.post(() ->
                                    Toast.makeText(FavoriteActivity.this, String.format("Ville %s deja presente", cityName), Toast.LENGTH_LONG).show());
                            return;
                        }

                        int position = FavoriteActivity.this.cityList.size();
                        FavoriteActivity.this.cityList.add(city);
                        FavoriteActivity.this.favoriteAdapter.notifyItemInserted(position);

                    } catch (JsonProcessingException e) {
                        Log.d("PIL", "##### addCityByName #### JsonProcessingException" + e.getMessage());
                    } finally {
                        this.circularProgressIndicatorLoader.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Log.d("PIL", String.format("FavoriteActivity - addCityByName error: %s", error.getMessage()));
                    this.circularProgressIndicatorLoader.setVisibility(View.GONE);
                    FavoriteActivity.this.handler.post(() ->
                            Toast.makeText(FavoriteActivity.this, String.format("Ville %s non trouvée", cityName), Toast.LENGTH_LONG).show());

                }
        );
    }

    private void updateCityList() {
        Log.d("PIL", "updateCityList: "+this.cityList.size());

        for (int i = 0; i < this.cityList.size(); i++) {
            updateCity(i, this.cityList.get(i));
        }

        Log.d("PIL", "LEAVE updateCityList: "+this.cityList.size());
    }

    private void updateCity(int index, City city) {
        openWeatherMapAPIClient.weatherByCityId(city.getId(), response -> {

            ObjectMapper mapper = new ObjectMapper();
            try {
                WeatherDTO weatherDTO = mapper.readValue(response.toString(), WeatherDTO.class);
                City currentCity = weatherDTO != null ? weatherDTO.toCity() : null;

                if(currentCity == null) return;

                if(city.equals(currentCity)) return;

                currentCity.setIdDb(city.getIdDb());
                currentCity.setFavPosition(index);

                FavoriteActivity.this.cityList.set(index, currentCity);
                FavoriteActivity.this.favoriteAdapter.notifyItemChanged(index);
                FavoriteActivity.this.dataBaseHelper.update(currentCity);

            } catch (JsonProcessingException e) {
                Log.d("PIL", e.getMessage());
            }
        });
    }

}