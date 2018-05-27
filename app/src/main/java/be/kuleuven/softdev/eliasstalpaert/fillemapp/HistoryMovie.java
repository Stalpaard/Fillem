package be.kuleuven.softdev.eliasstalpaert.fillemapp;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class HistoryMovie implements Parcelable {

    private String movieTitle, movieGenre, posterUrl, movieRuntime, movieDirector, movieActors, movieImdbId, imdbRating, imdbVotes;
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
            this.imdbRating = movieJson.getString("imdbRating");
            this.imdbVotes = movieJson.getString("imdbVotes");
        }
        catch(JSONException e){}
    }

    public String getJsonString() {
        return jsonString;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public String getImdbVotes() {
        return imdbVotes;
    }

    public String getMovieImdbId() {
        return movieImdbId;
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

    // Parcelling part
    public HistoryMovie(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.movieTitle = data[0];
        this.movieGenre = data[1];
        this.posterUrl = data[2];
        this.movieRuntime = data[3];
        this.movieDirector = data[4];
        this.movieActors = data[5];
        this.movieImdbId = data[6];
        this.imdbRating = data[7];
        this.imdbVotes = data[8];
        this.movieReleaseYear = data[9];
        this.jsonString = data[10];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.movieTitle,
                this.movieGenre, this.posterUrl, this.movieRuntime,
                this.movieDirector, this.movieActors, this.movieImdbId, this.imdbRating,
                this.imdbVotes, this.movieReleaseYear, this.jsonString});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public HistoryMovie createFromParcel(Parcel in) {
            return new HistoryMovie(in);
        }

        public HistoryMovie[] newArray(int size) {
            return new HistoryMovie[size];
        }
    };
}
