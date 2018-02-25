package com.example.xyzreader;

import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utilities {
    private static final String LOG_TAG = Utilities.class.getSimpleName();

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    private static SimpleDateFormat outputFormat = new SimpleDateFormat();
    private static GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);
    private static final String LINE_BREAK = "<br/>";
    private static final String PUBLISHED_BY = " by ";

    public static Date parsePublishedDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(LOG_TAG, ex.getMessage());
            Log.i(LOG_TAG, "passing today's date");
            return new Date();
        }
    }

    public static Spanned getSubtitleTextInHTML(String date, String author) {
        Date publishedDate = parsePublishedDate(date);

        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
            return Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + LINE_BREAK + PUBLISHED_BY + author);
        }else{
            return Html.fromHtml(
                    outputFormat.format(publishedDate)
                            + LINE_BREAK + PUBLISHED_BY + author);
        }
    }

    public static String getSubtitleText(String date, String author){
        Date publishedDate = parsePublishedDate(date);

        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
            return DateUtils.getRelativeTimeSpanString(
                    publishedDate.getTime(),
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString() + PUBLISHED_BY + author;
        }else{
            return outputFormat.format(publishedDate) + PUBLISHED_BY + author;
        }
    }
}
