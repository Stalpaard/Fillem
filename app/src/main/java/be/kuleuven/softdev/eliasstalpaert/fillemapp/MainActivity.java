package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.edmodo.rangebar.RangeBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    public static final String WATCHLIST = "be.kuleuven.softdev.eliasstalpaert.fillemapp.WATCHLIST";
    public static final String HISTORYENABLE = "be.kuleuven.softdev.eliasstalpaert.fillemapp.HISTORYENABLE";

    public static List<String> history;
    public static List<HistoryMovie> historyMoviesList;
    public static ArrayList<HistoryMovie> watchList;
    public static List<String> watchString;
    public static Context mContext;
    public static Menu mMenu;

    private RatingBar ratingBar;
    private DrawerLayout mDrawerLayout;
    private ConstraintLayout mConstraintLayout;
    private NavigationView mNavigationView;
    private CardView helpScreen;
    private TextView textView_rating, textView_beginyear, textView_endyear, textView_minVotes;
    private Button button_movie, historyButton, helpMainButton, helpMainCloseButton, closeGenresButton, cancelMainButton, watchListButton;
    private RangeBar rangeBarYear;
    private SeekBar seekbarVotes;
    private MovieGenerator movieGenerator;
    private Integer beginyear, endyear, minVotes;
    private Float rating_float;

    @Override
    protected void onResume(){
        super.onResume();
        setInputEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        movieGenerator.setEnabled(false);
        movieGenerator = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadWatchList(this);
        this.findViews();
        this.setOnClickListeners();

        populateWatchString();
        history = new ArrayList<>();
        historyMoviesList = new LinkedList<>();
        mMenu = mNavigationView.getMenu();
        mContext = this;
        movieGenerator = new MovieGenerator(mContext, mMenu, this);
        this.setTitle("Genres");

        this.initActionBar();
        this.initHeaderClick();
        this.initFilters();
        this.initMovieGen();

    }

    public static void populateWatchString(){
        watchString = new ArrayList<>();
        for(HistoryMovie h : MainActivity.watchList){
            watchString.add(h.getMovieImdbId());
        }
    }

    public static SharedPreferences getSharedPreferences (Context ctxt) {
        return ctxt.getSharedPreferences("shared preferences", MODE_PRIVATE);
    }

    public static void loadWatchList(Context ctx){
        SharedPreferences sharedPreferences = MainActivity.getSharedPreferences(ctx);
        Gson gson = new Gson();
        String watchListJson = sharedPreferences.getString("watchlist", null);
        Type type = new TypeToken<ArrayList<HistoryMovie>>() {}.getType();
        MainActivity.watchList = gson.fromJson(watchListJson,type);
        if(MainActivity.watchList == null){
            MainActivity.watchList = new ArrayList<>();
        }
        populateWatchString();
    }

    public static void saveWatchList(Context ctx){
        SharedPreferences sharedPreferences = MainActivity.getSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonWatchlist = gson.toJson(MainActivity.watchList);
        editor.putString("watchlist", jsonWatchlist);
        editor.apply();
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

    private void setOnClickListeners(){
        watchListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWatchListActivity();
            }
        });

        button_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setInputEnabled(false);
                movieGenerator.generate();
                cancelMainButton.setVisibility(View.VISIBLE);
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHistoryActivity();
            }
        });

        helpMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                helpScreen.setVisibility(View.VISIBLE);
            }
        });

        cancelMainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelMainButton.setEnabled(false);
                movieGenerator.setEnabled(false);
            }
        });

        helpMainCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpScreen.setVisibility(View.GONE);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_float = rating * 2;
                movieGenerator.setRating_float(rating_float);
                textView_rating.setText("Minimum Rating: " + rating_float.toString().trim());
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

    private void findViews(){
        watchListButton = findViewById(R.id.watchListButton);
        historyButton = findViewById(R.id.historyButton);
        mConstraintLayout = findViewById(R.id.constraintLayoutMain);
        closeGenresButton = findViewById(R.id.closeGenres);
        cancelMainButton = findViewById(R.id.cancelMainButton);
        helpScreen = findViewById(R.id.helpMainCardView);
        helpMainButton = findViewById(R.id.helpMainButton);
        helpMainCloseButton = findViewById(R.id.helpCloseMainButton);
        button_movie = findViewById(R.id.button_movie);
        rangeBarYear = findViewById(R.id.rangebarYear);
        seekbarVotes = findViewById(R.id.seekbarVotes);
        ratingBar = findViewById(R.id.ratingBar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        textView_rating = findViewById(R.id.textView_rating);
        textView_beginyear = findViewById(R.id.textView_beginyear);
        textView_endyear = findViewById(R.id.textView_endyear);
        textView_minVotes = findViewById(R.id.textView_minVotes);
    }

    private void initHeaderClick(){
        View header_view = mNavigationView.getHeaderView(0);
        header_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void initActionBar() throws NullPointerException{
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        try{
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background_no_black));
            actionBar.setTitle(Html.fromHtml("<font color='#444536'>Genres</font>"));
        }
        catch (NullPointerException n){
            Toast.makeText(this, "Nullpointer Exception ActionBar", Toast.LENGTH_SHORT).show();
        }
    }

    private void initMovieGen(){
        movieGenerator.setBeginyear(beginyear);
        movieGenerator.setEndyear(endyear);
        movieGenerator.setMinVotes(minVotes);
        movieGenerator.setRating_float(rating_float);
    }

    private void initFilters(){
        beginyear = 1894;
        endyear = 2018;
        minVotes = calcVotesFromProgress(50);
        rating_float = 2.5f;
        ratingBar.setRating(2.5f);
        textView_beginyear.setText(beginyear.toString());
        textView_endyear.setText(endyear.toString());
        textView_rating.setText("Minimum Rating: " + rating_float);
        textView_minVotes.setText(minVotes.toString());
        rangeBarYear.setTickCount(127);
        rangeBarYear.setThumbRadius(10);
        seekbarVotes.setProgress(50);
    }

    private void finishActivity(){
        finish();
    }

    public void hideCancel(){
        cancelMainButton.setEnabled(true);
        cancelMainButton.setVisibility(View.GONE);
    }

    public void setInputEnabled(boolean state){
        button_movie.setEnabled(state);
        ratingBar.setEnabled(state);
        rangeBarYear.setEnabled(state);
        seekbarVotes.setEnabled(state);
    }

    private int calcVotesFromProgress(int progress){
        return (((progress)*1000000)/100);
    }

    private void startHistoryActivity(){
        Intent intent = new Intent(this, HistoryScreenRecycler.class);
        intent.putExtra(HISTORYENABLE, true);
        this.startActivity(intent);
    }

    private void startWatchListActivity(){
        Intent intent = new Intent(this, HistoryScreenRecycler.class);
        intent.putExtra(HISTORYENABLE, false);
        this.startActivity(intent);
    }

}
