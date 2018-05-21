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
    public static final String EXTRA_JSONSTRING = "be.kuleuven.softdev.eliasstalpaert.fillemapp.JSONSTRING";

    private RatingBar ratingBar;
    private ActionBar actionBar;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Menu mMenu;

    private TextView textView_movie;
    private TextView textView_rating;
    private TextView textView_beginyear;
    private TextView textView_endyear;
    private TextView textView_minVotes;

    private Map<String,MenuItem> genres;

    private Button button_movie;
    private RequestQueue requestQueue;

    private Toast fetchMovie;

    private JSONObject movie;

    private String current_movie_id;
    private String jsonString;

    private RangeBar rangeBarYear;
    private SeekBar seekbarVotes;

    private Integer beginyear;
    private Integer endyear;
    private Integer minVotes;
    private Float rating_float;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize variables
        rating_float = 0f;
        genres = new TreeMap<>();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mMenu = mNavigationView.getMenu();

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
        init();

//        textView_beginyear.setText(rangeBarYear.getLeftIndex() + 1894);
//        textView_endyear.setText(rangeBarYear.getRightIndex() + 1894);
        //Set on click listeners
        button_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateMovie();
                fetchMovie = Toast.makeText(MainActivity.this, "Fetching movie...", Toast.LENGTH_LONG);
                fetchMovie.show();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_float = rating * 2;
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
                if(endyear > 2018){
                    endyear = 2018;
                }
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
                        // set item as selected to persist highlight
                        boolean checked = menuItem.isChecked();
                        menuItem.setChecked(!checked);
                        // close drawer when item is tapped
                        //mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

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
        endyear = 2018;
        minVotes = calcVotesFromProgress(50);

        textView_beginyear.setText(beginyear.toString());
        textView_endyear.setText(endyear.toString());
        textView_rating.setText("Rating");
        textView_minVotes.setText(minVotes.toString());

        genres.put("action",getMenuItem(R.id.actionGenre));
        genres.put("adventure",getMenuItem(R.id.adventureGenre));
        genres.put("animation",getMenuItem(R.id.animationGenre));
        genres.put("biography",getMenuItem(R.id.biographyGenre));
        genres.put("comedy",getMenuItem(R.id.comedyGenre));
        genres.put("crime",getMenuItem(R.id.crimeGenre));
        genres.put("documentary",getMenuItem(R.id.documentaryGenre));
        genres.put("drama",getMenuItem(R.id.dramaGenre));
        genres.put("family",getMenuItem(R.id.familyGenre));
        genres.put("fantasy",getMenuItem(R.id.fantasyGenre));
        genres.put("game-show",getMenuItem(R.id.gameshowGenre));
        genres.put("history",getMenuItem(R.id.historyGenre));
        genres.put("horror",getMenuItem(R.id.horrorGenre));
        genres.put("music",getMenuItem(R.id.musicGenre));
        genres.put("musical",getMenuItem(R.id.musicalGenre));
        genres.put("mystery",getMenuItem(R.id.mysteryGenre));
        genres.put("news",getMenuItem(R.id.newsGenre));
        genres.put("reality-tv",getMenuItem(R.id.realityGenre));
        genres.put("romance",getMenuItem(R.id.romanceGenre));
        genres.put("sci-fi",getMenuItem(R.id.scifiGenre));
        genres.put("sport",getMenuItem(R.id.sportGenre));
        genres.put("talk-show",getMenuItem(R.id.talkshowGenre));
        genres.put("thriller",getMenuItem(R.id.thrillerGenre));
        genres.put("war",getMenuItem(R.id.warGenre));
        genres.put("western",getMenuItem(R.id.westernGenre));
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

    public MenuItem getMenuItem(int id){
        return mMenu.findItem(id);
    }

    private void startDisplayActivity() {
        Intent intent = new Intent(this, DisplayMovieActivity.class);
        intent.putExtra(EXTRA_JSONSTRING, jsonString);
        startActivity(intent);
    }

    public void generateMovie() {
        //mNavigationView.getMenu().getItem(R.id.actionGenre).isChecked();
        String queryUrl = buildUrl();

        JsonArrayRequest request = new JsonArrayRequest(queryUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                           movie = jsonArray.getJSONObject(0);
                           current_movie_id = movie.getString("imdbId");
                           checkResponse();
                           //startDisplayActivity();
                        }
                        catch(JSONException e) {
                            fetchMovie.cancel();
                            Toast.makeText(MainActivity.this, "No movies found", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        fetchMovie.cancel();
                        Toast.makeText(MainActivity.this, "Unable to fetch data: please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(request);
    }

    public void checkResponse() {
        String queryUrl = "http://www.omdbapi.com/?i=" + current_movie_id + "&apikey=e2383f7f";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, queryUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseObject) {
                        try {
                            String response = responseObject.getString("Response");
                            if(response.equals("True")){
                                jsonString = responseObject.toString(); // save JSONObject to String
                                fetchMovie.cancel(); // cancel Toast "Fetching movie..."
                                startDisplayActivity(); // goto displayactivity
                            }
                            else{
                                generateMovie();
                                //Toast.makeText(MainActivity.this, "No details found in API, try again", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            fetchMovie.cancel();
                            Toast.makeText(MainActivity.this, "Error finding details", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        generateMovie();
                        //Toast.makeText(MainActivity.this, "noman", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private String buildUrl(){
        String url = "http://api.a17-sd206.studev.groept.be/query_movies_new";
        StringBuilder urlBuilder = new StringBuilder(url);
        int emptyCount = 0;
        for(String s : genres.keySet()){
            MenuItem m = genres.get(s);
            if(m.isChecked()){
                urlBuilder.append("/" + s);
            }
            else{
                urlBuilder.append("/.*");
                emptyCount++;
            }
        }
        int ratingInt = rating_float.intValue();
        urlBuilder.append("/" + 0);
        urlBuilder.append("/" + minVotes);
        urlBuilder.append("/" + ratingInt);
        urlBuilder.append("/" + beginyear);
        urlBuilder.append("/" + endyear);
        String queryUrl;
        if(emptyCount < 25){
            queryUrl = urlBuilder.toString();
        }
        else{
            StringBuilder urlEmptyBuilder = new StringBuilder(url);
            for(String s : genres.keySet()){
                urlEmptyBuilder.append("/" + s);
            }
            urlEmptyBuilder.append("/" + 0);
            urlEmptyBuilder.append("/" + minVotes);
            urlEmptyBuilder.append("/" + ratingInt);
            urlEmptyBuilder.append("/" + beginyear);
            urlEmptyBuilder.append("/" + endyear);
            queryUrl = urlEmptyBuilder.toString();
        }
        return queryUrl;
    }
}
