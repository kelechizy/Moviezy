package com.serigon.moviezy.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Kelechi on 12/5/2015.
 */
public class StringFormatter {

    public static String formatDate(String dateString) {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;

        try {
            newDate = myFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        myFormat = new SimpleDateFormat("dd MMM yyyy");
        String date = myFormat.format(newDate);


        return date;
    }


    public static String getLatestReleaseDate() {
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        c.add(Calendar.MONTH, 1);

        String dateString = year + "-" + month + "-" + day;
        String lateDateString = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + day;

        String date = "primary_release_date.gte=" + dateString + "&primary_release_date.lte=" + lateDateString;


        return date;
    }
}
