package info.legeay.meteo.activity;

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

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private TextView textViewCityName;
    private TextView textViewInternetKO;
    private LinearLayout linearlayoutMeteoDisplay;

    private Button button1;
    private Button button2;
    private Button button3;

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

        this.button1 = findViewById(R.id.button_1);
        this.button2 = findViewById(R.id.button_2);
        this.button3 = findViewById(R.id.button_3);

        this.setButtonsEvents();

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

    private void setButtonsEvents() {
        this.button1.setOnClickListener(view -> {
            Toast.makeText(this,
                    "Clic sur bouton 1",
                    Toast.LENGTH_SHORT).show();
        });

        this.button2.setOnClickListener(view -> {
            Toast.makeText(this,
                    "Clic sur bouton 2",
                    Toast.LENGTH_SHORT).show();
        });

        this.button3.setOnClickListener(view -> {
            Toast.makeText(this,
                    "Clic sur bouton 3",
                    Toast.LENGTH_SHORT).show();
        });
    }
}