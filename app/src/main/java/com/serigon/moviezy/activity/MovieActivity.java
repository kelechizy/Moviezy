package com.serigon.moviezy.activity;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.serigon.movietrend.R;

public class MovieActivity extends AppCompatActivity implements MovieFragment.Callback {

    private boolean mTwoPane;
    private String DETAIL_ACTIVITY_FRAGMENT_TAG = "DAFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayIntro();

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_fragment_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_fragment_container, new DetailActivityFragment())
                        .commit();
            }

            int col = Integer.parseInt(String.valueOf(R.string.my_gridview_column));

//            GridView myGridView = (GridView) findViewById(R.id.grid_movie);
//            myGridView.setNumColumns(col);
        } else {
            mTwoPane = false;
        }

        setupToolbar();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rate) {
            Context context = getApplicationContext();

            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            try { startActivity(goToMarket); }
            catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, contentUri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_fragment_container, fragment, DETAIL_ACTIVITY_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }


    private void displayIntro() {
        // Display an app Intro page
        // When the app version code changes

        SharedPreferences sharedPreferences = getSharedPreferences("version", 0);
        int savedVersionCode = sharedPreferences.getInt("VersionCode", 0);
        int appVersionCode;

        try {
            appVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;

            if(savedVersionCode != appVersionCode){
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putInt("VersionCode", appVersionCode);
                sharedPreferencesEditor.commit();

                Intent intent = new Intent( getApplicationContext(),IntroActivity.class );
                startActivity(intent);
            }
        }
        catch (Exception e) {}
    }


    private void setupToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        try {
            setSupportActionBar(myToolbar);
            getSupportActionBar().setTitle("  Moviezy");
            getSupportActionBar().setIcon(R.drawable.ic_movie_white_24dp);
        }
        catch (Exception e) { }
    }

}
