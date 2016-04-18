package com.serigon.moviezy.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.serigon.movietrend.R;
import com.serigon.moviezy.adapter.CastAdapter;
import com.serigon.moviezy.data.MovieContract;
import com.serigon.moviezy.utility.GridAutofitLayoutManager;
import com.serigon.moviezy.utility.RecyclerItemClickListener;
import com.serigon.moviezy.utility.VerticalSpaceItemDecoration;

public class CastActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CAST_LOADER = 0;
    int gridColumnWidthDp = 150;

    private Uri mCastUri;
    private String mMovieTitle;
    private Long mMovieId;
    private RecyclerView mCastRecyclerView;
    private CastAdapter mCastRecyclerViewAdapter;

    private static final String[] CAST_COLUMNS = {
            MovieContract.CastEntry._ID,
            MovieContract.CastEntry.COLUMN_MOVIE_KEY,
            MovieContract.CastEntry.COLUMN_CAST_ID,
            MovieContract.CastEntry.COLUMN_CAST_NAME,
            MovieContract.CastEntry.COLUMN_CAST_CHARACTER,
            MovieContract.CastEntry.COLUMN_CAST_PROFILE_PATH,
    };

    //public static final int COL_ID = 0;
    public static final int COL_CAST_MOVIE_KEY = 1;
    public static final int COL_CAST_ID = 2;
    public static final int COL_CAST_NAME = 3;
    public static final int COL_CAST_CHARACTER = 4;
    public static final int COL_CAST_PROFILE_PATH = 5;

    public CastActivityFragment() {  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cast, container, false);

        mMovieId = getActivity().getIntent().getExtras().getLong("MOVIE_ID");
        mMovieTitle = getActivity().getIntent().getExtras().getString("MOVIE_TITLE");

        // Build the URI to fetch the casts for specific movie
        // For Example, if movieId = 12345
        // mCastUri = com.serigon.moviezy/casts/12345
        mCastUri = MovieContract.CastEntry.buildCastUriForMovieId(mMovieId);

        // Start Loader
        getLoaderManager().initLoader(CAST_LOADER, null, this);

        mCastRecyclerView = (RecyclerView) rootView.findViewById(R.id.castRecyclerView);
        mCastRecyclerViewAdapter = new CastAdapter(getActivity(), null);
        mCastRecyclerView.setAdapter(mCastRecyclerViewAdapter);
        mCastRecyclerView.setHasFixedSize(false);

        GridLayoutManager mCastGridLayoutManager = new GridAutofitLayoutManager(getContext(),gridColumnWidthDp);

        //add ItemDecoration
        mCastRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(2));
        mCastRecyclerView.setLayoutManager(mCastGridLayoutManager);

        setupToolbar(rootView);
        
        return rootView;
    }

    private void setupToolbar(View rootView) {
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.my_toolbar);
        try {
            ((CastActivity) getActivity()).setSupportActionBar(toolbar);
            ((CastActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((CastActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((CastActivity) getActivity()).getSupportActionBar().setTitle("Casts of "+mMovieTitle);
        } catch (Exception e) {}
    }

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
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        mCastRecyclerViewAdapter.swapCursor(data);

        mCastRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Cursor mCursor  = data;
                        mCursor.moveToPosition(position);

                        Intent intent = new Intent(getActivity(), PersonActivity.class)
                                .putExtra("PERSON_ID", mCursor.getLong(CastActivityFragment.COL_CAST_ID))
                                .putExtra("PERSON_NAME", mCursor.getString(CastActivityFragment.COL_CAST_NAME));
                        startActivityForResult(intent,0);

                    }
                })
        );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCastRecyclerViewAdapter.swapCursor(null);
    }
}
