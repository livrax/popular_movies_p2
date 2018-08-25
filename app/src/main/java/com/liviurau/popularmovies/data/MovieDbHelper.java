package com.liviurau.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Liviu Rau on 05-Mar-18.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry.COLUMN_ID + " REAL, " +
                MovieContract.MovieEntry.COLUMN_NAME + " REAL, " +
                MovieContract.MovieEntry.COLUMN_TAGLINE + " REAL, " +
                MovieContract.MovieEntry.COLUMN_POSTER + " REAL, " +
                MovieContract.MovieEntry.COLUMN_BACKDROP + " REAL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " REAL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE + " REAL, " +
                MovieContract.MovieEntry.COLUMN_RUNTIME + " REAL, " +
                MovieContract.MovieEntry.COLUMN_BUDGET + " REAL, " +
                MovieContract.MovieEntry.COLUMN_REVENUE + " REAL" + ");";
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
