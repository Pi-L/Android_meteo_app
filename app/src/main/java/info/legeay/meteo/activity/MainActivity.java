package info.legeay.meteo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import info.legeay.meteo.R;
import info.legeay.meteo.databinding.ActivityMainBinding;
import info.legeay.meteo.util.Network;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    public static final String KEY_PREFIX = "main_";

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private TextView textViewCityName;
    private TextView textViewInternetKO;
    private LinearLayout linearlayoutMeteoDisplay;
    private EditText editTextMessage;

    private FloatingActionButton buttonFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        this.textViewCityName = findViewById(R.id.textview_city_name);
        this.textViewInternetKO = findViewById(R.id.textview_internet_ko);
        this.linearlayoutMeteoDisplay = findViewById(R.id.linearlayout_meteo_display);
        this.buttonFavorite = findViewById(R.id.button_favorite);
        this.editTextMessage = findViewById(R.id.edittext_message);

        this.setEvents();

        this.textViewCityName.setText(R.string.city_name);

        setPageVisibility();

        Toast.makeText(this,
                String.format("%s internet?: %s", this.textViewCityName.getText(), Network.isInternetAvailable(this) ? "oui" : "non"),
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setPageVisibility();
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
            intent.putExtra(String.format("%smessage", KEY_PREFIX), this.editTextMessage.getText().toString());
            startActivity(intent);
        });

    }
}