package info.legeay.meteo.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import info.legeay.meteo.R;

public class FavoriteActivity extends AppCompatActivity {

    public static final String KEY_PREFIX = "fav_";

    private TextView textViewMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite);

        this.textViewMessage = findViewById(R.id.textview_favorite_message);

        Bundle extras = getIntent().getExtras();
        String message = extras.getString(String.format("%smessage", MainActivity.KEY_PREFIX));

//        Log.d("PIL", "fav message: "+message);

        if(message == null || message.isEmpty()) this.textViewMessage.setVisibility(View.GONE);
        else this.textViewMessage.setText(String.format("Message : %s", message));

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_favorite);
//        myToolbar.setTitleTextColor(Color.WHITE);
//        setSupportActionBar(myToolbar);


    }
}