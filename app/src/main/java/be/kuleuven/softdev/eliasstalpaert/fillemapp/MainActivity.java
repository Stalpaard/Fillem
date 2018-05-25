package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.edmodo.rangebar.RangeBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private ActionBar actionBar;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    public static Menu mMenu;

    private TextView textView_movie;
    private TextView textView_rating;
    private TextView textView_beginyear;
    private TextView textView_endyear;
    private TextView textView_minVotes;

    private Button button_movie;

    private RangeBar rangeBarYear;
    private SeekBar seekbarVotes;
    private MovieGenerator movieGenerator;

    private Integer beginyear;
    private Integer endyear;
    private Integer minVotes;
    private Float rating_float;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize variables
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mMenu = mNavigationView.getMenu();
        movieGenerator = new MovieGenerator(this, mMenu);

        textView_movie = findViewById(R.id.textView_movie);
        textView_rating = findViewById(R.id.textView_rating);
        textView_beginyear = findViewById(R.id.textView_beginyear);
        textView_endyear = findViewById(R.id.textView_endyear);
        textView_minVotes = findViewById(R.id.textView_minVotes);

        button_movie = findViewById(R.id.button_movie);

        rangeBarYear = findViewById(R.id.rangebarYear);
        seekbarVotes = findViewById(R.id.seekbarVotes);
        ratingBar = findViewById(R.id.ratingBar);

        //init routine
        this.setTitle("Genres");
        init();

        //Set on click listeners
        button_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieGenerator.generate();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_float = rating * 2;
                movieGenerator.setRating_float(rating_float);
                textView_rating.setText(rating_float.toString().trim());
            }
        });
        rangeBarYear.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onIndexChangeListener(RangeBar rangeBar, int i, int i1) {
                beginyear = i+1894;
                endyear = i1+1894;
                if(beginyear < 1894){
                    beginyear = 1894;
                }
                if(beginyear > 2018){
                    beginyear = 2018;
                }
                if(endyear > 2018){
                    endyear = 2018;
                }
                if(endyear < 1894){
                    endyear = 1894;
                }
                movieGenerator.setBeginyear(beginyear);
                movieGenerator.setEndyear(endyear);
                String s1 = beginyear.toString();
                String s2 = endyear.toString();
                textView_beginyear.setText(s1);
                textView_endyear.setText(s2);
            }
        });

        seekbarVotes.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minVotes = calcVotesFromProgress(progress);
                movieGenerator.setMinVotes(minVotes);
                textView_minVotes.setText(minVotes.toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        boolean checked = menuItem.isChecked();
                        menuItem.setChecked(!checked);
                        return true;
                    }
                });
    }

    public void init() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        rangeBarYear.setTickCount(127);
        rangeBarYear.setThumbRadius(10);
        seekbarVotes.setProgress(50);

        beginyear = 1894;
        movieGenerator.setBeginyear(beginyear);
        endyear = 2018;
        movieGenerator.setEndyear(endyear);
        minVotes = calcVotesFromProgress(50);
        movieGenerator.setMinVotes(endyear);
        rating_float = 0f;
        movieGenerator.setRating_float(rating_float);

        textView_beginyear.setText(beginyear.toString());
        textView_endyear.setText(endyear.toString());
        textView_rating.setText("Rating");
        textView_minVotes.setText(minVotes.toString());
    }

    private int calcVotesFromProgress(int progress){
        return (((progress - 0)*1953205)/100) + 5;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
