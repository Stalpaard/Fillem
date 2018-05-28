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
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class MovieGenerator {

    public static final String EXTRA_JSONSTRING = "be.kuleuven.softdev.eliasstalpaert.fillemapp.JSONSTRING";
    public static final String EXTRA_BEGINYEAR = "be.kuleuven.softdev.eliasstalpaert.fillemapp.BEGINYEAR";
    public static final String EXTRA_ENDYEAR = "be.kuleuven.softdev.eliasstalpaert.fillemapp.ENDYEAR";
    public static final String EXTRA_RATING = "be.kuleuven.softdev.eliasstalpaert.fillemapp.RATING";
    public static final String EXTRA_MINVOTES = "be.kuleuven.softdev.eliasstalpaert.fillemapp.MINVOTES";

    private Context context;
    private Set<String> localHistory;
    private JSONObject movie;
    private String current_movie_id, jsonString;
    private Toast fetchMovie, overallTriesToast;
    private Boolean enabled;
    private RequestQueue requestQueue;
    private Map<String,MenuItem> genres;
    private Menu menu;
    private Float rating_float;
    private Integer beginyear, endyear, minVotes, limitOfTries, generateTries, overallTries;
    private MainActivity mainActivity;


    public MovieGenerator(Context context, Menu menu, MainActivity mainActivity) {
        this.context = context;
        this.menu = menu;
        this.enabled = true;
        this.localHistory = new TreeSet<>();
        this.mainActivity = mainActivity;

        genres = new TreeMap<>();
        requestQueue = Volley.newRequestQueue(context);
        overallTriesToast = Toast.makeText(MainActivity.mContext, "Still searching...", Toast.LENGTH_SHORT);

        initGenres();
    }

    private void initGenres(){
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


    public void generate(){
        generateTries = 0;
        overallTries = 0;
        fetchMovie = Toast.makeText(context, "Fetching movie...", Toast.LENGTH_SHORT);
        fetchMovie.show();
        calculateLimit();
    }

    public void setRating_float(Float rating_float) {
        this.rating_float = rating_float*10;
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

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    private void reEnableInput(){
        mainActivity.setInputEnabled(true);
    }

    private void cancelToasts(){
        fetchMovie.cancel();
    }


    private void calculateLimit(){
        String queryUrl = buildUrl("http://api.a17-sd206.studev.groept.be/get_size_of_results");
        JsonArrayRequest request = new JsonArrayRequest(queryUrl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            JSONObject countArray = jsonArray.getJSONObject(0);
                            String countString = countArray.getString("count");
                            limitOfTries = Integer.parseInt(countString);
                            if(limitOfTries > 0){
                                generateMovie();
                            }
                            else{
                                mainActivity.hideCancel();
                                Toast.makeText(context, "No movies found", Toast.LENGTH_SHORT).show();
                                reEnableInput();
                                mainActivity.hideCancel();
                            }
                        }
                        catch(JSONException e) {
                            Toast.makeText(context, "Couldn't calculate limit of tries, try again or restart", Toast.LENGTH_SHORT).show();
                            reEnableInput();
                            mainActivity.hideCancel();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Unable to fetch data: please check your internet connection", Toast.LENGTH_SHORT).show();
                        reEnableInput();
                        mainActivity.hideCancel();
                    }
                });
        requestQueue.add(request);
    }

    private void generateMovie() {
        if(generateTries >= limitOfTries){
            Toast.makeText(context, "All possible results have been found, change filters (all results are in history)", Toast.LENGTH_SHORT).show();
            reEnableInput();
        }
        else{
            String queryUrl = buildUrl("http://api.a17-sd206.studev.groept.be/final_query");

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
                                cancelToasts();
                                Toast.makeText(context, "No movies found", Toast.LENGTH_SHORT).show();
                                reEnableInput();
                                mainActivity.hideCancel();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            cancelToasts();
                            Toast.makeText(context, "Unable to fetch data: please check your internet connection", Toast.LENGTH_SHORT).show();
                            reEnableInput();
                            mainActivity.hideCancel();
                        }
                    });
            requestQueue.add(request);
        }
    }

    private void checkResponse() {
        overallTries++;
        if(overallTries % 5 == 0 && overallTries < 20){
            overallTriesToast.show();
        }
        if(overallTries > 20 || !(isEnabled())){
            cancelToasts();
            if(overallTries > 20){
                Toast.makeText(MainActivity.mContext, "Server Time-Out", Toast.LENGTH_SHORT).show();
            }
            setEnabled(true);
            overallTries = 0;
            mainActivity.hideCancel();
            reEnableInput();
        }
        else {
            String queryUrl = "http://www.omdbapi.com/?i=" + current_movie_id + "&apikey=e2383f7f";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, queryUrl, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject responseObject) {
                            try {
                                String response = responseObject.getString("Response");
                                if (response.equals("True") && historyContainsId(current_movie_id) && !(localHistoryContainsId(current_movie_id))) {
                                    localHistory.add(current_movie_id);
                                    generateTries++;
                                }
                                if (response.equals("True") && !(historyContainsId(current_movie_id))) {
                                    MainActivity.history.add(current_movie_id);
                                    jsonString = responseObject.toString();
                                    MainActivity.historyMoviesList.add(new HistoryMovie(jsonString));
                                    cancelToasts();
                                    mainActivity.hideCancel();
                                    startDisplayActivity();
                                } else {
                                    if (isEnabled()) {
                                        generateMovie();
                                    }
                                }
                            } catch (JSONException e) {
                                cancelToasts();
                                Toast.makeText(context, "Error finding details", Toast.LENGTH_SHORT).show();
                                reEnableInput();
                                mainActivity.hideCancel();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            generateMovie();
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        }
    }

    private String buildUrl(String url){
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
        urlBuilder.append("/" + minVotes);
        urlBuilder.append("/" + 1953210);
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
            urlEmptyBuilder.append("/" + minVotes);
            urlEmptyBuilder.append("/" + 1953210);
            urlEmptyBuilder.append("/" + ratingInt);
            urlEmptyBuilder.append("/" + beginyear);
            urlEmptyBuilder.append("/" + endyear);
            queryUrl = urlEmptyBuilder.toString();
        }
        return queryUrl;
    }

    private void startDisplayActivity() {
        Intent intent = new Intent(context, DisplayMovieActivity.class);

        intent.putExtra(EXTRA_JSONSTRING, jsonString);
        intent.putExtra(EXTRA_BEGINYEAR, beginyear);
        intent.putExtra(EXTRA_ENDYEAR, endyear);
        intent.putExtra(EXTRA_RATING, rating_float);
        intent.putExtra(EXTRA_MINVOTES, minVotes);
        intent.putExtra(HistoryMovieAdapter.EXTRA_NOGENERATE, false);

        context.startActivity(intent);
    }


    private MenuItem getMenuItem(int id){
        return menu.findItem(id);
    }

    private boolean isEnabled(){
        return this.enabled;
    }

    private boolean historyContainsId(String imdbId){
        if(MainActivity.history.contains(imdbId)){
            return true;
        }
        else{
            return false;
        }
    }

    private boolean localHistoryContainsId(String imdbId){
        if(localHistory.contains(imdbId)) return true;
        else return false;
    }


}
