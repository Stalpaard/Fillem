package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import org.json.JSONException;
import org.json.JSONObject;

public class HistoryMovie {

    private String movieTitle, movieGenre, posterUrl, movieRuntime, movieDirector, movieActors, movieImdbId;
    private String movieReleaseYear;
    private String jsonString;

    public HistoryMovie(String jsonString) {
        try{
            JSONObject movieJson = new JSONObject(jsonString);
            this.jsonString = jsonString;
            this.movieTitle = movieJson.getString("Title");
            this.movieReleaseYear = movieJson.getString("Year");
            this.movieRuntime = movieJson.getString("Runtime");
            this.movieGenre = movieJson.getString("Genre");
            this.movieDirector = movieJson.getString("Director");
            this.movieActors = movieJson.getString("Actors");
            this.movieImdbId = movieJson.getString("imdbID");
            this.posterUrl = movieJson.getString("Poster");
        }
        catch(JSONException e){}
    }

    public String getJsonString() {
        return jsonString;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getMovieGenre() {
        return movieGenre;
    }

    public String getPosterUrl() {
        return this.posterUrl;
    }

    public String getMovieReleaseYear() {
        return movieReleaseYear;
    }
}
