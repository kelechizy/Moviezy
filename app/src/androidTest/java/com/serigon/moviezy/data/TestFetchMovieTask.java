package com.serigon.moviezy.data;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.serigon.moviezy.adapter.MovieAdapter;
import com.serigon.moviezy.task.FetchMovieTask;


/**
 * Created by Kelechi on 9/29/2015.
 */

public class TestFetchMovieTask extends AndroidTestCase {
    static final String ADD_MOVIE_ID = "12345";
    static final String ADD_MOVIE_TITLE = "Sunnydale ASS";
    static final String ADD_MOVIE_OVERVIEW = "Overview";
    static final String ADD_MOVIE_DATE = "Date";
    static final String ADD_MOVIE_POSTER = "poster";
    static final String ADD_MOVIE_BACKDROP = "backdrop";
    static final String ADD_MOVIE_VOTE = "vote";
    static final Long ADD_MOVIE_POPULARITY = Long.valueOf(1232223);
    static final String SORT_BY="popularity.desc";


    /*
        Students: uncomment testAddLocation after you have written the AddLocation function.
        This test will only run on API level 11 and higher because of a requirement in the
        content provider.
     */

    @TargetApi(11)
    public void testAddMovie() {
        // start from a clean state
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{ADD_MOVIE_ID});

        FetchMovieTask fwt = new FetchMovieTask(getContext(),new MovieAdapter(getContext()),SORT_BY,1,null);
        long movieId = fwt.addMovie(ADD_MOVIE_ID, ADD_MOVIE_TITLE,
                ADD_MOVIE_OVERVIEW, ADD_MOVIE_DATE,ADD_MOVIE_POSTER,ADD_MOVIE_BACKDROP,ADD_MOVIE_VOTE,ADD_MOVIE_POPULARITY);

        // does addLocation return a valid record ID?
        assertFalse("Error: addLocation returned an invalid ID on insert",
                movieId == -1);

        // test all this twice
        for ( int i = 0; i < 2; i++ ) {

            // does the ID point to our location?
            Cursor locationCursor = getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{
                            MovieContract.MovieEntry._ID,
                            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                            MovieContract.MovieEntry.COLUMN_MOVIE_DATE,
                            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
                            MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP,
                            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE
                    },
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                    new String[]{ADD_MOVIE_ID},
                    null);

            // these match the indices of the projection
            if (locationCursor.moveToFirst()) {
                assertEquals("Error: the queried value of movieId does not match the returned value" +
                        "from addLocation", locationCursor.getLong(0), movieId);
                assertEquals("Error: the queried value of movie id is incorrect",
                        locationCursor.getString(1), ADD_MOVIE_ID);
                assertEquals("Error: the queried value of movie title is incorrect",
                        locationCursor.getString(2), ADD_MOVIE_TITLE);
                assertEquals("Error: the queried value of movie overview is incorrect",
                        locationCursor.getString(3), ADD_MOVIE_OVERVIEW);
                assertEquals("Error: the queried value of movie date is incorrect",
                        locationCursor.getString(4), ADD_MOVIE_DATE);
                assertEquals("Error: the queried value of movie poster is incorrect",
                        locationCursor.getString(5), ADD_MOVIE_POSTER);
                assertEquals("Error: the queried value of movie backdrop is incorrect",
                        locationCursor.getString(6), ADD_MOVIE_BACKDROP);
                assertEquals("Error: the queried value of movie vote is incorrect",
                        locationCursor.getString(7), ADD_MOVIE_VOTE);
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }

            // there should be no more records
            assertFalse("Error: there should be only one record returned from a location query",
                    locationCursor.moveToNext());

            // add the location again
            long newmovieId = fwt.addMovie(ADD_MOVIE_ID, ADD_MOVIE_TITLE,
                    ADD_MOVIE_OVERVIEW, ADD_MOVIE_DATE, ADD_MOVIE_POSTER,ADD_MOVIE_BACKDROP,ADD_MOVIE_VOTE, ADD_MOVIE_POPULARITY);

            assertEquals("Error: inserting a movie again should return the same ID",
                    movieId, newmovieId);
        }
        // reset our state back to normal
        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{ADD_MOVIE_ID});

        // clean up the test so that other tests can use the content provider
        getContext().getContentResolver().
                acquireContentProviderClient(MovieContract.MovieEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();
    }
}