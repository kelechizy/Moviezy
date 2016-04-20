package com.serigon.moviezy.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.serigon.movietrend.R;
import com.serigon.moviezy.adapter.IntroAdapter;
import com.serigon.moviezy.transformer.ZoomOutPageTransformer;

public class IntroActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ImageView mIndicator1;
    private ImageView mIndicator2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mIndicator1 = (ImageView) findViewById(R.id.indicator1);
        mIndicator2 = (ImageView) findViewById(R.id.indicator2);

        // Set an Adapter on the ViewPager
        mViewPager.setAdapter(new IntroAdapter(getSupportFragmentManager()));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {

                switch(position){
                    case 1:
                        mIndicator1.setImageResource(R.drawable.ic_indicator);
                        mIndicator2.setImageResource(R.drawable.ic_indicator_selected);
                        break;
                    default:
                        mIndicator1.setImageResource(R.drawable.ic_indicator_selected);
                        mIndicator2.setImageResource(R.drawable.ic_indicator);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // Set a PageTransformer
        mViewPager.setPageTransformer(false, new ZoomOutPageTransformer());

        Button mContinueButton = (Button) findViewById(R.id.continueButton);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intro, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
