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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import info.legeay.meteo.R;
import info.legeay.meteo.adapter.FavoriteAdapter;
import info.legeay.meteo.client.OpenWeatherMapAPIClient;
import info.legeay.meteo.dao.CityDAO;
import info.legeay.meteo.database.DataBaseHelper;
import info.legeay.meteo.database.MeteoDatabase;
import info.legeay.meteo.dto.WeatherDTO;
import info.legeay.meteo.model.City;
import info.legeay.meteo.util.Network;
import io.reactivex.rxjava3.core.Single;

public class FavoriteActivity extends AppCompatActivity {

    public static final String KEY_PREFIX = "fav_";

//    private DataBaseHelper dataBaseHelper;

    private MeteoDatabase meteoDatabase;
    private CityDAO cityDAO;

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

        Toolbar myToolbar = findViewById(R.id.toolbar_favorite);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();

        if(actionBar == null) Log.d("PILMETEOAPP", "action bar is null: ");
        else {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24);
        }

        this.handler = new Handler();
        openWeatherMapAPIClient = new OpenWeatherMapAPIClient(this);

        this.recyclerView = findViewById(R.id.favorite_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);
        this.favoriteAdapter = new FavoriteAdapter(this, cityList);
        this.recyclerView.setAdapter(this.favoriteAdapter);

        this.floatingActionButtonSearch = findViewById(R.id.floatingactionbutton_favorite_search);
        this.circularProgressIndicatorLoader = findViewById(R.id.circularprogressindicator_favorite_loader);


        this.meteoDatabase = MeteoDatabase.getDatabase(this);
        this.cityDAO = meteoDatabase.cityDAO();

        this.setEvents();
        this.attachItemTouchHelperToRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Network.isInternetAvailable(this)) floatingActionButtonSearch.setVisibility(View.VISIBLE);
        else floatingActionButtonSearch.setVisibility(View.GONE);

        // de la meme maniere qu'on fait une classe Client pour une API, il faudrait faire une classe Repository ici pour plus de proprete
        Single<List<City>> cityListSingle = this.cityDAO.getAllSortedByFavPositionAsc();

        cityListSingle.subscribeOn(MeteoDatabase.dbScheduler)
                .subscribe(
                        currentCityList -> {
                        this.cityList.clear();
                        if(currentCityList.isEmpty()) return;

                        this.cityList.addAll(currentCityList);
                        // not thread safe
                        updateCityList();
                    },
                        error -> Log.d("PILMETEOAPP", String.format("onResume: cityListSingle.subscribeOn error :%s", error.getMessage()))
                );
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.cityDAO.deleteAll().subscribeOn(MeteoDatabase.dbScheduler)
                .subscribe(() -> {
                            if(FavoriteActivity.this.cityList.isEmpty()) return;
                            FavoriteActivity.this.cityDAO.insertAll(this.cityList).subscribeOn(MeteoDatabase.dbScheduler)
                                            .subscribe();
                        }
                );
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

                        Log.d("PILMETEOAPP", "onMove: ");

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
                            FavoriteActivity.this.favoriteAdapter.notifyItemChanged(faViewHolder.getBindingAdapterPosition());
                            return;
                        }

                        City city = faViewHolder.city;
                        int position = faViewHolder.getBindingAdapterPosition();

                        FavoriteActivity.this.cityList.remove(position);

                        Log.d("PILMETEOAPP", "onSwiped : "+position+" - "+FavoriteActivity.this.cityList.size());

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
                            Log.d("PILMETEOAPP", "city == null");
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
                        city.setFavPosition(position);
                        FavoriteActivity.this.cityList.add(city);
                        FavoriteActivity.this.favoriteAdapter.notifyItemInserted(position);

//                        FavoriteActivity.this.cityDAO.insert(city).subscribeOn(MeteoDatabase.dbScheduler).subscribe();

                    } catch (JsonProcessingException e) {
                        Log.d("PILMETEOAPP", "##### addCityByName #### JsonProcessingException" + e.getMessage());
                    } finally {
                        this.circularProgressIndicatorLoader.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Log.d("PILMETEOAPP", String.format("FavoriteActivity - addCityByName error: %s", error.getMessage()));
                    this.circularProgressIndicatorLoader.setVisibility(View.GONE);
                    FavoriteActivity.this.handler.post(() ->
                            Toast.makeText(FavoriteActivity.this, String.format("Ville %s non trouvée", cityName), Toast.LENGTH_LONG).show());

                }
        );
    }

    // don't wait for hte previous request to be over ... maybe not ideal
    private void updateCityList() {
        for (int i = 0; i < this.cityList.size(); i++) {
            updateCity(i, this.cityList.get(i));
        }
    }

    private void updateCity(int index, City city) {
        // not thread safe ...
        openWeatherMapAPIClient.weatherByCityId(city.getId(), response -> {
            Log.d("PILMETEOAPP", "updateCity response for index : "+index+" - list size: "+FavoriteActivity.this.cityList.size()+" - bad thread managment: "+(index > this.cityList.size() - 1));

            // bad fix for async response conflict
            // todo: better
            if(index > this.cityList.size() - 1) return;

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

            } catch (JsonProcessingException e) {
                Log.d("PILMETEOAPP", e.getMessage());
            }
        });
    }

}