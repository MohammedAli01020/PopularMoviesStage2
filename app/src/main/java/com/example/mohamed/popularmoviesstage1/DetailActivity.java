package com.example.mohamed.popularmoviesstage1;


import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.mohamed.popularmoviesstage1.data.Movie;
import com.example.mohamed.popularmoviesstage1.data.PopularMoviesContract.MovieEntry;
import com.example.mohamed.popularmoviesstage1.databinding.ActivityDetailBinding;
import com.example.mohamed.popularmoviesstage1.sync.FavoriteIntentService;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private boolean isClicked;
    private String poster;
    private String date;
    private String trailerUri;
    private int vote;
    private String title ;
    private String overview;
    private Movie movie;
    private int mIndex;

    private ActivityDetailBinding mActivityDetailBinding;;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mActivityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent intent = getIntent();
        mIndex = intent.getIntExtra("index", -1);

        if (mIndex != -1) {
            updateUi(mIndex);
        }

        setupPreferences();

        mActivityDetailBinding.imgBtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClicked) {
                    mActivityDetailBinding.imgBtnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
                    movie.setmIsFavorite(0);
                    isClicked = (movie.getmIsFavorite() == 1);
                    Intent intent = new Intent(DetailActivity.this, FavoriteIntentService.class);
                    intent.setAction(FavoriteIntentService.ACTION_DELETE_FAV);
                    intent.putExtra("title", title);
                    startService(intent);

                } else {
                    mActivityDetailBinding.imgBtnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                    movie.setmIsFavorite(1);
                    isClicked = (movie.getmIsFavorite() == 1);
                    Intent intent = new Intent(DetailActivity.this, FavoriteIntentService.class);
                    intent.setAction(FavoriteIntentService.ACTION_ADD_FAV);
                    intent.putExtra("index", mIndex);
                    startService(intent);
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void updateUi(int mIndex) {
        movie = MainActivity.sMovies.get(mIndex);
        trailerUri = movie.getmTrailer();
        poster = movie.getmPoster();
        date = movie.getmReleaseDate();
        vote = movie.getmVote();
        title = movie.getmOriginalTitle();
        overview = movie.getmOverview();
        isClicked = isClickedAsFavorite();
        Picasso.with(this).load(Uri.parse(poster)).into(mActivityDetailBinding.poster);
        mActivityDetailBinding.releaseDate.setText(date);
        mActivityDetailBinding.voteAverage.setText(getString(R.string.vote) + String.valueOf(vote));
        mActivityDetailBinding.originalTitle.setText(title);
        mActivityDetailBinding.overview.setText(overview);
    }

    private void setupPreferences() {
        if (isClicked) {
            updateStarButton(1);
        } else {
            updateStarButton(0);
        }
    }


    private void updateStarButton(int aBoolean) {
        if (aBoolean == 1) {
            mActivityDetailBinding.imgBtnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            mActivityDetailBinding.imgBtnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean isClickedAsFavorite() {
        Uri uri = MovieEntry.CONTENT_URI.buildUpon()
                .appendEncodedPath(title)
                .build();

        Cursor cursor = getContentResolver().query(uri,
                new String[]{MovieEntry.COLUMN_IS_FAVORITE},
                null,
                null,
                null,
                null);

        if (cursor != null && !cursor.moveToFirst()) return false;
        assert cursor != null;
        boolean isChecked = cursor.getInt(cursor.getColumnIndexOrThrow(MovieEntry.COLUMN_IS_FAVORITE)) == 1;

        cursor.close();
        return isChecked;
    }


    public void startTrailer(View view) {
        if (trailerUri.length() != 0) {
            Uri uri = Uri.parse(trailerUri);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }
}
