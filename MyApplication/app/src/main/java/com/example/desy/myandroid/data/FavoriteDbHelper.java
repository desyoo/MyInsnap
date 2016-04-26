package com.example.desy.myandroid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.desy.myandroid.data.FavoriteContract.FavoirteEntry;

/**
 * Created by desy on 11/4/15.
 */
public class FavoriteDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "favorite.db";

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoirteEntry.TABLE_NAME + " (" +
                FavoirteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavoirteEntry.COLUMN_UNIQUE_ID + " TEXT NOT NULL, " +
                FavoirteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoirteEntry.COLUMN_DATE + " TEXT, " +
                FavoirteEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                FavoirteEntry.COLUMN_IMAGE + " TEXT NOT NULL, " +
                FavoirteEntry.COLUMN_DESC + " TEXT, " +
                FavoirteEntry.COLUMN_SOURCE + " TEXT, " +
                FavoirteEntry.COLUMN_PRICE + " TEXT, " +
                FavoirteEntry.COLUMN_URL + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoirteEntry.TABLE_NAME);
        onCreate(db);
    }
}
