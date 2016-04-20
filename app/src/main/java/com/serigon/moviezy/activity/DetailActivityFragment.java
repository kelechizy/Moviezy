package com.serigon.moviezy.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.serigon.movietrend.BuildConfig;
import com.serigon.movietrend.R;
import com.serigon.moviezy.adapter.CastMiniAdapter;
import com.serigon.moviezy.adapter.GenreAdapter;
import com.serigon.moviezy.adapter.SimilarMovieAdapter;
import com.serigon.moviezy.data.MovieContract;
import com.serigon.moviezy.task.FetchMovieDetailTask;
import com.serigon.moviezy.utility.RecyclerItemClickListener;
import com.serigon.moviezy.utility.StringFormatter;


public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String DETAIL_URI = "DETAIL_URI";
    private static final String MOVIE_SHARE_HASHTAG = "#Moviezy";

    private static final int DETAIL_LOADER = 0;
    private static final int TRAILER_LOADER = 1;
    private static final int GENRE_LOADER = 2;
    private static final int CAST_LOADER = 3;
    private static final int SIMILAR_LOADER = 4;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY,
            MovieContract.MovieEntry.COLUMN_MOVIE_FAVORITE,
    };

    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_MOVIE_KEY,
            MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
            MovieContract.TrailerEntry.COLUMN_TRAILER_SOURCE,
    };

    private static final String[] GENRE_COLUMNS = {
            MovieContract.GenreEntry._ID,
            MovieContract.GenreEntry.COLUMN_MOVIE_KEY,
            MovieContract.GenreEntry.COLUMN_GENRE_NAME,
            MovieContract.GenreEntry.COLUMN_GENRE_SOURCE_ID,
    };

    private static final String[] CAST_COLUMNS = {
            MovieContract.CastEntry._ID,
            MovieContract.CastEntry.COLUMN_MOVIE_KEY,
            MovieContract.CastEntry.COLUMN_CAST_ID,
            MovieContract.CastEntry.COLUMN_CAST_NAME,
            MovieContract.CastEntry.COLUMN_CAST_CHARACTER,
            MovieContract.CastEntry.COLUMN_CAST_PROFILE_PATH,
    };

    private static final String[] SIMILAR_COLUMNS = {
            MovieContract.SimilarEntry._ID,
            MovieContract.SimilarEntry.COLUMN_MOVIE_KEY,
            MovieContract.SimilarEntry.COLUMN_SIMILAR_MOVIE_ID,
            MovieContract.SimilarEntry.COLUMN_SIMILAR_POSTER_PATH
    };

    // These indices are tied to MOVIE_COLUMNS, TRAILER_COLUMNS, GENRE_COLUMNS , CAST_COLUMNS and SIMILAR_COLUMNS
    // If MOVIE_COLUMNS, TRAILER_COLUMNS, GENRE_COLUMNS , CAST_COLUMNS and SIMILAR_COLUMNS changes,
    // these must change.

    static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_OVERVIEW = 3;
    public static final int COL_MOVIE_DATE = 4;
    public static final int COL_MOVIE_POSTER = 5;
    public static final int COL_MOVIE_BACKDROP = 6;
    public static final int COL_MOVIE_VOTE = 7;
    public static final int COL_MOVIE_POPULARITY = 8;
    public static final int COL_MOVIE_FAVORITE = 9;

    //public static final int COL_ID = 0;
    public static final int COL_TRAILER_MOVIE_KEY = 1;
    public static final int COL_TRAILER_NAME = 2;
    public static final int COL_TRAILER_SOURCE = 3;

    //public static final int COL_ID = 0;
    public static final int COL_GENRE_MOVIE_KEY = 1;
    public static final int COL_GENRE_NAME = 2;
    public static final int COL_GENRE_SOURCE_ID = 3;

    //public static final int COL_ID = 0;
    public static final int COL_CAST_MOVIE_KEY = 1;
    public static final int COL_CAST_ID = 2;
    public static final int COL_CAST_NAME = 3;
    public static final int COL_CAST_CHARACTER = 4;
    public static final int COL_CAST_PROFILE_PATH = 5;

    //public static final int COL_ID = 0;
    public static final int COL_SIMILAR_MOVIE_KEY = 1;
    public static final int COL_SIMILAR_SIMILAR_MOVIE_ID = 2;
    public static final int COL_SIMILAR_POSTER_PATH = 3;

    private ImageView mPoster;
    private ImageView mBackdrop;
    private TextView mTitle;
    private TextView mOverview;
    private TextView mVote;
    private TextView mDate;
    private Button mViewCastButton;
    private RecyclerView mGenre;
    private RecyclerView mSimilar;
    private GridView mSimilarGridView;
    private GridView mCastGridView;

    private Uri mUri;
    private Uri mTrailerUri;
    private Uri mGenreUri;
    private Uri mCastUri;
    private Uri mSimilarUri;
    private Long mMovieId;
    private String mMovieTitle;
    private String mMovieOverview;
    private String mMovieDate;
    private Double mMovieVote;
    private String mMoviePoster;
    private String mMovieBackdrop;
    private GenreAdapter mGenreRecyclerViewAdapter;
    private SimilarMovieAdapter mSimilarRecyclerViewAdapter;
    private CastMiniAdapter mCastMiniAdapter;

    private boolean fragmentWasPaused = false;
    private String pauseMovieTitle;
    private String pauseMovieOverview;
    private String pauseMovieDate;
    private String pauseMovieVote;
    private String pauseMoviePoster;
    private String pauseMovieBackdrop;

    public interface Callback {
        public void onItemSelected(Uri contentUri);
    }

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentWasPaused = true;
        Intent pauseData = getActivity().getIntent();

        if(fragmentWasPaused && pauseData.getStringExtra("MOVIE_HAS_DATA") != "YES") {
            pauseData.putExtra("MOVIE_TITLE",mMovieTitle);
            pauseData.putExtra("MOVIE_OVERVIEW",mMovieOverview);
            pauseData.putExtra("MOVIE_POSTER",mMoviePoster);
            pauseData.putExtra("MOVIE_BACKDROP",mMovieBackdrop);
            pauseData.putExtra("MOVIE_DATE",mMovieDate);
            pauseData.putExtra("MOVIE_VOTE",mMovieVote);
            pauseData.putExtra("MOVIE_HAS_DATA","YES");
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        if (fragmentWasPaused) {
            getLoaderManager().destroyLoader(DETAIL_LOADER);

            pauseMovieTitle = getActivity().getIntent().getStringExtra("MOVIE_TITLE");
            pauseMovieOverview = getActivity().getIntent().getStringExtra("MOVIE_OVERVIEW");
            pauseMoviePoster = getActivity().getIntent().getStringExtra("MOVIE_POSTER");
            pauseMovieBackdrop = getActivity().getIntent().getStringExtra("MOVIE_BACKDROP");
            pauseMovieDate = getActivity().getIntent().getStringExtra("MOVIE_DATE");
            pauseMovieVote = getActivity().getIntent().getStringExtra("MOVIE_VOTE");

            mTitle.setText(pauseMovieTitle);
            mDate.setText(pauseMovieDate);
            mOverview.setText(pauseMovieOverview);
            mVote.setText(pauseMovieVote);

            Glide.with(getContext())
                    .load(pauseMoviePoster)
                    .crossFade()
                    .into(mPoster);

            Glide.with(getContext())
                    .load(pauseMovieBackdrop)
                    .crossFade()
                    .into(mBackdrop);

            fragmentWasPaused = false;
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        // Get movieID for Movie to build Trailer URI, Genre URI, Cast URI and Similar URI
        // Example, if mMovieId = 318846
        // mUri = content://com.serigon.movietrend.data/movie/318846
        // mTrailerUri = content://com.serigon.movietrend.data/trailer/318846
        // mGenreUri = content://com.serigon.movietrend.data/genre/318846
        // mCastUri = content://com.serigon.movietrend.data/casts/318846
        // mSimilarUri = content://com.serigon.movietrend.data/similar/318846

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
        }
        try {
            mMovieId = Long.parseLong(MovieContract.MovieEntry.getIdFromUri(mUri));
            mTrailerUri = MovieContract.TrailerEntry.buildTrailerUriForMovieId(mMovieId);
            mGenreUri = MovieContract.GenreEntry.buildGenreUriForMovieId(mMovieId);
            mCastUri = MovieContract.CastEntry.buildCastUriForMovieId(mMovieId);
            mSimilarUri = MovieContract.SimilarEntry.buildSimilarUriForMovieId(mMovieId);
        } catch (Exception e) {}

        mPoster = (ImageView) rootView.findViewById(R.id.detail_poster);
        mBackdrop = (ImageView) rootView.findViewById(R.id.detail_backdrop);
        mTitle = (TextView) rootView.findViewById(R.id.detail_title);
        mOverview = (TextView) rootView.findViewById(R.id.detail_overview);
        mVote = (TextView) rootView.findViewById(R.id.detail_vote);
        mDate = (TextView) rootView.findViewById(R.id.detail_date);
        mGenre = (RecyclerView) rootView.findViewById(R.id.genreRecyclerView);
        mSimilar = (RecyclerView) rootView.findViewById(R.id.similarRecyclerView);

        mGenreRecyclerViewAdapter = new GenreAdapter(getActivity(), null);
        mGenre.setAdapter(mGenreRecyclerViewAdapter);
        mGenre.setHasFixedSize(false);
        LinearLayoutManager mGenreLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mGenre.setLayoutManager(mGenreLinearLayoutManager);

        mSimilar = (RecyclerView) rootView.findViewById(R.id.similarRecyclerView);
        mSimilarRecyclerViewAdapter = new SimilarMovieAdapter(getActivity(), null);
        mSimilar.setHasFixedSize(false);

        LinearLayoutManager mSimilarLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mSimilar.setLayoutManager(mSimilarLinearLayoutManager);
        mSimilar.setAdapter(mSimilarRecyclerViewAdapter);

        mCastMiniAdapter = new CastMiniAdapter(getActivity());
        mCastGridView = (GridView) rootView.findViewById(R.id.grid_cast);
        mCastGridView.setAdapter(mCastMiniAdapter);

        mViewCastButton = (Button) rootView.findViewById(R.id.viewCastButton);

        if (mMovieId != null) {
            // Start Loader to get Movie Details, Casts, Trailer, Genre and Similar Movies
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            getLoaderManager().initLoader(CAST_LOADER, null, castLoaderListener);
            getLoaderManager().initLoader(TRAILER_LOADER, null, trailerLoaderListener);
            getLoaderManager().initLoader(GENRE_LOADER, null, genreLoaderListener);
            getLoaderManager().initLoader(SIMILAR_LOADER, null, similarLoaderListener);

            // Fetch the detail of a certain Movie
            // We are using AyncTask to do that
            FetchMovieDetailTask movieDetailTask = new FetchMovieDetailTask(getActivity(), mMovieId, this);
            movieDetailTask.execute();
        }

        setupToolbar(rootView);

        return rootView;
    }

    private void setupToolbar(View rootView) {
        // Add toolbar
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.app_bar);
        try {
            ((DetailActivity) getActivity()).setSupportActionBar(toolbar);
            ((DetailActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((DetailActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((DetailActivity) getActivity()).getSupportActionBar().setTitle("");
        } catch (Exception e) {}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            startActivity(createShareMovieIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detailfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        } else {
            Log.d("Sad", "Share Action Provider is null?");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null == mUri) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                mUri,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {
            mMovieTitle = data.getString(COL_MOVIE_TITLE);
            mMovieOverview = data.getString(COL_MOVIE_OVERVIEW);
            mMovieDate = StringFormatter.formatDate(data.getString(COL_MOVIE_DATE));
            mMoviePoster = data.getString(COL_MOVIE_POSTER);
            mMovieBackdrop = data.getString(COL_MOVIE_BACKDROP);
            mMovieVote = data.getDouble(COL_MOVIE_VOTE);

            mTitle.setText(mMovieTitle);
            mDate.setText(mMovieDate);
            mVote.setText((mMovieVote == 0) ? "No Rating" : mMovieVote.toString());
            mOverview.setText((!mMovieOverview.isEmpty()) ? mMovieOverview :"Not Available");

            Glide.with(getContext())
                    .load(mMoviePoster)
                    .placeholder(R.drawable.movieholder_dark)
                    .crossFade()
                    .into(mPoster);

            Glide.with(getContext())
                    .load(mMovieBackdrop)
                    .crossFade()
                    .into(mBackdrop);

            try {
                ((CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout)).setTitle(mMovieTitle);
            } catch (Exception e) {}
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    // TRAILER LOADER
    private LoaderManager.LoaderCallbacks<Cursor> trailerLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (null == mTrailerUri) {
                return null;
            }

            return new CursorLoader(
                    getActivity(),
                    mTrailerUri,
                    TRAILER_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Although the Cursor data has up to 3 trailers
            // Feature the first trailer and show fab
            if (data.getCount() != 0) {
                final Cursor mData = data;

                // Move cursor to the first trailer
                mData.moveToFirst();

                // Setup the FAB
                FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Cursor m = mData;
                        String VIDEO_ID = m.getString(DetailActivityFragment.COL_TRAILER_SOURCE);
                        String YT_KEY = BuildConfig.YOUTUBE_API_KEY;

                        Intent videoIntent = YouTubeStandalonePlayer.createVideoIntent(getActivity(), YT_KEY, VIDEO_ID, 0, true, false);
                        startActivityForResult(videoIntent, 1);
                    }
                });

                fab.show();
            }
            else
                getLoaderManager().restartLoader(TRAILER_LOADER, null, trailerLoaderListener);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {}
    };

    // GENRE LOADER
    private LoaderManager.LoaderCallbacks<Cursor> genreLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (null == mGenreUri) {
                return null;
            }

            return new CursorLoader(
                    getActivity(),
                    mGenreUri,
                    GENRE_COLUMNS,
                    null,
                    null,
                    "_id DESC"
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mGenreRecyclerViewAdapter.swapCursor(data);
            if (data.getCount() < 2)
                getLoaderManager().restartLoader(GENRE_LOADER, null, genreLoaderListener);
            mGenre.invalidate();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mGenreRecyclerViewAdapter.swapCursor(null);
        }
    };

    // SIMILAR MOVIES LOADER
    private LoaderManager.LoaderCallbacks<Cursor> similarLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (null == mSimilarUri) {
                return null;
            }

            return new CursorLoader(
                    getActivity(),
                    mSimilarUri,
                    SIMILAR_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mSimilarRecyclerViewAdapter.swapCursor(data);


            if (data.getCount() != 0) {
                mSimilar.addOnItemTouchListener(
                        new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Cursor mCursor = mSimilarRecyclerViewAdapter.getCursor();
                                mCursor.moveToPosition(position);

                                Uri contentUri = MovieContract.MovieEntry.buildMovieUri(mCursor.getInt(COL_SIMILAR_SIMILAR_MOVIE_ID));

                                Intent intent = new Intent(getActivity(), DetailActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        .setData(contentUri);
                                startActivity(intent);
                            }
                        })
                );
            }

            // For performance reasons we use postInvalidate
            // postInvalidate re-draws the view
            //mSimilarGridView.postInvalidate();
            //mSimilarCardView.postInvalidate();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mSimilarRecyclerViewAdapter.swapCursor(null);
        }
    };

    // CAST LOADER
    private LoaderManager.LoaderCallbacks<Cursor> castLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (null == mCastUri) {
                return null;
            }

            return new CursorLoader(
                    getActivity(),
                    mCastUri,
                    CAST_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCastMiniAdapter.swapCursor(data);

            final Cursor mData = data;
            CardView mCastCardView = (CardView) getActivity().findViewById(R.id.castMoviedCardView);

            mCastCardView.setVisibility(View.GONE);

            if (data.getCount() != 0) {
                mCastGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        mData.moveToPosition(i);

                        Intent intent = new Intent(getActivity(), PersonActivity.class)
                                .putExtra("PERSON_ID", mData.getLong(DetailActivityFragment.COL_CAST_ID))
                                .putExtra("PERSON_NAME", mData.getString(DetailActivityFragment.COL_CAST_NAME));
                        startActivityForResult(intent, 4);
                    }
                });

                // For performance reasons we use postInvalidate
                // postInvalidate re-draws the view
                mCastGridView.postInvalidate();
                mCastCardView.postInvalidate();
                mCastCardView.setVisibility(View.VISIBLE);
            }

            mViewCastButton.setVisibility(View.VISIBLE);
            mViewCastButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), CastActivity.class)
                            .putExtra("MOVIE_ID",mMovieId)
                            .putExtra("MOVIE_TITLE", mMovieTitle)
                            .putExtra("MOVIE_OVERVIEW",mMovieOverview)
                            .putExtra("MOVIE_BACKDROP",mMovieBackdrop)
                            .putExtra("MOVIE_VOTE",mMovieVote);
                    startActivityForResult(intent, 2);

                }
            });
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mCastMiniAdapter.swapCursor(null);
        }
    };

    public void reloadLoaders() {
        try {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
            getLoaderManager().restartLoader(TRAILER_LOADER, null, trailerLoaderListener);
            getLoaderManager().restartLoader(GENRE_LOADER, null, genreLoaderListener);
            getLoaderManager().restartLoader(CAST_LOADER, null, castLoaderListener);
            getLoaderManager().restartLoader(SIMILAR_LOADER, null, similarLoaderListener);
        }
        catch (Exception ex){}
    }

    private Intent createShareMovieIntent() {
        // Share movie
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, I want you to check this out "
                + mMovieTitle
                + MOVIE_SHARE_HASHTAG
                + " for movies on my Android" );
        return shareIntent;
    }

}
