package com.serigon.moviezy.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.serigon.movietrend.R;
import com.serigon.moviezy.adapter.CreditAdapter;
import com.serigon.moviezy.data.MovieContract;
import com.serigon.moviezy.task.FetchPersonTask;
import com.serigon.moviezy.utility.RecyclerItemClickListener;
import com.serigon.moviezy.utility.StringFormatter;


public class PersonActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String PERSON_URI = "URI";

    private static final String PERSON_SHARE_HASHTAG = "#Moviezy";
    private String personStr;

    private static final int PERSON_LOADER = 0;
    private static final int CREDIT_LOADER = 1;

    private static final String[] PERSON_COLUMNS = {
            MovieContract.PersonEntry._ID,
            MovieContract.PersonEntry.COLUMN_PERSON_ID,
            MovieContract.PersonEntry.COLUMN_PERSON_NAME,
            MovieContract.PersonEntry.COLUMN_PERSON_BIOGRAPHY,
            MovieContract.PersonEntry.COLUMN_PERSON_GENDER,
            MovieContract.PersonEntry.COLUMN_PERSON_BIRTHDAY,
            MovieContract.PersonEntry.COLUMN_PERSON_DEATHDAY,
            MovieContract.PersonEntry.COLUMN_PERSON_PLACE_OF_BIRTH,
            MovieContract.PersonEntry.COLUMN_PERSON_POPULARITY,
            MovieContract.PersonEntry.COLUMN_PERSON_PROFILE_PATH,
    };

    private static final String[] CREDIT_COLUMNS = {
            MovieContract.CreditEntry._ID,
            MovieContract.CreditEntry.COLUMN_PERSON_KEY,
            MovieContract.CreditEntry.COLUMN_MOVIE_KEY,
            MovieContract.CreditEntry.COLUMN_CREDIT_ID,
            MovieContract.CreditEntry.COLUMN_CREDIT_CHARACTER,
            MovieContract.CreditEntry.COLUMN_CREDIT_TITLE,
            MovieContract.CreditEntry.COLUMN_CREDIT_POSTER_PATH,
    };


    //static final int COL_ID = 0;
    public static final int COL_PERSON_ID = 1;
    public static final int COL_PERSON_NAME= 2;
    public static final int COL_PERSON_BIOGRAPHY = 3;
    public static final int COL_PERSON_GENDER = 4;
    public static final int COL_PERSON_BIRTHDAY = 5;
    public static final int COL_PERSON_DEATHDAY = 6;
    public static final int COL_PERSON_PLACE_OF_BIRTH = 7;
    public static final int COL_PERSON_POPULARITY = 8;
    public static final int COL_PERSON_PROFILE_PATH = 9;


    public static final int COL_CREDIT_PERSON_KEY = 1;
    public static final int COL_CREDIT_MOVIE_KEY= 2;
    public static final int COL_CREDIT_ID = 3;
    public static final int COL_CREDIT_CHARACTER = 4;
    public static final int COL_CREDIT_TITLE = 5;
    public static final int COL_CREDIT_POSTER_PATH = 6;


    private Uri mUri;
    private Uri mCreditUri;
    private Long mPersonId;
    private String mPersonName;

    private TextView mName;
    private TextView mBirthDay;
    private TextView mPlaceOfBirth;
    private TextView mBiography;
    private ImageView mProfile;

    private RecyclerView mCredit;
    private CreditAdapter mCreditRecyclerViewAdapter;

    public PersonActivityFragment() { setHasOptionsMenu(true);}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_person, container, false);

        mPersonId = getActivity().getIntent().getExtras().getLong("PERSON_ID");
        mPersonName = getActivity().getIntent().getExtras().getString("PERSON_NAME");

        try {
            mUri = MovieContract.PersonEntry.buildPersonUri(mPersonId);
            mCreditUri = MovieContract.CreditEntry.buildCreditUriForPersonId(mPersonId);
        } catch (Exception e) { }


        mName = (TextView) rootView.findViewById(R.id.personName);
        mBirthDay = (TextView) rootView.findViewById(R.id.person_birthday);
        mPlaceOfBirth = (TextView) rootView.findViewById(R.id.person_placeofbirth);
        mBiography =(TextView) rootView.findViewById(R.id.person_biography);
        mProfile =(ImageView) rootView.findViewById(R.id.personProfile);
        mCredit = (RecyclerView) rootView.findViewById(R.id.creditRecyclerView);

        mCreditRecyclerViewAdapter = new CreditAdapter(getActivity(), null);
        mCredit.setHasFixedSize(false);

        LinearLayoutManager mCreditLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mCredit.setLayoutManager(mCreditLinearLayoutManager);
        mCredit.setAdapter(mCreditRecyclerViewAdapter);


        if (mPersonId != null) {
            //Start Loader to fetch Person Detail and Movie Credits
            getLoaderManager().initLoader(PERSON_LOADER, null, this);
            getLoaderManager().initLoader(CREDIT_LOADER, null, creditLoaderListener);

            FetchPersonTask personTask = new FetchPersonTask(getActivity(), mPersonId, this);
            personTask.execute();
        }

        setupToolbar(rootView);

        return rootView;
    }

    private void setupToolbar(View rootView) {
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.my_toolbar);
        try {
            ((PersonActivity) getActivity()).setSupportActionBar(toolbar);
            ((PersonActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((PersonActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((PersonActivity) getActivity()).getSupportActionBar().setTitle(mPersonName);
        } catch (Exception e) {}
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                mUri,
                PERSON_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String name = data.getString(COL_PERSON_NAME);
            String bio = data.getString(COL_PERSON_BIOGRAPHY);
            String profile = data.getString(COL_PERSON_PROFILE_PATH);
            String place_of_birth = data.getString(COL_PERSON_PLACE_OF_BIRTH);
            String birthday = data.getString(COL_PERSON_BIRTHDAY);

            try {
                birthday = StringFormatter.formatDate(birthday);
            }catch (Exception ex){}

            mName.setText(name);
            mBiography.setText(bio);
            mBirthDay.setText(birthday);
            mPlaceOfBirth.setText(place_of_birth);

            Glide.with(getContext())
                    .load(profile)
                    .placeholder(R.drawable.ic_perm_identity_white_48dp)
                    .error(R.drawable.ic_perm_identity_white_48dp)
                    .crossFade()
                    .into(mProfile);


        }

    }

    @Override
    public void onLoaderReset(Loader loader) {}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_personfragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share_person);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createSharePersonIntent());
        } else {
            Log.d("Sad", "Share Action Provider is null?");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            startActivity(createSharePersonIntent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // CREDIT LOADER
    private LoaderManager.LoaderCallbacks<Cursor> creditLoaderListener = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (null == mCreditUri) {
                return null;
            }

            return new CursorLoader(
                    getActivity(),
                    mCreditUri,
                    CREDIT_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCreditRecyclerViewAdapter.swapCursor(data);

            mCredit.addOnItemTouchListener(
                    new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Cursor mCursor = mCreditRecyclerViewAdapter.getCursor();
                            mCursor.moveToPosition(position);

                            Uri contentUri = MovieContract.MovieEntry.buildMovieUri(Long.parseLong(mCursor.getString(COL_CREDIT_MOVIE_KEY)));

                            Intent intent = new Intent(getActivity(), DetailActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    .setData(contentUri);
                            startActivity(intent);
                        }
                    })
            );

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mCreditRecyclerViewAdapter.swapCursor(null);
        }
    };

    public void reloadLoaders() {
        try {
            getLoaderManager().restartLoader(PERSON_LOADER, null, this);
            getLoaderManager().restartLoader(CREDIT_LOADER, null, creditLoaderListener);
        }catch (Exception ex){}
    }

    private Intent createSharePersonIntent() {
        // Share movie
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey, I want you to check this out "
                + mPersonName
                + PERSON_SHARE_HASHTAG
                + " Android"  );
        return shareIntent;
    }
}
