package com.serigon.moviezy.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.serigon.moviezy.utility.EndlessRecyclerOnScrollListener;
import com.serigon.moviezy.utility.GridAutofitLayoutManager;


/**
 * A placeholder menu_detailfragment containing a simple view.
 */

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieFragment mMoviefragment = this;
    private static final String BUNDLE_RECYCLER_LAYOUT = "MainActivity.recycler.layout";

    private static final String sortBy = "popularity.desc";
    private static final String SELECTED_KEY = "selected_position";

    private int pageNumber = 1;
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private int mPosition = GridView.INVALID_POSITION;

    private MovieAdapter mMovieAdapter;
    private GridAutofitLayoutManager mMovieGridLayoutManager;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mMovie.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null)
        {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mMovie.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

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
        mMovieGridLayoutManager = new GridAutofitLayoutManager(getContext(), 200);
        mMovie.setLayoutManager(mMovieGridLayoutManager);


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

        mMovie.addOnScrollListener(new EndlessRecyclerOnScrollListener(mMovieGridLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                // do something...

                //totalItemCount;
                Log.e("Page", "" + current_page);

                pageNumber = current_page;

                FetchMovieTask movieTask = new FetchMovieTask(getActivity(), mMovieAdapter, sortBy, current_page, mMoviefragment);
                movieTask.execute();
            }
        });
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
