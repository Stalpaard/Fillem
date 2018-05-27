package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrailerGenerator {
    private String imdbId, youtubeId;
    private Context context;
    private RequestQueue requestQueue;
    private Integer tmdbId;
    private CardView trailerCardview;
    private DisplayMovieActivity displayMovieActivity;
    private JSONArray trailerResults;

    public TrailerGenerator(String imdbId, Context context, DisplayMovieActivity displayMovieActivity) {
        this.imdbId = imdbId;
        this.context = context;
        this.displayMovieActivity = displayMovieActivity;

        trailerCardview = displayMovieActivity.findViewById(R.id.trailerCardview);

        requestQueue = Volley.newRequestQueue(context);
        trailerCardview.setVisibility(View.GONE);
    }

    public void generateTrailer(){
        findTmdbId(imdbId);
    }

    public void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    private void findTmdbId(String imdbId){
        String queryUrl = "https://api.themoviedb.org/3/find/" + imdbId + "?api_key=0db5dab83bfacc63c6de14c2d16f3925&external_source=imdb_id";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, queryUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseObject) {
                        try {
                            JSONArray movie_details = responseObject.getJSONArray("movie_results");
                            JSONObject movie_object = movie_details.getJSONObject(0);
                            if(movie_object != null){
                                if(!movie_object.isNull("id")){
                                    tmdbId = movie_object.getInt("id");
                                    findTrailers(tmdbId);
                                }
                            }

                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void findTrailers(Integer tmdbId){
        String queryUrl = "http://api.themoviedb.org/3/movie/" + tmdbId + "/videos?api_key=0db5dab83bfacc63c6de14c2d16f3925";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, queryUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject responseObject) {
                        try {
                            trailerResults = responseObject.getJSONArray("results");
                            checkJsonArrayForTrailers(trailerResults);

                        } catch (JSONException e) {
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void checkJsonArrayForTrailers(JSONArray jsonArray){
        if(jsonArray.length() > 0){
            JSONObject trailer = null;
            for(int i = 0; i < jsonArray.length(); i++){
                try{
                    trailer = jsonArray.getJSONObject(i);
                    if(trailer.getString("site").equals("YouTube")){
                        break;
                    }
                    else{
                        trailer = null;
                    }
                }
                catch (JSONException e){

                }
            }

            if(trailer != null){
                try{
                    if(!trailer.isNull("key")){
                        youtubeId = trailer.getString("key");
                        Toast.makeText(context, "Trailer found!", Toast.LENGTH_SHORT).show();
                        trailerCardview.setVisibility(View.VISIBLE);
                    }
                }
                catch(JSONException e){
                }
            }
        }
    }



}
