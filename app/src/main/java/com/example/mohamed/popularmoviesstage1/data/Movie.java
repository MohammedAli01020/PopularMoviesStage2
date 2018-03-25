package com.example.mohamed.popularmoviesstage1.data;

public class Movie {
    private String mPoster;
    private String mReleaseDate;
    private int mVote;
    private String mOriginalTitle;
    private String mOverview;
    private int mIsFavorite;
    private String mTrailer;

    public Movie(String mPoster, String mReleaseDate, int mVote, String mOriginalTitle, String mOverview, int mIsFavorite, String mTrailer) {
        this.mPoster = mPoster;
        this.mReleaseDate = mReleaseDate;
        this.mVote = mVote;
        this.mOriginalTitle = mOriginalTitle;
        this.mOverview = mOverview;
        this.mIsFavorite = mIsFavorite;
        this.mTrailer = mTrailer;
    }

    public Movie(String mPoster, String mReleaseDate, int mVote, String mOriginalTitle, String mOverview, String mTrailer) {
        this.mPoster = mPoster;
        this.mReleaseDate = mReleaseDate;
        this.mVote = mVote;
        this.mOriginalTitle = mOriginalTitle;
        this.mOverview = mOverview;
        this.mTrailer = mTrailer;
    }

    public String getmPoster() {
        return mPoster;
    }

    public String getmReleaseDate() {
        return mReleaseDate;
    }

    public int getmVote() {
        return mVote;
    }

    public String getmOriginalTitle() {
        return mOriginalTitle;
    }

    public String getmOverview() {
        return mOverview;
    }

    public int getmIsFavorite() {
        return mIsFavorite;
    }

    public void setmIsFavorite(int mIsFavorite) {
        this.mIsFavorite = mIsFavorite;
    }

    public String getmTrailer() {
        return mTrailer;
    }
}
