package com.serigon.moviezy.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Kelechi on 10/11/2015.
 */
public class MovieProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int MOVIE_WITH_TRAILER = 102;

    static final int TRAILER = 200;
    static final int TRAILER_FOR_MOVIE = 201;

    static final int GENRE = 300;
    static final int GENRE_FOR_MOVIE = 301;

    static final int CAST = 400;
    static final int CAST_FOR_MOVIE = 401;

    static final int SIMILAR = 500;
    static final int SIMILAR_FOR_MOVIE = 501;

    static final int PERSON = 600;
    static final int PERSON_WITH_ID = 601;

    static final int CREDIT = 700;
    static final int CREDIT_FOR_PERSON = 701;

    private static final SQLiteQueryBuilder sMovieByIdQueryBuilder;

    private static final SQLiteQueryBuilder sMovieWithTrailerQueryBuilder;
    private static final SQLiteQueryBuilder sTrailerForMovieQueryBuilder;
    private static final SQLiteQueryBuilder sGenreForMovieQueryBuilder;
    private static final SQLiteQueryBuilder sCastForMovieQueryBuilder;
    private static final SQLiteQueryBuilder sSimilarForMovieQueryBuilder;

    private static final SQLiteQueryBuilder sPersonByIdQueryBuilder;
    private static final SQLiteQueryBuilder sCreditForPersonQueryBuilder;

    static {
        sMovieByIdQueryBuilder = new SQLiteQueryBuilder();
        sMovieWithTrailerQueryBuilder = new SQLiteQueryBuilder();
        sTrailerForMovieQueryBuilder = new SQLiteQueryBuilder();
        sGenreForMovieQueryBuilder = new SQLiteQueryBuilder();
        sCastForMovieQueryBuilder = new SQLiteQueryBuilder();
        sSimilarForMovieQueryBuilder = new SQLiteQueryBuilder();
        sPersonByIdQueryBuilder = new SQLiteQueryBuilder();
        sCreditForPersonQueryBuilder = new SQLiteQueryBuilder();


        //This is an inner join which looks like
        //movie INNER JOIN trailer ON movie.movie_id = trailer.movie_key
        sMovieWithTrailerQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.TrailerEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.TrailerEntry.TABLE_NAME +
                        "." + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY);

        sMovieByIdQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);

        sTrailerForMovieQueryBuilder.setTables(MovieContract.TrailerEntry.TABLE_NAME);

        sGenreForMovieQueryBuilder.setTables(MovieContract.GenreEntry.TABLE_NAME);

        sCastForMovieQueryBuilder.setTables(MovieContract.CastEntry.TABLE_NAME);

        sSimilarForMovieQueryBuilder.setTables(MovieContract.SimilarEntry.TABLE_NAME);

        sPersonByIdQueryBuilder.setTables(MovieContract.PersonEntry.TABLE_NAME);

        sCreditForPersonQueryBuilder.setTables(MovieContract.CreditEntry.TABLE_NAME);


    }

    // movie.movie_id = ?
    private static final String sMovieIdSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    // location.location_setting = ?
    private static final String sMovieWithTrailerSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ? ";


    // location.location_setting = ? AND date = ?
    private static final String sMovieIdAndDaySelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? AND " +
                    MovieContract.MovieEntry.COLUMN_MOVIE_DATE + " = ? ";


    // trailer.movie_key = ?
    private static final String sTrailerForMovieSelection =
            MovieContract.TrailerEntry.TABLE_NAME +
                    "." + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ? ";

    // genre.movie_key = ?
    private static final String sGenreForMovieSelection =
            MovieContract.GenreEntry.TABLE_NAME +
                    "." + MovieContract.GenreEntry.COLUMN_MOVIE_KEY + " = ? ";

    // cast.movie_key = ?
    private static final String sCastForMovieSelection =
            MovieContract.CastEntry.TABLE_NAME +
                    "." + MovieContract.CastEntry.COLUMN_MOVIE_KEY + " = ? ";

    // similar.movie_key = ?
    private static final String sSimilarForMovieSelection =
            MovieContract.SimilarEntry.TABLE_NAME +
                    "." + MovieContract.SimilarEntry.COLUMN_MOVIE_KEY + " = ? ";

    // person.person_id = ?
    private static final String sPersonSelection =
            MovieContract.PersonEntry.TABLE_NAME +
                    "." + MovieContract.PersonEntry.COLUMN_PERSON_ID + " = ? ";

    // credit.person_key = ?
    private static final String sCreditForPersonSelection =
            MovieContract.CreditEntry.TABLE_NAME +
                    "." + MovieContract.CreditEntry.COLUMN_PERSON_KEY + " = ? ";


    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;


        // 2) Use the addURI function to match each of the types.  Use the constants from
        // MovieContract to help define the types to the UriMatcher.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);

        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/*", TRAILER_FOR_MOVIE);

        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);

        matcher.addURI(authority, MovieContract.PATH_GENRE + "/*", GENRE_FOR_MOVIE);

        matcher.addURI(authority, MovieContract.PATH_GENRE, GENRE);

        matcher.addURI(authority, MovieContract.PATH_CAST + "/*", CAST_FOR_MOVIE);

        matcher.addURI(authority, MovieContract.PATH_CAST, CAST);

        matcher.addURI(authority, MovieContract.PATH_SIMILAR + "/*", SIMILAR_FOR_MOVIE);

        matcher.addURI(authority, MovieContract.PATH_SIMILAR, SIMILAR);

        matcher.addURI(authority, MovieContract.PATH_PERSON, PERSON);

        matcher.addURI(authority, MovieContract.PATH_PERSON + "/*", PERSON_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_CREDIT, CREDIT);

        matcher.addURI(authority, MovieContract.PATH_CREDIT + "/*", CREDIT_FOR_PERSON);

        // 3) Return the new matcher!
        return matcher;
    }



    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /*
        Here's where you'll code the getType function that uses the UriMatcher.
        You can test this by uncommenting testGetType in TestProvider.
     */

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.

        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case TRAILER_FOR_MOVIE:
                return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case GENRE_FOR_MOVIE:
                return MovieContract.GenreEntry.CONTENT_ITEM_TYPE;
            case GENRE:
                return MovieContract.GenreEntry.CONTENT_TYPE;
            case CAST_FOR_MOVIE:
                return MovieContract.CastEntry.CONTENT_ITEM_TYPE;
            case CAST:
                return MovieContract.CastEntry.CONTENT_TYPE;
            case SIMILAR_FOR_MOVIE:
                return MovieContract.SimilarEntry.CONTENT_ITEM_TYPE;
            case SIMILAR:
                return MovieContract.SimilarEntry.CONTENT_TYPE;
            case PERSON:
                return MovieContract.PersonEntry.CONTENT_TYPE;
            case PERSON_WITH_ID:
                return MovieContract.PersonEntry.CONTENT_ITEM_TYPE;
            case CREDIT:
                return MovieContract.CreditEntry.CONTENT_TYPE;
            case CREDIT_FOR_PERSON:
                return MovieContract.CreditEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // "movie/*"
            case MOVIE_WITH_ID: {
                retCursor = getMovieById(uri, projection, sortOrder);
                break;
            }

            // "MOVIE"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // "MOVIE WITH TRAILER"
            case TRAILER_FOR_MOVIE: {

                String movieId = MovieContract.MovieEntry.getIdFromUri(uri);
                selectionArgs = new String[]{movieId};
                selection = sTrailerForMovieSelection;

                return sTrailerForMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

            }

            // "TRAILER"
//            case TRAILER: {
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        MovieContract.TrailerEntry.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        null
//                );
//                break;
//            }

            // "GENRE FOR MOVIE"
            case GENRE_FOR_MOVIE: {

                String movieId = MovieContract.MovieEntry.getIdFromUri(uri);
                selectionArgs = new String[]{movieId};
                selection = sGenreForMovieSelection;

                return sGenreForMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

            }

            // "GENRE"
//            case GENRE: {
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        MovieContract.GenreEntry.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        null
//                );
//                break;
//            }

            // "CAST FOR MOVIE"
            case CAST_FOR_MOVIE: {

                String movieId = MovieContract.MovieEntry.getIdFromUri(uri);
                selectionArgs = new String[]{movieId};
                selection = sCastForMovieSelection;

                return sCastForMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
            }

            // "CAST"
//            case CAST: {
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        MovieContract.CastEntry.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        null
//                );
//                break;
//            }

            // "MOVIE WITH SIMILAR MOVIES"
            case SIMILAR_FOR_MOVIE: {

                String movieId = MovieContract.MovieEntry.getIdFromUri(uri);
                selectionArgs = new String[]{movieId};
                selection = sSimilarForMovieSelection;

                return sSimilarForMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

            }

//            case SIMILAR: {
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        MovieContract.SimilarEntry.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        null
//                );
//                break;
//            }

            case PERSON_WITH_ID: {
                String personId = MovieContract.PersonEntry.getIdFromUri(uri);
                selectionArgs = new String[]{personId};
                selection = sPersonSelection;

                retCursor = sPersonByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            }

            case PERSON: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.PersonEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

//            case CREDIT: {
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        MovieContract.CreditEntry.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        null
//                );
//                break;
//            }

            case CREDIT_FOR_PERSON: {
                String personId = MovieContract.PersonEntry.getIdFromUri(uri);
                selectionArgs = new String[]{personId};
                selection = sCreditForPersonSelection;

                return sCreditForPersonQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        String movieId = MovieContract.MovieEntry.getIdFromUri(uri);
        String[] selectionArgs = new String[]{movieId};
        String selection = sMovieIdSelection;

        return sMovieByIdQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

    }

    /*
        Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }

            case TRAILER: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }

            case GENRE: {
                long _id = db.insert(MovieContract.GenreEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.GenreEntry.buildGenreUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }


            case CAST: {
                long _id = db.insert(MovieContract.CastEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.CastEntry.buildCastUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }

            case SIMILAR: {
                long _id = db.insert(MovieContract.SimilarEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.SimilarEntry.buildSimilarUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }

            case PERSON: {
                long _id = db.insert(MovieContract.PersonEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.PersonEntry.buildPersonUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }

            case CREDIT: {
                long _id = db.insert(MovieContract.CreditEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.CreditEntry.buildCreditUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // Use the uriMatcher to match the MOVIE, TRAILER, CAST, SIMILAR, PERSON URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.

        // A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.

        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case TRAILER:
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case GENRE:
                rowsDeleted = db.delete(MovieContract.GenreEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case CAST:
                rowsDeleted = db.delete(MovieContract.CastEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case SIMILAR:
                rowsDeleted = db.delete(MovieContract.SimilarEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PERSON:
                rowsDeleted = db.delete(MovieContract.PersonEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case CREDIT:
                rowsDeleted = db.delete(MovieContract.CreditEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the actual rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GENRE:
                rowsUpdated = db.update(MovieContract.GenreEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CAST:
                rowsUpdated = db.update(MovieContract.CastEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SIMILAR:
                rowsUpdated = db.update(MovieContract.SimilarEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PERSON:
                rowsUpdated = db.update(MovieContract.PersonEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CREDIT:
                rowsUpdated = db.update(MovieContract.CreditEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case TRAILER:
                db.beginTransaction();
                int returnCounter = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCounter++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCounter;

            case GENRE:
                db.beginTransaction();
                int returnCounter2 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.GenreEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCounter2++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCounter2;

            case CAST:
                db.beginTransaction();
                int returnCounter3 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.CastEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCounter3++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCounter3;


            case SIMILAR:
                db.beginTransaction();
                int returnCounter4 = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.SimilarEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCounter4++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCounter4;

            case PERSON:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.PersonEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case CREDIT:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.CreditEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }

}
