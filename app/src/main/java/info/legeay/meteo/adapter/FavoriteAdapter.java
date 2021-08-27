package info.legeay.meteo.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.util.List;

import info.legeay.meteo.R;
import info.legeay.meteo.activity.FavoriteActivity;
import info.legeay.meteo.activity.MainActivity;
import info.legeay.meteo.activity.MapsActivity;
import info.legeay.meteo.dto.GoogleMapActivityDTO;
import info.legeay.meteo.dto.WeatherDTO;
import info.legeay.meteo.model.City;
import info.legeay.meteo.util.Network;

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
        city.setFavPosition(holder.getLayoutPosition());
        holder.city = city;

        holder.textViewFavoriteCityName.setText(city.getName());
        holder.textViewFavoriteCityWeather.setText(city.getWeatherDescription());
        holder.imageViewFavoriteCityWeather.setImageResource(city.getWeatherIconDrawableId());
        holder.textViewFavoriteCityTemperature.setText(city.getCurrentTemperature());
    }

    @Override
    public int getItemCount() {return this.cityList == null ? 0 : this.cityList.size();}

    // Classe holder qui contient la vue d’un item
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public City city;

        public TextView textViewFavoriteCityName;
        public TextView textViewFavoriteCityWeather;
        public ImageView imageViewFavoriteCityWeather;
        public TextView textViewFavoriteCityTemperature;

        public ViewHolder(View view) {
            super(view);

            view.setTag(this);

            textViewFavoriteCityName = (TextView) view.findViewById(R.id.textview_favorite_city_name);
            textViewFavoriteCityWeather = (TextView) view.findViewById(R.id.textview_favorite_city_weather);
            imageViewFavoriteCityWeather = (ImageView) view.findViewById(R.id.imageview_favorite_city_weather);
            textViewFavoriteCityTemperature = (TextView) view.findViewById(R.id.textview_favorite_city_temperature);

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if(!Network.isInternetAvailable(FavoriteAdapter.this.context)) return;

            ObjectMapper mapper = new ObjectMapper();
            Intent intent = new Intent(FavoriteAdapter.this.context, MapsActivity.class);

            try {
                GoogleMapActivityDTO googleMapActivityDTO = new GoogleMapActivityDTO(this.city.getName(),this.city.getLat(), this.city.getLon(), this.city.isDayTime());
                String coord = mapper.writeValueAsString(googleMapActivityDTO);
                Log.d("PILMETEOAPP", "adapter holder GoogleMapActivityDTO tiString: "+googleMapActivityDTO);
                Log.d("PILMETEOAPP", "adapter holder mapper.writeValueAsString: "+coord);
                intent.putExtra(String.format("%scoord", FavoriteActivity.KEY_PREFIX), coord);

            } catch (JsonProcessingException e) {
                Log.d("PILMETEOAPP", "adapter holder mapper.writeValueAsString: "+e.getMessage());
            }

            FavoriteAdapter.this.context.startActivity(intent);
        }


//        @Override
//        public boolean onLongClick(View v) {
//
//            final AlertDialog alertDialog = new AlertDialog.Builder(FavoriteAdapter.this.context)
//                    .setTitle("Supprimer cette ville?")
//                    .setPositiveButton("SUPPRIMER", (dialog, which) -> {
//                        FavoriteAdapter.this.cityList.remove(this.city);
//                        FavoriteAdapter.this.notifyDataSetChanged();
//                    })
//                    .setNegativeButton("Annuler", (dialog, which) -> dialog.cancel())
//                    .create();
//
//            alertDialog.show();
//
//            return true;
//        }
    }
}