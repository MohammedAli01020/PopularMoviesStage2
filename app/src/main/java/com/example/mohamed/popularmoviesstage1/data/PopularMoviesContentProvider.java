package com.example.mohamed.popularmoviesstage1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.mohamed.popularmoviesstage1.data.PopularMoviesContract.MovieEntry;


public class PopularMoviesContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    private static UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY, PopularMoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(PopularMoviesContract.CONTENT_AUTHORITY, PopularMoviesContract.PATH_MOVIES + "/*", MOVIE_WITH_ID);

        return uriMatcher;
    }


    private PopularMoviesDbHelper mMoviesDbHelper;
    @Override
    public boolean onCreate() {
        mMoviesDbHelper = new PopularMoviesDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        final SQLiteDatabase db = mMoviesDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor cursor;

        switch (match) {
            case MOVIES:
                cursor = db.query(MovieEntry.TABLE_NAME,
                        strings,
                        s,
                        strings1,
                        null,
                        null,
                        s1);
                break;

            case MOVIE_WITH_ID:
                String title = uri.getLastPathSegment();

                cursor = db.query(MovieEntry.TABLE_NAME,
                        strings,
                        MovieEntry.COLUMN_TITLE + "=?",
                        new String[] {title},
                        null,
                        null,
                        s1);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri retUri;
        switch (match) {
            case MOVIES: {
                long id = db.insert(MovieEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    retUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        getContext().getContentResolver().notifyChange(uri, null);

        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        int retId;
        switch (match) {
            case MOVIE_WITH_ID: {
                String title = uri.getLastPathSegment();
                retId = db.delete(MovieEntry.TABLE_NAME,
                        MovieEntry.COLUMN_TITLE + "=?", new String[] {title});
                break;
            }

            case MOVIES: {
                retId = db.delete(MovieEntry.TABLE_NAME, null, null);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (retId <= 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return retId;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        if (mMoviesDbHelper != null) {
            mMoviesDbHelper.close();
        }
    }
}
