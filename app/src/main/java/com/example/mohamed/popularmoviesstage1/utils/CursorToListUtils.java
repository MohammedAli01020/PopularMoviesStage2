package com.example.mohamed.popularmoviesstage1.utils;

import android.database.Cursor;

import com.example.mohamed.popularmoviesstage1.data.Movie;
import com.example.mohamed.popularmoviesstage1.data.PopularMoviesContract;

import java.util.ArrayList;


public class CursorToListUtils {
    public static ArrayList<Movie> getList(Cursor cursor) {
        ArrayList<Movie> movies = new ArrayList<>();
        if (cursor == null) {
            return movies;
        }

        while (cursor.moveToNext()) {
            movies.add(new Movie(cursor.getString(cursor.getColumnIndexOrThrow(PopularMoviesContract.MovieEntry.COLUMN_POSTER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PopularMoviesContract.MovieEntry.COLUMN_DATE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(PopularMoviesContract.MovieEntry.COLUMN_VOTE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PopularMoviesContract.MovieEntry.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PopularMoviesContract.MovieEntry.COLUMN_OVERVIEW)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(PopularMoviesContract.MovieEntry.COLUMN_IS_FAVORITE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PopularMoviesContract.MovieEntry.COLUMN_TRAILER))));
        }

        cursor.close();
        return movies;
    }
}
