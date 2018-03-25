package com.example.mohamed.popularmoviesstage1.sync;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.mohamed.popularmoviesstage1.MainActivity;
import com.example.mohamed.popularmoviesstage1.data.Movie;
import com.example.mohamed.popularmoviesstage1.data.PopularMoviesContract.MovieEntry;
import com.example.mohamed.popularmoviesstage1.data.PopularMoviesDbHelper;


public class FavoriteIntentService extends IntentService {
    public static final String ACTION_ADD_FAV = "action-add-fav";
    public static final String ACTION_DELETE_FAV = "action-delete-fav";

    public FavoriteIntentService() {
        super("FavoriteIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String action = intent.getAction();

        if (ACTION_ADD_FAV.equals(action)) {
            addFavorite(intent);
        } else if (ACTION_DELETE_FAV.equals(action)) {
            deleteFavorite(intent);
        }

    }

    private void deleteFavorite(Intent intent) {
        String title = intent.getStringExtra("title");
        Uri uri = MovieEntry.CONTENT_URI.buildUpon()
                .appendEncodedPath(title)
                .build();

        getContentResolver().delete(uri, null, null);
    }

    synchronized private void addFavorite(Intent intent) {

        int index = intent.getIntExtra("index", -1);
        Movie movie = MainActivity.sMovies.get(index);

        ContentValues values = new ContentValues();

        values.put(MovieEntry.COLUMN_POSTER, movie.getmPoster());
        values.put(MovieEntry.COLUMN_DATE, movie.getmReleaseDate());
        values.put(MovieEntry.COLUMN_VOTE, movie.getmVote());
        values.put(MovieEntry.COLUMN_TITLE, movie.getmOriginalTitle());
        values.put(MovieEntry.COLUMN_OVERVIEW, movie.getmOverview());
        values.put(MovieEntry.COLUMN_IS_FAVORITE, 1);
        values.put(MovieEntry.COLUMN_TRAILER, movie.getmTrailer());

        getContentResolver().insert(MovieEntry.CONTENT_URI, values);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
