package com.serigon.moviezy.task;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.serigon.movietrend.BuildConfig;
import com.serigon.moviezy.activity.PersonActivityFragment;
import com.serigon.moviezy.data.MovieContract;

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
 * Created by Kelechi on 3/20/2016.
 */
public class FetchPersonTask  extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchPersonTask.class.getSimpleName();

    private final long mPersonId;
    private final Context mContext;
    private final PersonActivityFragment mPersonActivityFragment;

    public FetchPersonTask(Context context, Long personId, PersonActivityFragment activity) {
        mContext = context;
        mPersonId = personId;
        mPersonActivityFragment = activity;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        mPersonActivityFragment.reloadLoaders();
    }

    /**
     * Take the string representing the Person in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     */

    @Override
    protected Void doInBackground(Void... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String personJsonString = null;


        try {
            // String Parameters used to build the URI
            final String THE_MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/person/" + mPersonId;
            final String APPEND_TO_RESPONSE_PARAM = "append_to_response";
            final String append_to_response = "movie_credits";
            final String API_KEY_PARAM = "api_key";
            final String api_key = BuildConfig.MOVIE_DB_API_KEY;

            Uri builtUri = Uri.parse(THE_MOVIE_DB_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, api_key)
                    .appendQueryParameter(APPEND_TO_RESPONSE_PARAM, append_to_response)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to The Movie DB api, and open the connection
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

            personJsonString = buffer.toString();

            getPersonDetailDataFromJson(personJsonString);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
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


    private void getPersonDetailDataFromJson(String personJsonStr)
            throws JSONException {

        // Here we are extracting 2 groups of data-set from the Person Json string
        // and insert them into 2 tables i.e Person table and Credit table
        // Person information of a person e.g Keanu Reeves...Bio, Place of Brith, Date of Birth,
        // Credit information of person e.g Keanu Reeves... Matrix, Matrix Revolution, Constantine


        // Person information
        final String ID = "id";
        final String BIOGRAPHY = "biography";
        final String NAME = "name";
        final String POPULARITY = "popularity";
        final String PROFILE_PATH = "profile_path";
        final String PLACE_OF_BIRTH = "place_of_birth";
        final String GENDER = "gender";
        final String BIRTHDAY = "birthday";
        final String DEATHDAY = "deathday";

        // Credit : Person's movie credits information
        final String MDB_MOVIE_CREDIT = "movie_credits";
        final String MDB_MOVIE_CREDIT_CAST = "cast";
        final String TITLE = "title";
        final String CHARACTER = "character";
        final String RELEASE_DATE = "release_date";
        final String POSTER_PATH = "poster_path";
        final String CREDIT_ID = "credit_id";

        // Image sizes are "w92", "w154", "w185", "w342", "w500", "w780", or "original"
        final String IMAGE_SIZE = "w185";
        final String BACKDROP_SIZE = "w342";
        final String IMAGE_URL = "http://image.tmdb.org/t/p/" + IMAGE_SIZE;
        final String BACKDROP_URL = "http://image.tmdb.org/t/p/" + BACKDROP_SIZE;


        // Extract the Person Information from json
        try {

            JSONObject json = new JSONObject(personJsonStr);

            String id = json.getString(ID);
            String name = json.getString(NAME);
            String biography = json.getString(BIOGRAPHY);
            String gender = json.getString(GENDER);
            String birthday = json.getString(BIRTHDAY);
            String deathday = json.getString(DEATHDAY);
            String place_of_birth = json.getString(PLACE_OF_BIRTH);
            String profile_path = IMAGE_URL + json.getString(PROFILE_PATH);
            String popularity = json.getString(POPULARITY);

            // Custom string validation to replace null and empty strings
            gender = (gender.equals("1"))? "Female": "Male";
            biography = (biography.equals("null")) ? "Not Available" : biography;
            birthday = (birthday.equals("null")) ? "Not Available" : birthday;
            place_of_birth = (place_of_birth.equals("null") || place_of_birth.trim() == "") ? "Not Available" : place_of_birth;


            Vector<ContentValues> cVVector = new Vector<ContentValues>(9);
            ContentValues personValues = new ContentValues();

            personValues.put(MovieContract.PersonEntry.COLUMN_PERSON_ID, id);
            personValues.put(MovieContract.PersonEntry.COLUMN_PERSON_NAME, name);
            personValues.put(MovieContract.PersonEntry.COLUMN_PERSON_BIOGRAPHY, biography);
            personValues.put(MovieContract.PersonEntry.COLUMN_PERSON_GENDER, gender);
            personValues.put(MovieContract.PersonEntry.COLUMN_PERSON_BIRTHDAY, birthday);
            personValues.put(MovieContract.PersonEntry.COLUMN_PERSON_DEATHDAY, deathday);
            personValues.put(MovieContract.PersonEntry.COLUMN_PERSON_PLACE_OF_BIRTH, place_of_birth);
            personValues.put(MovieContract.PersonEntry.COLUMN_PERSON_POPULARITY, popularity);
            personValues.put(MovieContract.PersonEntry.COLUMN_PERSON_PROFILE_PATH, profile_path);

            cVVector.add(personValues);

            int inserted = 0;

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.PersonEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "Person Inserted . " + inserted + " Inserted");

        } catch (JSONException e) {
           e.printStackTrace();
        }


        // Extract the Credit Information from json
        try {
            JSONObject json = new JSONObject(personJsonStr);
            JSONObject creditJson = json.getJSONObject(MDB_MOVIE_CREDIT);
            JSONArray creditArray = creditJson.getJSONArray(MDB_MOVIE_CREDIT_CAST);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(creditArray.length());
            Vector<ContentValues> moviesCVVector = new Vector<ContentValues>(creditArray.length());


            for (int i = 0; i < creditArray.length(); i++) {
                // These are the values that will be collected.
                String movieId;
                String creditId;
                String character;
                String title;
                String posterPath;
                String releaseDate;

                // Get the JSON object representing the movie credit
                JSONObject credit = creditArray.getJSONObject(i);

                movieId = credit.getString(ID);
                creditId = credit.getString(CREDIT_ID);
                character = credit.getString(CHARACTER);
                title = credit.getString(TITLE);
                posterPath = credit.getString(POSTER_PATH);
                releaseDate = credit.getString(RELEASE_DATE);

                // Only include movie credits that have posters
                if(posterPath != "null"){
                    posterPath = IMAGE_URL + posterPath;

                    ContentValues creditValues = new ContentValues();

                    creditValues.put(MovieContract.CreditEntry.COLUMN_CREDIT_ID, creditId);
                    creditValues.put(MovieContract.CreditEntry.COLUMN_MOVIE_KEY, movieId);
                    creditValues.put(MovieContract.CreditEntry.COLUMN_PERSON_KEY, mPersonId);
                    creditValues.put(MovieContract.CreditEntry.COLUMN_CREDIT_CHARACTER, character);
                    creditValues.put(MovieContract.CreditEntry.COLUMN_CREDIT_TITLE, title);
                    creditValues.put(MovieContract.CreditEntry.COLUMN_CREDIT_POSTER_PATH, posterPath);

                    cVVector.add(creditValues);


                    // Movie credits should also be inserted into Movies

                    ContentValues movieValues = new ContentValues();

                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, posterPath);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_DATE,releaseDate);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, "");
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP, "");
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE, "");
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY, "");
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE, "false");
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_INCLUDE, "false");

                    moviesCVVector.add(movieValues);
                }

            }

            int inserted = 0;
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.CreditEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "CREDIT Inserted . " + inserted + " Inserted");

            inserted = 0;
            if (moviesCVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[moviesCVVector.size()];
                moviesCVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "NEW MOVIE Inserted . " + inserted + " Inserted");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
