package com.example.mohamed.popularmoviesstage1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mohamed.popularmoviesstage1.data.PopularMoviesContract.MovieEntry;

public class PopularMoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PopularMovies.db";
    private static final int DATABASE_VERSION = 5;

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " integer primary key autoincrement, " +
                MovieEntry.COLUMN_POSTER + " text not null, " +
                MovieEntry.COLUMN_DATE + " text not null, " +
                MovieEntry.COLUMN_VOTE + " integer not null, " +
                MovieEntry.COLUMN_TITLE + " text not null unique, " +
                MovieEntry.COLUMN_OVERVIEW + " text not null unique, " +
                MovieEntry.COLUMN_IS_FAVORITE + " integer not null, " +
                MovieEntry.COLUMN_TRAILER + " text not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
