package com.serigon.moviezy.task;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.serigon.movietrend.BuildConfig;
import com.serigon.moviezy.activity.MovieFragment;
import com.serigon.moviezy.adapter.MovieAdapter;
import com.serigon.moviezy.data.MovieContract;
import com.serigon.moviezy.data.MovieContract.MovieEntry;

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
public class FetchMovieTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private final MovieAdapter mMovieAdapter;
    private final Context mContext;
    private final String mSortBy;
    private final int mPage;
    private final MovieFragment mMovieFragment;


    public FetchMovieTask(Context context, MovieAdapter movieAdapter, String sortBy, int pageNumber, MovieFragment activity) {
        mContext = context;
        mMovieAdapter = movieAdapter;
        mSortBy = sortBy;
        mPage = pageNumber;
        mMovieFragment = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mMovieFragment.reloadLoader();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mMovieFragment.refreshCompleted();
    }

    private boolean DEBUG = true;

    /**
     * Helper method to handle insertion of a new movie in the movie database.
     *
     * @return the row ID of the added location.
     **/


    public long addMovie(String mMovieId, String title, String overview, String date, String poster, String backdrop, String vote, long popularity) {

        // Insert movie using the content resolver and the base URI

        String favorite = "false";
        long movieId;

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{mMovieId},
                null);

        if (movieCursor.moveToFirst()) {
            int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry._ID);
            movieId = movieCursor.getLong(movieIdIndex);
        }
        else {
            //Now that the content provider is set up, inserting rows of data is pretty simple
            //First create a ContentValues object to hold the data you want to insert.
            ContentValues movieValues = new ContentValues();

            //Then add the data, along with the corresponding name of the data type,
            //so the content provider knows what kind  of value is being inserted
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovieId);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_DATE, date);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, poster);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP, backdrop);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE, vote);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY, popularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, favorite);


            Uri insertedUri = mContext.getContentResolver().insert(
                    MovieContract.MovieEntry.CONTENT_URI,
                    movieValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            movieId = ContentUris.parseId(insertedUri);
        }

        movieCursor.close();
        // Wait, that worked?  Yes!
        return movieId;
    }

    /**
     * Take the String representing the complete movies in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */


    @Override
    protected Void doInBackground(Void... params) {

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String moviesJsonStr = null;

        try {
            // Construct the URL for the The Movie DB query
            // Possible parameters are avaiable at The Movie DB API page, at
            // http://api.themoviedb.org
            //
            // Sample api call
            // http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=1234567890abcdefghijklmno


            final String THE_MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
            final String KEY_PARAM = "api_key";
            final String PAGE_PARAM = "page";
            final String PRIMARY_RELEASE_YEAR = "primary_release_year";
            final String CERTIFICATION_COUNTRY = "certification_country";
            final String certification_country="US";
            final String SORT_BY = "sort_by";
            final String api_key = BuildConfig.MOVIE_DB_API_KEY;


            // Lets build the uri to request Movies using the api
            Uri builtUri = Uri.parse(THE_MOVIE_DB_BASE_URL).buildUpon()
                    .appendQueryParameter(CERTIFICATION_COUNTRY, certification_country)
                    .appendQueryParameter(KEY_PARAM, api_key)
                    .appendQueryParameter(PAGE_PARAM, "" + mPage)
                    .build();

            if (!mSortBy.isEmpty() || mSortBy != null) {
                builtUri = builtUri.buildUpon()
                        .appendQueryParameter(SORT_BY, mSortBy)
                        .build();
            }


            URL url = new URL(builtUri.toString());
            Log.e("The ULR" + url.toString(), url.toString());

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

            moviesJsonStr = buffer.toString();

            getMovieDataFromJson(moviesJsonStr);

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


    private void getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // Now we have a String representing the complete movies list in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        // Movie result information.  Each movie's info is an element of the "list" array.
        final String OWM_LIST = "results";

        // Movie information
        final String BACKDROP_PATH = "backdrop_path";
        final String ID = "id";
        final String TITLE = "title";
        final String OVERVIEW = "overview";
        final String RELEASE_DATE = "release_date";
        final String POSTER_PATH = "poster_path";
        final String VOTE_AVERAGE = "vote_average";
        final String VOTE_COUNT = "vote_count";
        final String POPULARITY = "popularity";
        final String INCLUDE = "yes";
        // Note*
        // A new field in Movies table indicates whether to include in the Home Screen
        // Not all movies are included in the Main Screen
        // For example: Similar Movies, Credit Movies are into the Movies table but set with
        // INCLUDE = 'no'

        // Image sizes are "w92", "w154", "w185", "w342", "w500", "w780", or "original"
        final String IMAGE_SIZE = "w185";
        final String BACKDROP_SIZE = "w342";
        final String IMAGE_URL = "http://image.tmdb.org/t/p/" + IMAGE_SIZE;
        final String BACKDROP_URL = "http://image.tmdb.org/t/p/" + BACKDROP_SIZE;


        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_LIST);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {

                // These are the values that will be collected.

                JSONObject movie = movieArray.getJSONObject(i);

                String id;
                String title;
                String backdrop_path;
                String overview;
                String date;
                String poster_path;
                String vote;
                String popularity;


                id = movie.getString(ID);
                title = movie.getString(TITLE);
                overview = movie.getString(OVERVIEW);
                date = movie.getString(RELEASE_DATE);
                poster_path = IMAGE_URL + movie.getString(POSTER_PATH);
                backdrop_path = BACKDROP_URL + movie.getString(BACKDROP_PATH);
                vote = movie.getString(VOTE_AVERAGE);
                popularity = movie.getString(POPULARITY);


                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieEntry.COLUMN_MOVIE_ID, id);
                movieValues.put(MovieEntry.COLUMN_MOVIE_TITLE, title);
                movieValues.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, overview);
                movieValues.put(MovieEntry.COLUMN_MOVIE_DATE, date);
                movieValues.put(MovieEntry.COLUMN_MOVIE_POSTER, poster_path);
                movieValues.put(MovieEntry.COLUMN_MOVIE_BACKDROP, backdrop_path);
                movieValues.put(MovieEntry.COLUMN_MOVIE_VOTE, vote);
                movieValues.put(MovieEntry.COLUMN_MOVIE_POPULARITY, popularity);
                movieValues.put(MovieEntry.COLUMN_MOVIE_FAVORITE, "false");
                movieValues.put(MovieEntry.COLUMN_MOVIE_INCLUDE, "yes");

                cVVector.add(movieValues);

            }

            int inserted = 0;
            // add to database

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }

            Log.e(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }


}