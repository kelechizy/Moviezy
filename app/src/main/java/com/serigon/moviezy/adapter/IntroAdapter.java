package com.serigon.moviezy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.serigon.moviezy.activity.IntroActivityFragment;

/**
 * Created by Kelechi on 3/15/2016.
 */

public class IntroAdapter extends FragmentPagerAdapter {

    public IntroAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return IntroActivityFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

}