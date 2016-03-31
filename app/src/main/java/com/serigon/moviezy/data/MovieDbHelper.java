package com.serigon.moviezy.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.serigon.moviezy.data.MovieContract.CastEntry;
import com.serigon.moviezy.data.MovieContract.GenreEntry;
import com.serigon.moviezy.data.MovieContract.MovieEntry;
import com.serigon.moviezy.data.MovieContract.SimilarEntry;
import com.serigon.moviezy.data.MovieContract.TrailerEntry;
import com.serigon.moviezy.data.MovieContract.PersonEntry;
import com.serigon.moviezy.data.MovieContract.CreditEntry;


/**
 * Created by Kelechi on 9/27/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "moviezy.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for movies
                // it's reasonable to assume the user will want information
                // for a certain movie and all movies *following*, so the movies data
                // should be sorted accordingly.
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the real ID of the movie for further api calls
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +

                MovieEntry.COLUMN_MOVIE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_VOTE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_BACKDROP + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POPULARITY + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_FAVORITE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_INCLUDE + " TEXT NOT NULL, " +

                // To assure the application have just one movie entry per date
                // per movie_id, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_DATE + ", " +
                MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_SOURCE + " TEXT NOT NULL, " +

                // Set up the movie_key column as a foreign key to trailer table.
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +

                // To assure the application have just one trailer entry per name
                // per movie, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + TrailerEntry.COLUMN_TRAILER_NAME + ", " +
                TrailerEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_GENRE_TABLE = "CREATE TABLE " + GenreEntry.TABLE_NAME + " (" +
                GenreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                GenreEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                GenreEntry.COLUMN_GENRE_NAME + " TEXT NOT NULL, " +
                GenreEntry.COLUMN_GENRE_SOURCE_ID + " TEXT NOT NULL, " +

                // Set up the movie_key column as a foreign key to genre table.
                " FOREIGN KEY (" + GenreEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +

                // To assure the application have just one genre entry per name
                // per movie, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + GenreEntry.COLUMN_GENRE_NAME + ", " +
                GenreEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_CAST_TABLE = "CREATE TABLE " + CastEntry.TABLE_NAME + " (" +
                CastEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CastEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                CastEntry.COLUMN_CAST_ID + " INTEGER NOT NULL, " +
                CastEntry.COLUMN_CAST_NAME + " TEXT NOT NULL, " +
                CastEntry.COLUMN_CAST_CHARACTER + " TEXT NOT NULL, " +
                CastEntry.COLUMN_CAST_PROFILE_PATH + " TEXT NOT NULL, " +

                // Set up the movie_key column as a foreign key to cast table.
                " FOREIGN KEY (" + CastEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +

                // To assure the application have just one cast entry per person
                // per movie, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + CastEntry.COLUMN_CAST_ID + ", " +
                CastEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_SIMILAR_TABLE = "CREATE TABLE " + SimilarEntry.TABLE_NAME + " (" +
                SimilarEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SimilarEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                SimilarEntry.COLUMN_SIMILAR_MOVIE_ID + " INTEGER NOT NULL, " +
                SimilarEntry.COLUMN_SIMILAR_POSTER_PATH + " STRING NOT NULL, " +

                // Set up the movie_key column as a foreign key to similar table.
                " FOREIGN KEY (" + SimilarEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +

                // To assure the application have just one similar movie entry
                // per movie, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + SimilarEntry.COLUMN_SIMILAR_MOVIE_ID + ", " +
                SimilarEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_PERSON_TABLE = "CREATE TABLE " + PersonEntry.TABLE_NAME + " (" +
                PersonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PersonEntry.COLUMN_PERSON_ID + " TEXT NOT NULL, " +
                PersonEntry.COLUMN_PERSON_NAME + " TEXT NOT NULL, " +
                PersonEntry.COLUMN_PERSON_BIOGRAPHY + " TEXT NOT NULL, " +
                PersonEntry.COLUMN_PERSON_GENDER + " TEXT NOT NULL, " +
                PersonEntry.COLUMN_PERSON_BIRTHDAY + " TEXT NOT NULL, " +
                PersonEntry.COLUMN_PERSON_DEATHDAY + " TEXT NOT NULL, " +
                PersonEntry.COLUMN_PERSON_PLACE_OF_BIRTH + " TEXT NOT NULL, " +
                PersonEntry.COLUMN_PERSON_POPULARITY+ " TEXT NOT NULL, " +
                PersonEntry.COLUMN_PERSON_PROFILE_PATH+ " TEXT NOT NULL, " +

                // To assure the application have just one person entry per id
                // it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + PersonEntry.COLUMN_PERSON_ID + ") ON CONFLICT REPLACE);";



        final String SQL_CREATE_CREDIT_TABLE = "CREATE TABLE " + CreditEntry.TABLE_NAME + " (" +
                CreditEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CreditEntry.COLUMN_PERSON_KEY + " INTEGER NOT NULL, " +
                CreditEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                CreditEntry.COLUMN_CREDIT_ID + " TEXT NOT NULL, " +
                CreditEntry.COLUMN_CREDIT_CHARACTER + " TEXT NOT NULL, " +
                CreditEntry.COLUMN_CREDIT_TITLE +  " TEXT NOT NULL, " +
                CreditEntry.COLUMN_CREDIT_POSTER_PATH +  " TEXT NOT NULL, " +

                // Set up the person_key column as a foreign key to credit table.
                " FOREIGN KEY (" + CreditEntry.COLUMN_PERSON_KEY + ") REFERENCES " +
                PersonEntry.TABLE_NAME + " (" + PersonEntry.COLUMN_PERSON_ID + "), " +

                // To assure the application have just one credit entry per person
                // it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + CreditEntry.COLUMN_CREDIT_ID + ", " +
                CreditEntry.COLUMN_PERSON_KEY + ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GENRE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CAST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SIMILAR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PERSON_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CREDIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 8 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + GenreEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CastEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SimilarEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PersonEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CreditEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void refresh() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MovieEntry.TABLE_NAME, null, null);
    }
}
