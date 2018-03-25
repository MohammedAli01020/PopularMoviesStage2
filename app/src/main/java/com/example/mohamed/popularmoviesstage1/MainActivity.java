package com.example.mohamed.popularmoviesstage1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.mohamed.popularmoviesstage1.data.Movie;
import com.example.mohamed.popularmoviesstage1.data.NetworkUtils;
import com.example.mohamed.popularmoviesstage1.data.PopularMoviesContract.MovieEntry;
import com.example.mohamed.popularmoviesstage1.databinding.ActivityMainBinding;
import com.example.mohamed.popularmoviesstage1.utils.CursorToListUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MOVIES_LOADER_ID = 1;
    private static final String QUERY_MOST_POPULAR = "popularity.desc";
    private static final String QUERY_MOST_VOTED = "vote_average.desc";
    public static ArrayList<Movie> sMovies;
    private MovieAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private URL mUrl;
    private ActivityMainBinding mActivityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mLayoutManager = new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false);

        mActivityMainBinding.recyclerView.setHasFixedSize(true);

        mActivityMainBinding.recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MovieAdapter();

        mActivityMainBinding.recyclerView.setAdapter(mAdapter);

        setupPreferences();
    }


    private void setupPreferences() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        mostPopular(preferences.getBoolean(getString(R.string.pref_most_popular_key),
                getResources().getBoolean(R.bool.pref_most_popular_default)));

        mostVoted(preferences.getBoolean(getString(R.string.pref_most_voted_key),
                getResources().getBoolean(R.bool.pref_most_voted_default)));

        liked(preferences.getBoolean(getString(R.string.pref_liked_key),
                getResources().getBoolean(R.bool.pref_liked_default)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {
            private ArrayList<Movie> mMovies;

            @Override
            protected void onStartLoading() {
                if (mMovies != null) {
                    mActivityMainBinding.pbProgress.setVisibility(View.VISIBLE);
                    deliverResult(mMovies);
                } else {
                    forceLoad();
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public ArrayList<Movie> loadInBackground() {
                if (args == null) {
                    return NetworkUtils.fetchDataFromUrl(mUrl);

                } else {
                    Cursor cursor = getContentResolver().query(MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null,
                            null);

                    return CursorToListUtils.getList(cursor);

                }
            }

            @Override
            public void deliverResult(ArrayList<Movie> data) {
                mMovies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        mActivityMainBinding.pbProgress.setVisibility(View.INVISIBLE);
        if (movies.size() == 0) {
            mActivityMainBinding.emptyList.setVisibility(View.VISIBLE);
        } else {
            mActivityMainBinding.emptyList.setVisibility(View.INVISIBLE);
            sMovies = movies;
            mAdapter.fillList(sMovies);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        mAdapter.fillList(null);
    }

    private void showList() {
        mActivityMainBinding.recyclerView.setVisibility(View.VISIBLE);
        mActivityMainBinding.connection.setVisibility(View.INVISIBLE);
    }

    private void showConnection() {
        mActivityMainBinding.recyclerView.setVisibility(View.INVISIBLE);
        mActivityMainBinding.connection.setVisibility(View.VISIBLE);
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.pref_most_popular_key))) {
            mostPopular(sharedPreferences.getBoolean(getString(R.string.pref_most_popular_key),
                    getResources().getBoolean(R.bool.pref_most_popular_default)));

        } else if (s.equals(getString(R.string.pref_most_voted_key))) {
            mostVoted(sharedPreferences.getBoolean(getString(R.string.pref_most_voted_key),
                    getResources().getBoolean(R.bool.pref_most_voted_default)));
        } else if (s.equals(getString(R.string.pref_liked_key))) {
            liked(sharedPreferences.getBoolean(getString(R.string.pref_liked_key),
                    getResources().getBoolean(R.bool.pref_liked_default)));
        }
    }

    private void mostPopular(boolean b) {
        if (b) {
            if (isOnline()) {
                showList();
                mUrl = NetworkUtils.getUrl(QUERY_MOST_POPULAR);
                getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            } else {
                showConnection();
            }
        }
    }

    private void mostVoted(boolean b) {
        if (b) {
            if (isOnline()) {
                showList();
                mUrl = NetworkUtils.getUrl(QUERY_MOST_VOTED);
                getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            } else {
                showConnection();
            }
        }
    }

    private void liked(boolean b) {
        if (b) {
            if (isOnline()) {
                showList();
                Bundle bundle = new Bundle();
                getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, bundle, this);
            } else {
                showConnection();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
