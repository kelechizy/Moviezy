package com.serigon.moviezy.transformer;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Kelechi on 3/15/2016.
 */
public class  ParallaxPageTransformer implements ViewPager.PageTransformer {

    public void transformPage(View view, float position) {

        int pageWidth = view.getWidth();


        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(1);

        } else if (position <= 1) { // [-1,1]

//            dummyImageView.setTranslationX(-position * (pageWidth / 2)); //Half the normal speed

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(1);
        }


    }
}