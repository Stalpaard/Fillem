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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView textView_movie;
    private TextView textView_rating;
    private Button button_movie;
    private RequestQueue requestQueue;
    private JSONObject movie;
    private String current_movie_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize variables
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        ratingBar = findViewById(R.id.ratingBar);
        textView_movie = findViewById(R.id.textView_movie);
        textView_rating = findViewById(R.id.textView_rating);
        button_movie = findViewById(R.id.button_movie);
        //init routine
        textView_rating.setText("Rating");
        textView_movie.setText("Movie ID");

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
                Float r = rating*2;
                textView_rating.setText(r.toString());
            }
        });
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
