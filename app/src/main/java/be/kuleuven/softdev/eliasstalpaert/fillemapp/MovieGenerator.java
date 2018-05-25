package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.TreeMap;

public class MovieGenerator {

    public static final String EXTRA_JSONSTRING = "be.kuleuven.softdev.eliasstalpaert.fillemapp.JSONSTRING";

    private Context context;
    private JSONObject movie;
    private String current_movie_id;
    private String jsonString;
    private Toast fetchMovie;
    private RequestQueue requestQueue;
    private Map<String,MenuItem> genres;
    private Menu menu;
    private Float rating_float;
    private Integer beginyear;
    private Integer endyear;
    private Integer minVotes;

    public String getJsonString() {
        return jsonString;
    }

    public void setRating_float(Float rating_float) {
        this.rating_float = rating_float;
    }

    public void setBeginyear(Integer beginyear) {
        this.beginyear = beginyear;
    }

    public void setEndyear(Integer endyear) {
        this.endyear = endyear;
    }

    public void setMinVotes(Integer minVotes) {
        this.minVotes = minVotes;
    }

    public MovieGenerator(Context context, Menu menu) {
        genres = new TreeMap<>();
        requestQueue = Volley.newRequestQueue(context);
        this.context = context;
        this.menu = menu;

        genres.put("action",menu.getItem(0));
        genres.put("adventure",menu.getItem(1));
        genres.put("animation",menu.getItem(2));
        genres.put("biography",menu.getItem(3));
        genres.put("comedy",menu.getItem(4));
        genres.put("crime",menu.getItem(5));
        genres.put("documentary",menu.getItem(6));
        genres.put("drama",menu.getItem(7));
        genres.put("family",menu.getItem(8));
        genres.put("fantasy",menu.getItem(9));
        genres.put("game-show",menu.getItem(10));
        genres.put("history",menu.getItem(11));
        genres.put("horror",menu.getItem(12));
        genres.put("music",menu.getItem(13));
        genres.put("musical",menu.getItem(14));
        genres.put("mystery",menu.getItem(15));
        genres.put("news",menu.getItem(16));
        genres.put("reality-tv",menu.getItem(17));
        genres.put("romance",menu.getItem(18));
        genres.put("sci-fi",menu.getItem(19));
        genres.put("sport",menu.getItem(20));
        genres.put("talk-show",menu.getItem(21));
        genres.put("thriller",menu.getItem(22));
        genres.put("war",menu.getItem(23));
        genres.put("western",menu.getItem(24));
    }

    private void startDisplayActivity() {
        Intent intent = new Intent(context, DisplayMovieActivity.class);
        intent.putExtra(EXTRA_JSONSTRING, jsonString); //jsonstring aangepast
        context.startActivity(intent);
    }

    public void generateMovie() {
        //mNavigationView.getMenu().getItem(R.id.actionGenre).isChecked();
        fetchMovie = Toast.makeText(context, "Fetching movie...", Toast.LENGTH_LONG);
        fetchMovie.show();

        String queryUrl = buildUrl();

        JsonArrayRequest request = new JsonArrayRequest(queryUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            movie = jsonArray.getJSONObject(0);
                            current_movie_id = movie.getString("imdbId");
                            checkResponse();
                        }
                        catch(JSONException e) {
                            fetchMovie.cancel();
                            Toast.makeText(context, "No movies found", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        fetchMovie.cancel();
                        Toast.makeText(context, "Unable to fetch data: please check your internet connection", Toast.LENGTH_SHORT).show();
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
                            }
                        } catch (JSONException e) {
                            fetchMovie.cancel();
                            Toast.makeText(context, "Error finding details", Toast.LENGTH_SHORT).show();
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
