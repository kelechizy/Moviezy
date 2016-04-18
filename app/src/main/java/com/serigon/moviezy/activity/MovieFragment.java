package com.serigon.moviezy.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TableLayout;

import com.serigon.movietrend.R;
import com.serigon.moviezy.adapter.MovieAdapter;
import com.serigon.moviezy.data.MovieContract;
import com.serigon.moviezy.data.MovieDbHelper;
import com.serigon.moviezy.task.FetchMovieTask;
import com.serigon.moviezy.utility.GridAutofitLayoutManager;


/**
 * A placeholder menu_detailfragment containing a simple view.
 */

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieFragment mMoviefragment = this;

    private static final String sortBy = "popularity.desc";
    private static final String SELECTED_KEY = "selected_position";

    private int pageNumber = 1;
    private int mPosition = GridView.INVALID_POSITION;

    private MovieAdapter mMovieAdapter;
    private TableLayout tableLayout;
    private GridView movieGridView;
    private RecyclerView mMovie;
    private SwipeRefreshLayout pullToRefresh;

    private static final int MOVIE_LOADER = 0;
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

    // These indices are tied to MOVIE_COLUMNS.  If MOVIE_COLUMNS changes, these
    // must change.
    public static final int COL_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_MOVIE_TITLE = 2;
    public static final int COL_MOVIE_OVERVIEW = 3;
    public static final int COL_MOVIE_DATE = 4;
    public static final int COL_MOVIE_POSTER = 5;
    public static final int COL_MOVIE_BACKDROP = 6;
    public static final int COL_MOVIE_VOTE = 7;
    public static final int COL_MOVIE_POPULARITY = 8;
    public static final int COL_MOVIE_FAVORITE = 9;

    public interface Callback {
        public void onItemSelected(Uri contentUri);
    }

    public MovieFragment() {setHasOptionsMenu(true);}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            refreshMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main2, container, false);

        mMovie = (RecyclerView) rootView.findViewById(R.id.movieRecyclerView);

        mMovieAdapter = new MovieAdapter(getActivity(),null);
        mMovie.setAdapter(mMovieAdapter);
        mMovie.setHasFixedSize(false);
        GridAutofitLayoutManager mMovieGridLayoutManager = new GridAutofitLayoutManager(getContext(), 200);
        mMovie.setLayoutManager(mMovieGridLayoutManager);


//        movieGridView = (GridView) rootView.findViewById(R.id.grid_movie);
//        mMovieAdapter = new MovieAdapter(getActivity());
//        movieGridView.setAdapter(mMovieAdapter);
//        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                //CursorAdapter returns a cursor at the correct position for getItem(), or null
//                //if it cannot seek to that position.
//
//                Cursor cursor = ((MovieAdapter) adapterView.getAdapter()).getCursor();
//                cursor.moveToPosition(position);
//
//                if (cursor != null) {
//                    ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieUri(
//                            cursor.getInt(COL_MOVIE_ID)));
//                }
//                mPosition = position;
//            }
//        });
//
//        movieGridView.setOnScrollListener(new EndlessScrollListener() {
//            @Override
//            public boolean onLoadMore(int page, int totalItemsCount) {
//                // Triggered only when new data needs to be appended to the list
//                // Add whatever code is needed to append new items to your AdapterView
//                totalItemsCount++;
//                page = 1 + (totalItemsCount / 20);
//
//                FetchMovieTask movieTask = new FetchMovieTask(getActivity(), mMovieAdapter, sortBy, page, mMoviefragment);
//                movieTask.execute();
//
//                return true; // ONLY if more data is actually being loaded; false otherwise.
//            }
//        });

        pullToRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                refreshMovies();
            }
        });

        // Fetch the detail of a certain Movie
        // We are using AyncTask to do that
        FetchMovieTask movieTask = new FetchMovieTask(getActivity(), mMovieAdapter, sortBy, pageNumber, this);
        movieTask.execute();

        // Start Loader
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);

        // On screen rotation we would want to restore the position on the Gridview list
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection = MovieContract.MovieEntry.TABLE_NAME +
                "." + MovieContract.MovieEntry.COLUMN_MOVIE_INCLUDE + " = ? ";
        String[] selectionArgs = new String[]{"yes"};

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                selection,
                selectionArgs,
                null
        );

    }

    //@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        mMovieAdapter.swapCursor(data);

//        mMovie.addOnItemTouchListener(
//                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        Cursor mCursor = data;
//                        mCursor.moveToPosition(position);
//
//                        Intent intent = new Intent(getActivity(), DetailActivity.class)
//                                .putExtra("MOVIE_TITLE", mCursor.getString(MovieFragment.COL_MOVIE_TITLE))
//                                .putExtra("MOVIE_ID", mCursor.getLong(MovieFragment.COL_MOVIE_ID));
//
//                        startActivity(intent);
//
//                    }
//                })
//        );
    }

    //@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }


    public void reloadLoader() {
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void refreshMovies() {
        MovieDbHelper myDB = new MovieDbHelper(getContext());
        myDB.refresh();

        FetchMovieTask movieTask = new FetchMovieTask(getActivity(), mMovieAdapter, sortBy, 1, this);
        movieTask.execute();
    }

    public void refreshCompleted(){
        pullToRefresh.setRefreshing(false);
    }

}
