package com.serigon.moviezy.transformer;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.serigon.movietrend.R;

/**
 * Created by Kelechi on 3/15/2016.
 */

public class ZoomOutPageTransformer2 implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        View title = view.findViewById(R.id.title);
        ImageView image1 = (ImageView) view.getRootView().findViewById(R.id.image1);
        ImageView image2 = (ImageView) view.getRootView().findViewById(R.id.image2);
        ImageView image3 = (ImageView) view.getRootView().findViewById(R.id.image3);
        ImageView image4 = (ImageView) view.getRootView().findViewById(R.id.image4);
        ImageView image5 = (ImageView) view.getRootView().findViewById(R.id.image5);
        ImageView image6 = (ImageView) view.getRootView().findViewById(R.id.image6);
        ImageView image7 = (ImageView) view.getRootView().findViewById(R.id.image7);
        ImageView image8 = (ImageView) view.getRootView().findViewById(R.id.image8);
        ImageView backImage = (ImageView) view.getRootView().findViewById(R.id.backImageView);




        float absPosition = Math.abs(position);

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));


            image1.setTranslationX(-position * (pageWidth / 4)); //Half the normal speed
            image2.setTranslationX(position * (pageWidth / 4));
            image3.setTranslationX(absPosition * (pageWidth / 2f));
            image4.setTranslationX(-position * (pageWidth / 2));


            // Let's start by animating the title.
            // We want it to fade as it scrolls out

            title.setAlpha(1.0f - absPosition);

            backImage.setTranslationX(position * (pageWidth / 2));

            image8.setTranslationY(-position * (pageHeight / 4)); //Half the normal speed
            image6.setTranslationY(position * (pageHeight / 4));
            image5.setTranslationY(absPosition * (pageHeight / 2f));
            image7.setTranslationY(-position * (pageHeight / 2f)); //Half the normal speed





        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);

//
//            image1.setTranslationY(-position * (pageHeight / 4)); //Half the normal speed
//
//            image2.setTranslationY(position * (pageHeight /4));
//
//            image3.setTranslationY(absPosition*(pageHeight / 2f));
        }
    }
}