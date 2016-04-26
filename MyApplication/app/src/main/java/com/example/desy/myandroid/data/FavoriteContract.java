package com.example.desy.myandroid.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by desy on 11/4/15.
 */
public class FavoriteContract  {
    public static final String CONTENT_AUTHORITY = "com.example.desy.myandroid";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_FAVORITE = "favorite";

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }


    /* Inner class that defines the table contents of the location table */
    public static final class FavoirteEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        // Table name
        public static final String TABLE_NAME = "favorite";

        // favorites id
        public static final String COLUMN_UNIQUE_ID = "uniqueID";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";
        // favorites title
        public static final String COLUMN_TITLE = "title";
        // Short description and long description of the favorites
        public static final String COLUMN_DESC = "description";
        //favorites category
        public static final String COLUMN_CATEGORY = "category";
        // favorites source
        public static final String COLUMN_SOURCE = "source";
        // favorites image
        public static final String COLUMN_IMAGE = "imageUrl";
        // favorites url
        public static final String COLUMN_URL = "url";
        // favorites price
        public static final String COLUMN_PRICE = "price";

        public static Uri buildFavoriteUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
