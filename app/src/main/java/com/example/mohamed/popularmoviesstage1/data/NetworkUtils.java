package com.example.mohamed.popularmoviesstage1.data;


import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String URL_ADDRESS =
            "https://api.themoviedb.org/3/discover/movie?";
    private final static String SORT_BY = "sort_by";
    private final static String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final static String API_KEY_PARAM = "api_key";
    private final static String API_KEY = "922b501959e1248171a56ffca95efa0f";
    private final static String YOUTUBE_URL = "http://www.youtube.com/watch?v=";


    public static URL getUrl(String query) {

        Uri uri = Uri.parse(URL_ADDRESS).buildUpon()
                .appendQueryParameter(SORT_BY, query)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public static ArrayList<Movie> fetchDataFromUrl(URL url) {

        try {
            return extractMovieFromJson(getJsonFromUrl(url));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static String getJsonFromUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = null;
        InputStream stream = null;
        String jsonResponse = "";
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                stream = urlConnection.getInputStream();
                jsonResponse = readFromStream(stream);
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, "Failed to connect", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (stream != null) {
                stream.close();
            }
        }

        return jsonResponse;
    }


    private static String readFromStream(InputStream stream) {

        Scanner scanner = new Scanner(stream);
        scanner.useDelimiter("\\A");

        if (scanner.hasNext()) {
            return scanner.next();
        } else {
            return null;
        }
    }

    private static ArrayList<Movie> extractMovieFromJson(String jsonResponse) {

        ArrayList<Movie> movies = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray results = baseJsonResponse.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject currentMovie = results.getJSONObject(i);
                String mPoster = "https://image.tmdb.org/t/p/w500" + currentMovie.getString("poster_path");
                String mReleaseDate = currentMovie.getString("release_date");
                int mVot = currentMovie.getInt("vote_average");
                String mOriginalTitle = currentMovie.getString("original_title");
                String mOverview = currentMovie.getString("overview");
                int movieId = currentMovie.getInt("id");

                String trailer = null;
                try {
                    trailer = getTrailerUriFromId(movieId);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                movies.add(new Movie(mPoster, mReleaseDate, mVot, mOriginalTitle, mOverview, trailer));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies;
    }

    private static String getTrailerUriFromId(int movieId) throws IOException {
        URL url = buildMovieUri(movieId);
        return extractMovieVideoFromJson(getJsonFromUrl(url));

    }

    private static String extractMovieVideoFromJson(String jsonResponse) {
        JSONObject json;
        String key = "";
        try {
            json = new JSONObject(jsonResponse);
            JSONArray videos = json.getJSONArray("results");

            if (videos.length() == 0) {
                return null;
            }
            JSONObject currentVideo = videos.getJSONObject(0);
            key = currentVideo.getString("key");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return YOUTUBE_URL + key;
    }


    private static URL buildMovieUri(int movieId) {
        String path = String.format("%s/videos", movieId);
        Uri builtUri = Uri.parse(API_BASE_URL).buildUpon()
                .appendEncodedPath(path)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
