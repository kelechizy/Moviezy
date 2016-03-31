package com.serigon.moviezy.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Kelechi on 10/11/2015.
 */
public class MovieContract {
    //The Content Authority is the name for the entire content provider
    public static final String CONTENT_AUTHORITY = "com.serigon.movietrend.data";

    //Use Content Authority to create the base URI's
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible Paths
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_GENRE = "genre";
    public static final String PATH_CAST = "cast";
    public static final String PATH_SIMILAR = "similar";
    public static final String PATH_PERSON = "person";
    public static final String PATH_CREDIT = "credit";


    /* Inner class that defines the contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        // Movie id as returned by API, to identify the specific movie
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_DATE = "date";
        public static final String COLUMN_MOVIE_VOTE = "vote";
        public static final String COLUMN_MOVIE_POSTER = "poster";
        public static final String COLUMN_MOVIE_BACKDROP = "backdrop";
        public static final String COLUMN_MOVIE_POPULARITY = "popularity";
        public static final String COLUMN_MOVIE_FAVORITE = "favorite";
        public static final String COLUMN_MOVIE_INCLUDE = "include";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static Uri buildMovieUriNoAppend() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildMovieUriWithTrailer(long id) {
            return CONTENT_URI.buildUpon().appendPath("" + id).appendPath("trailer").build();
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    /* Inner class that defines the contents of the trailer table */
    public static final class TrailerEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_TRAILER_NAME = "name";
        public static final String COLUMN_TRAILER_SOURCE = "source";

        // Column with the foreign key into the trailer table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrailerUriForMovieId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class GenreEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRE).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "genre";

        public static final String COLUMN_GENRE_NAME = "name";
        public static final String COLUMN_GENRE_SOURCE_ID = "source";

        // Column with the foreign key into the genre table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static Uri buildGenreUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildGenreUriForMovieId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class CastEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CAST).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAST;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CAST;

        public static final String TABLE_NAME = "casts";
        public static final String COLUMN_CAST_ID = "cast_id";
        public static final String COLUMN_CAST_NAME = "name";
        public static final String COLUMN_CAST_CHARACTER = "character";
        public static final String COLUMN_CAST_PROFILE_PATH = "profile_path";

        // Column with the foreign key into the casts table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static Uri buildCastUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCastUriForMovieId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class SimilarEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SIMILAR).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SIMILAR;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SIMILAR;

        public static final String TABLE_NAME = "similar";

        public static final String COLUMN_SIMILAR_MOVIE_ID = "similar_id";
        public static final String COLUMN_SIMILAR_POSTER_PATH = "poster_path";

        // Column with the foreign key into the similar table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        public static Uri buildSimilarUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildSimilarUriForMovieId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class PersonEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PERSON).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSON;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSON;

        public static final String TABLE_NAME = "person";

        public static final String COLUMN_PERSON_ID = "person_id";
        public static final String COLUMN_PERSON_NAME = "name";
        public static final String COLUMN_PERSON_BIOGRAPHY = "biography";
        public static final String COLUMN_PERSON_GENDER = "gender";
        public static final String COLUMN_PERSON_BIRTHDAY = "birthday";
        public static final String COLUMN_PERSON_DEATHDAY = "deathday";
        public static final String COLUMN_PERSON_PLACE_OF_BIRTH = "place_of_birth";
        public static final String COLUMN_PERSON_POPULARITY = "popularity";
        public static final String COLUMN_PERSON_PROFILE_PATH = "profile_path";

        public static Uri buildPersonUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }


    public static final class CreditEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CREDIT).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CREDIT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CREDIT;

        public static final String TABLE_NAME = "credit";

        public static final String COLUMN_PERSON_KEY = "person_id";
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        public static final String COLUMN_CREDIT_ID = "credit_id";
        public static final String COLUMN_CREDIT_CHARACTER = "character";
        public static final String COLUMN_CREDIT_TITLE = "title";
        public static final String COLUMN_CREDIT_POSTER_PATH = "poster_path";

        public static Uri buildCreditUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCreditUriForPersonId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
