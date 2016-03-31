package com.serigon.moviezy.task;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.serigon.movietrend.BuildConfig;
import com.serigon.moviezy.activity.DetailActivityFragment;
import com.serigon.moviezy.data.MovieContract.CastEntry;
import com.serigon.moviezy.data.MovieContract.GenreEntry;
import com.serigon.moviezy.data.MovieContract.MovieEntry;
import com.serigon.moviezy.data.MovieContract.SimilarEntry;
import com.serigon.moviezy.data.MovieContract.TrailerEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by Kelechi on 10/9/2015.
 */

public class FetchMovieDetailTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchMovieDetailTask.class.getSimpleName();
    private final long mMovieId;
    private final Context mContext;
    private final DetailActivityFragment mActivityFragment;

    public FetchMovieDetailTask(Context context, Long movieId, DetailActivityFragment activity) {
        mContext = context;
        mMovieId = movieId;
        mActivityFragment = activity;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mActivityFragment.reloadLoaders();
    }

    /**
     * Take the String representing the Detailed information of a Movie in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     */

    @Override
    protected Void doInBackground(Void... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonString;

        try {
            final String THE_MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/movie/" + mMovieId;
            final String APPEND_TO_RESPONSE_PARAM = "append_to_response";
            final String append_to_response = "similar_movies,trailers,casts";
            final String API_KEY_PARAM = "api_key";
            final String api_key = BuildConfig.MOVIE_DB_API_KEY;

            Uri builtUri = Uri.parse(THE_MOVIE_DB_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, api_key)
                    .appendQueryParameter(APPEND_TO_RESPONSE_PARAM, append_to_response)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            movieJsonString = buffer.toString();

            getMovieDetailDataFromJson(movieJsonString);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }


    private void getMovieDetailDataFromJson(String movieJsonStr)
            throws JSONException {

        // Now we have a String representing the Movie Detail in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        // Movie information. - Title, Overview, Release date
        // Movie Detail information. - Trailers, Casts, Genre, and Similar Movies

        // Movie information
        final String ID = "id";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String POSTER_PATH = "poster_path";
        final String BACKDROP_PATH = "backdrop_path";
        final String VOTE_AVERAGE = "vote_average";
        final String VOTE_COUNT = "vote_count";
        final String POPULARITY = "popularity";

        // Trailer information
        final String NAME = "name";
        final String SOURCE = "source";
        final String MDB_TRAILER = "trailers";
        final String MDB_TRAILER_TYPE = "youtube";
        final String FILE_PATH = "file_path";

        // Genre information
        final String MDB_GENRE = "genres";

        // Similar Movies information
        final String MDB_SIMILAR_MOVIES = "similar_movies";
        final String MDB_RESULTS = "results";

        // Cast information
        final String CHARACTER = "character";
        final String PROFILE_PATH = "profile_path";
        final String MDB_CASTS = "casts";
        final String MDB_CAST = "cast";

        // Image sizes are "w92", "w154", "w185", "w342", "w500", "w780", or "original"
        final String IMAGE_SIZE = "w185";
        final String BACKDROP_SIZE = "w342";
        final String IMAGE_URL = "http://image.tmdb.org/t/p/" + IMAGE_SIZE;
        final String BACKDROP_URL = "http://image.tmdb.org/t/p/" + BACKDROP_SIZE;


        // MOVIE
        // Extract Movie information from JSON
        // and update the Movie with new information
        try {
            JSONObject json = new JSONObject(movieJsonStr);

            String id = json.getString(ID);
            String title = json.getString(TITLE);
            String overview = json.getString(OVERVIEW);
            String date = json.getString(RELEASE_DATE);
            String vote = json.getString(VOTE_AVERAGE);
            String poster_path = IMAGE_URL + json.getString(POSTER_PATH);
            String backdrop = BACKDROP_URL + json.getString(BACKDROP_PATH);
            String popularity = json.getString(POPULARITY);

            String selection = "movie_id = ?";
            String[] selectionArgs = new String[]{String.valueOf(id)};


            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieEntry.COLUMN_MOVIE_TITLE, title);
            movieValues.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, overview);
            movieValues.put(MovieEntry.COLUMN_MOVIE_DATE, date);
            movieValues.put(MovieEntry.COLUMN_MOVIE_POSTER, poster_path);
            movieValues.put(MovieEntry.COLUMN_MOVIE_VOTE, vote);
            movieValues.put(MovieEntry.COLUMN_MOVIE_POPULARITY, popularity);
            movieValues.put(MovieEntry.COLUMN_MOVIE_BACKDROP, backdrop);

            int updated = 0;
            if (movieValues.size() > 0)
                updated = mContext.getContentResolver().update(MovieEntry.CONTENT_URI, movieValues, selection, selectionArgs);

            Log.v(LOG_TAG, "Updated Movies . " + updated + " Updated");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


        // TRAILER
        // Extract Trailer information from JSON
        // and insert into the trailer table in database
        try {
            JSONObject forecastJson = new JSONObject(movieJsonStr);
            JSONObject trailerJson = forecastJson.getJSONObject(MDB_TRAILER);
            JSONArray trailerArray = trailerJson.getJSONArray(MDB_TRAILER_TYPE);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(trailerArray.length());

            for (int i = 0; i < trailerArray.length(); i++) {
                // These are the values that will be collected.
                String name;
                String source;

                // Get the JSON object representing the trailer
                JSONObject trailer = trailerArray.getJSONObject(i);

                name = trailer.getString(NAME);
                source = trailer.getString(SOURCE);

                ContentValues trailerValues = new ContentValues();

                trailerValues.put(TrailerEntry.COLUMN_MOVIE_KEY, mMovieId);
                trailerValues.put(TrailerEntry.COLUMN_TRAILER_NAME, name);
                trailerValues.put(TrailerEntry.COLUMN_TRAILER_SOURCE, source);

                cVVector.add(trailerValues);
            }

            int inserted = 0;
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                inserted = mContext.getContentResolver().bulkInsert(TrailerEntry.CONTENT_URI, cvArray);
            }
            Log.v(LOG_TAG, "Trailers Inserted . " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


        // GENRE
        // Extract Genre information from JSON
        // and insert into the genre table in database
        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray genreArray = movieJson.getJSONArray(MDB_GENRE);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(genreArray.length());

            for (int i = 0; i < genreArray.length(); i++) {
                // These are the values that will be collected.
                String name;
                String sourceKey;

                JSONObject genre = genreArray.getJSONObject(i);

                name = genre.getString(NAME);
                sourceKey = genre.getString(ID);

                ContentValues genreValues = new ContentValues();

                genreValues.put(GenreEntry.COLUMN_MOVIE_KEY, mMovieId);
                genreValues.put(GenreEntry.COLUMN_GENRE_NAME, name);
                genreValues.put(GenreEntry.COLUMN_GENRE_SOURCE_ID, sourceKey);

                // Only add a genre name if does not contain "Trailer"
                if (!name.contains("Trailer"))
                    cVVector.add(genreValues);

            }

            int inserted = 0;
            // add to database

            if (cVVector.size() > 0) {
                // Call bulkInsert to add the genreEntries to the database here
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                inserted = mContext.getContentResolver().bulkInsert(GenreEntry.CONTENT_URI, cvArray);
            }
            Log.v(LOG_TAG, "Genres Inserted . " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


        // SIMILAR MOVIES
        // Extract Similar Movies information from JSON
        // and insert into the similar_movies table in database
        try {
            JSONObject json = new JSONObject(movieJsonStr);
            JSONObject similarMoviesJson = json.getJSONObject(MDB_SIMILAR_MOVIES);
            JSONArray similarMoviesArray = similarMoviesJson.getJSONArray(MDB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(similarMoviesArray.length());
            Vector<ContentValues> similarCVVector = new Vector<ContentValues>(similarMoviesArray.length());


            for (int i = 0; i < similarMoviesArray.length(); i++) {
                // These are the values that will be collected.
                String file_path;

                // Get the JSON object representing the similar movie
                JSONObject movie = similarMoviesArray.getJSONObject(i);

                String id;
                String title;
                String backdrop_path;
                String date;
                String poster_path;
                String vote;
                String popularity;

                id = movie.getString(ID);
                title = movie.getString(TITLE);
                date = movie.getString(RELEASE_DATE);
                poster_path = IMAGE_URL + movie.getString(POSTER_PATH);
                backdrop_path = BACKDROP_URL + movie.getString(BACKDROP_PATH);
                vote = movie.getString(VOTE_AVERAGE);
                popularity = movie.getString(POPULARITY);

                ContentValues similarValues = new ContentValues();

                similarValues.put(SimilarEntry.COLUMN_MOVIE_KEY, mMovieId);
                similarValues.put(SimilarEntry.COLUMN_SIMILAR_MOVIE_ID, id);
                similarValues.put(SimilarEntry.COLUMN_SIMILAR_POSTER_PATH, poster_path);

                similarCVVector.add(similarValues);


                // Similar Movies should also be inserted into Movies

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieEntry.COLUMN_MOVIE_ID, id);
                movieValues.put(MovieEntry.COLUMN_MOVIE_TITLE, title);
                movieValues.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, "");
                movieValues.put(MovieEntry.COLUMN_MOVIE_DATE, date);
                movieValues.put(MovieEntry.COLUMN_MOVIE_POSTER, poster_path);
                movieValues.put(MovieEntry.COLUMN_MOVIE_BACKDROP, backdrop_path);
                movieValues.put(MovieEntry.COLUMN_MOVIE_VOTE, vote);
                movieValues.put(MovieEntry.COLUMN_MOVIE_POPULARITY, popularity);
                movieValues.put(MovieEntry.COLUMN_MOVIE_FAVORITE, "false");
                movieValues.put(MovieEntry.COLUMN_MOVIE_INCLUDE, "false");

                cVVector.add(movieValues);
            }


            int inserted = 0;
            if (similarCVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[similarCVVector.size()];
                similarCVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(SimilarEntry.CONTENT_URI, cvArray);
            }
            Log.v(LOG_TAG, "Similar Movies Inserted . " + inserted + " Inserted");

            inserted = 0;
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }
            Log.v(LOG_TAG, "Movies Inserted . " + inserted + " Inserted");


        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


        // CASTS
        // Extract Cast information from JSON
        // and insert into the cast table in database
        try {
            JSONObject json = new JSONObject(movieJsonStr);
            JSONObject castsMoviesJson = json.getJSONObject(MDB_CASTS);
            JSONArray castMoviesArray = castsMoviesJson.getJSONArray(MDB_CAST);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(castMoviesArray.length());

            for (int i = 0; i < castMoviesArray.length(); i++) {

                // Get the JSON object representing the day
                JSONObject cast = castMoviesArray.getJSONObject(i);

                String id;
                String character;
                String name;
                String profile_path;

                id = cast.getString(ID);
                character = cast.getString(CHARACTER);
                name = cast.getString(NAME);
                profile_path = IMAGE_URL + cast.getString(PROFILE_PATH);

                ContentValues castValues = new ContentValues();

                castValues.put(CastEntry.COLUMN_CAST_ID, id);
                castValues.put(CastEntry.COLUMN_MOVIE_KEY, mMovieId);
                castValues.put(CastEntry.COLUMN_CAST_NAME, name);
                castValues.put(CastEntry.COLUMN_CAST_CHARACTER, character);
                castValues.put(CastEntry.COLUMN_CAST_PROFILE_PATH, profile_path);

                cVVector.add(castValues);
            }

            int inserted = 0;
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(CastEntry.CONTENT_URI, cvArray);
            }
            Log.v(LOG_TAG, "Casts Inserted . " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}