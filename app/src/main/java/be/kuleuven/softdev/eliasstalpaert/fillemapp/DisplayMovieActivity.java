package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DisplayMovieActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private Context context = this;

    private ConstraintLayout constraintLayout;
    private LinearLayout detailsLayout;

    private String movie_title, movie_year, movie_runtime, movie_genre, movie_director, movie_actors, movie_plot, imdbId, posterUrl, imdbRating, imdhVotes;
    private Boolean no_generate;
    private Integer beginyear, endyear, minVotes;
    private Float rating_float;

    private Button generateAgain, trailerPlayButton, exitDisplayButton, cancelDisplayButton, addToWatchlistButton;
    private TextView title_textview, releaseyear_textview, plot_textview, genre_textview, director_textview, actor_textview, runtime_textview, rating_textview, votes_textview;
    private ImageView imageView_internet;

    private MovieGeneratorDisplay movieGenerator;
    private TrailerGenerator trailerGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_movie);
        //initialize variables
        this.findViews();
        this.initIntentExtras();
        this.setOnClickListeners();

        movieGenerator = new MovieGeneratorDisplay(context, MainActivity.mMenu, this);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        initMovieVariables();
        updateMovieDetails();
        changeWatchlistButton();

        postInit();
    }

    private void saveWatchlist(){
        MainActivity.saveWatchList(this);
    }

    private void initIntentExtras(){
        beginyear = getIntent().getIntExtra(MovieGenerator.EXTRA_BEGINYEAR, 0);
        endyear = getIntent().getIntExtra(MovieGenerator.EXTRA_ENDYEAR, 3000);
        rating_float = getIntent().getFloatExtra(MovieGenerator.EXTRA_RATING, 0);
        minVotes = getIntent().getIntExtra(MovieGenerator.EXTRA_MINVOTES, 0);
        no_generate = getIntent().getBooleanExtra(HistoryMovieAdapter.EXTRA_NOGENERATE, false);
    }

    private void setOnClickListeners(){
        addToWatchlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryMovie h = new HistoryMovie(getIntent().getStringExtra(MovieGenerator.EXTRA_JSONSTRING));
                MainActivity.watchList.add(h);
                saveWatchlist();
                changeWatchlistButton();
                Toast.makeText(context, "Added to watchlist", Toast.LENGTH_SHORT).show();
            }
        });

        generateAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateAgain.setEnabled(false);
                movieGenerator.setRating_float(rating_float);
                movieGenerator.setMinVotes(minVotes);
                movieGenerator.setEndyear(endyear);
                movieGenerator.setBeginyear(beginyear);
                movieGenerator.generate();
                cancelDisplayButton.setVisibility(View.VISIBLE);
            }
        });

        cancelDisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDisplayButton.setEnabled(false);
                movieGenerator.setEnabled(false);
            }
        });

        trailerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeId = trailerGenerator.getYoutubeId();
                trailerGenerator.watchYoutubeVideo(context, youtubeId);
            }
        });

        exitDisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                movieGenerator.setEnabled(false);
                finishActivity();
            }
        });
    }

    private void findViews(){
        title_textview = findViewById(R.id.textView_title);
        addToWatchlistButton = findViewById(R.id.addToWatchlistButton);
        releaseyear_textview = findViewById(R.id.textView_displayReleaseYear);
        plot_textview = findViewById(R.id.textViewPlot);
        rating_textview = findViewById(R.id.ratingDisplay_textview);
        genre_textview = findViewById(R.id.textViewGenre);
        cancelDisplayButton = findViewById(R.id.cancelDisplayButton);
        votes_textview = findViewById(R.id.votesDisplay_textView);
        director_textview = findViewById(R.id.textViewDirector);
        actor_textview = findViewById(R.id.textViewActor);
        runtime_textview = findViewById(R.id.runTime_textView);
        constraintLayout = findViewById(R.id.constraint);
        trailerPlayButton = findViewById(R.id.trailerPlayButton);
        detailsLayout = findViewById(R.id.detailsLayout);
        generateAgain = findViewById(R.id.generateAgain);
        imageView_internet = findViewById(R.id.imageView_internet);
        exitDisplayButton = findViewById(R.id.exitDisplayButton);
    }

    private void loadImageByUrl(String posterUrl) {
        Picasso.get()
                .load(posterUrl)
                .fit()
                .centerInside()
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.error)
                .into(imageView_internet, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess(){
                        BitmapDrawable d = (BitmapDrawable) imageView_internet.getDrawable();
                        Bitmap b = d.getBitmap();
                        changeColours(b);
                    }

                    @Override
                    public void onError(Exception e) {
                        //Toast.makeText(DisplayMovieActivity.this, "Poster not found", Toast.LENGTH_SHORT).show();
                    }
                });
        }

    private void initMovieVariables(){
         try{
             JSONObject movieJson = new JSONObject(getIntent().getStringExtra(MovieGenerator.EXTRA_JSONSTRING));
             movie_title = movieJson.getString("Title");
             movie_year = movieJson.getString("Year");
             movie_runtime = movieJson.getString("Runtime");
             movie_genre = movieJson.getString("Genre");
             movie_plot = movieJson.getString("Plot");
             movie_director = movieJson.getString("Director");
             movie_actors = movieJson.getString("Actors");
             imdbId = movieJson.getString("imdbID");
             imdbRating = movieJson.getString("imdbRating");
             imdhVotes = movieJson.getString("imdbVotes");
             posterUrl = movieJson.getString("Poster");
         }
         catch (JSONException e){
             Toast.makeText(this, "Exception occurred", Toast.LENGTH_SHORT).show();
         }
    }

    private void updateMovieDetails(){
        title_textview.setText(movie_title);
        actor_textview.setText(movie_actors);
        director_textview.setText(movie_director);
        runtime_textview.setText("Runtime: " + movie_runtime);
        releaseyear_textview.setText(movie_year);
        plot_textview.setText(movie_plot);
        genre_textview.setText(movie_genre);
        rating_textview.setText(imdbRating + "/10");
        votes_textview.setText("("+imdhVotes+")");
    }

    private void postInit() {
        trailerGenerator = new TrailerGenerator(this.imdbId, context, this);
        trailerGenerator.generateTrailer();
        loadImageByUrl(posterUrl);
        if(no_generate){
            generateAgain.setVisibility(View.GONE);
        }
    }

    private void changeColours(Bitmap b){
        Palette.from(b).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette palette) {
                int detailsColor = palette.getDominantColor(6723232);
                int backgroundConstraint = palette.getLightVibrantColor(6723232);
                constraintLayout.setBackgroundColor(backgroundConstraint);
                detailsLayout.setBackgroundColor(detailsColor);
            }
        });
    }

    private void changeWatchlistButton(){
        if(isInWatchlist()){
            addToWatchlistButton.setEnabled(false);
            addToWatchlistButton.setText("In watchlist");
        }
    }

    public boolean isInWatchlist(){
        MainActivity.loadWatchList(this);
        return MainActivity.watchString.contains(this.imdbId);
    }

    public void reEnableInput(){
        cancelDisplayButton.setVisibility(View.GONE);
        cancelDisplayButton.setEnabled(true);
        generateAgain.setEnabled(true);
    }

    public void finishActivity(){
        cancelDisplayButton.setVisibility(View.GONE);
        cancelDisplayButton.setEnabled(true);
        movieGenerator.setEnabled(false);
        movieGenerator = null;
        this.finish();
    }
}
