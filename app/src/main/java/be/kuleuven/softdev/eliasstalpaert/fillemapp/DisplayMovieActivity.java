package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class DisplayMovieActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private TextView title_textview, releaseyear_textview, plot_textview, genre_textview, director_textview, actor_textview, runtime_textview;
    private Context context = this;
    private ConstraintLayout constraintLayout;
    private LinearLayout detailsLayout;

    private String movie_title, movie_year, movie_runtime, movie_genre, movie_director, movie_actors, movie_plot, imdbId, posterUrl;;

    private Boolean no_generate;

    private Integer beginyear, endyear, minVotes;
    private Float rating_float;

    private Button generateAgain, trailerPlayButton;
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

        postInit();
    }

    private void initIntentExtras(){
        beginyear = getIntent().getIntExtra(MovieGenerator.EXTRA_BEGINYEAR, 0);
        endyear = getIntent().getIntExtra(MovieGenerator.EXTRA_ENDYEAR, 3000);
        rating_float = getIntent().getFloatExtra(MovieGenerator.EXTRA_RATING, 0);
        minVotes = getIntent().getIntExtra(MovieGenerator.EXTRA_MINVOTES, 0);
        no_generate = getIntent().getBooleanExtra(HistoryMovieAdapter.EXTRA_NOGENERATE, false);
    }

    private void setOnClickListeners(){
        generateAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateAgain.setEnabled(false);
                movieGenerator.setRating_float(rating_float);
                movieGenerator.setMinVotes(minVotes);
                movieGenerator.setEndyear(endyear);
                movieGenerator.setBeginyear(beginyear);
                movieGenerator.generate();
            }
        });

        trailerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeId = trailerGenerator.getYoutubeId();
                trailerGenerator.watchYoutubeVideo(context, youtubeId);
            }
        });
    }

    private void findViews(){
        title_textview = findViewById(R.id.textView_title);
        releaseyear_textview = findViewById(R.id.textView_displayReleaseYear);
        plot_textview = findViewById(R.id.textViewPlot);
        genre_textview = findViewById(R.id.textViewGenre);
        director_textview = findViewById(R.id.textViewDirector);
        actor_textview = findViewById(R.id.textViewActor);
        runtime_textview = findViewById(R.id.runTime_textView);
        constraintLayout = findViewById(R.id.constraint);
        trailerPlayButton = findViewById(R.id.trailerPlayButton);
        detailsLayout = findViewById(R.id.detailsLayout);
        generateAgain = findViewById(R.id.generateAgain);
        imageView_internet = findViewById(R.id.imageView_internet);
    }

    private void loadImageByUrl(String posterUrl) {
        Picasso.get()
                .load(posterUrl)
                .fit()
                .centerInside()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher_round)
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
                int textColor = palette.getVibrantColor(6723232);
                int backgroundConstraint = palette.getLightVibrantColor(6723232);
                //releaseyear_textview.setTextColor(textColor);
                //title_textview.setTextColor(textColor);
                constraintLayout.setBackgroundColor(backgroundConstraint);
                detailsLayout.setBackgroundColor(textColor);
            }
        });
    }

    public void finishActivity(){
        this.finish();
    }
}
