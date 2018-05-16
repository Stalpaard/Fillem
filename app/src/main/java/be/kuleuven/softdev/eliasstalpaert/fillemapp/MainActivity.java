package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.edmodo.rangebar.RangeBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RatingBar ratingBar;

    private TextView textView_movie;
    private TextView textView_rating;
    private TextView textView_beginyear;
    private TextView textView_endyear;

    private Button button_movie;
    private ArrayList<Integer> genres_true;
    private HashMap<Integer,String> genres;
    private RequestQueue requestQueue;

    private JSONObject movie;

    private String current_movie_id;

    private RangeBar rangeBar;

    private Integer beginyear;
    private Integer endyear;
    private Float rating_float;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize variables
        genres_true = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        ratingBar = findViewById(R.id.ratingBar);
        textView_movie = findViewById(R.id.textView_movie);
        textView_rating = findViewById(R.id.textView_rating);
        textView_beginyear = findViewById(R.id.textView_beginyear);
        textView_endyear = findViewById(R.id.textView_endyear);
        button_movie = findViewById(R.id.button_movie);
        rangeBar = findViewById(R.id.rangebar);
        //init routine
        init();

//        textView_beginyear.setText(rangeBar.getLeftIndex() + 1894);
//        textView_endyear.setText(rangeBar.getRightIndex() + 1894);
        //Set on click listeners
        button_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateMovie();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rating_float = rating * 2;
                textView_rating.setText(rating_float.toString());
            }
        });
        rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
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
    }

    public void init() {
        rangeBar.setTickCount(127);
        rangeBar.setThumbRadius(10);
        textView_rating.setText("Rating");
        textView_movie.setText("Movie ID");
    }

    public void generateMovie() {
        JsonArrayRequest request = new JsonArrayRequest("http://api.a17-sd206.studev.groept.be/example_movies/action",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                           movie = jsonArray.getJSONObject(0);
                           current_movie_id = movie.getString("tconst");
                        }
                        catch(JSONException e) {
                            textView_movie.setText("Error: " + e.getLocalizedMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Unable to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(request);
        textView_movie.setText("Movie ID: " + current_movie_id);
    }
}
