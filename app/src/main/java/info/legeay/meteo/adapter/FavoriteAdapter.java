package info.legeay.meteo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import info.legeay.meteo.R;
import info.legeay.meteo.model.City;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private final Context context;
    private final List<City> cityList;

    public FavoriteAdapter(Context context, List<City> cityList) {
        this.context = context;
        this.cityList = cityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.favorite_item_city, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        City city = cityList.get(position);
        holder.textViewFavoriteCityName.setText(city.getName());
        holder.textViewFavoriteCityWeather.setText(city.getDescription());
        holder.imageViewFavoriteCityWeather.setImageDrawable(context.getDrawable(city.getWeatherIcon()));
        holder.textViewFavoriteCityTemperature.setText(city.getTemperature());
    }

    @Override
    public int getItemCount() {return this.cityList == null ? 0 : this.cityList.size();}

    // Classe holder qui contient la vue d’un item
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewFavoriteCityName;
        public TextView textViewFavoriteCityWeather;
        public ImageView imageViewFavoriteCityWeather;
        public TextView textViewFavoriteCityTemperature;

        public ViewHolder(View view) {
            super(view);

            textViewFavoriteCityName = view.findViewById(R.id.textview_favorite_city_name);
            textViewFavoriteCityWeather = view.findViewById(R.id.textview_favorite_city_weather);
            imageViewFavoriteCityWeather = view.findViewById(R.id.imageview_favorite_city_weather);
            textViewFavoriteCityTemperature = view.findViewById(R.id.textview_favorite_city_temperature);
        }
    }
}