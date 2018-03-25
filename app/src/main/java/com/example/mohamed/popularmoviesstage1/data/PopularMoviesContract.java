package com.example.mohamed.popularmoviesstage1.data;


import android.net.Uri;
import android.provider.BaseColumns;


public final class PopularMoviesContract {
    private PopularMoviesContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.mohamed.popularmoviesstage1";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        static final String _ID = BaseColumns._ID;
        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_VOTE = "vote";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_IS_FAVORITE = "isFavorite";
        public static final String COLUMN_TRAILER = "trailer";

    }
}
