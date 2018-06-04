package com.example.android.mynewsapp;

import android.graphics.Bitmap;
import java.util.Date;

public class Article {

    private String mTitle;
    private Date mPublished;
    private String mSection;
    private String mAuthor;
    private String mTrail;
    private Bitmap mThumbnail;
    private String mUrl;

    public Article(String mTitle, Date mPublished, String mSection, String mAuthor, String mTrail,
                   Bitmap mThumbnail, String mUrl) {
        this.mTitle = mTitle;
        this.mPublished = mPublished;
        this.mSection = mSection;
        this.mAuthor = mAuthor;
        this.mTrail = mTrail;
        this.mThumbnail = mThumbnail;
        this.mUrl = mUrl;
    }

    public String getmTitle() {
        return mTitle;
    }

    public Date getmPublished() {
        return mPublished;
    }

    public String getmSection() {
        return mSection;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmTrail() {
        if (mTrail.contains("<")) {
            String cleanedTrail = mTrail.replaceAll("<[^>]*>", "");
            return cleanedTrail;
        }
        return mTrail;
    }

    public Bitmap getmThumbnail() {
        return mThumbnail;
    }

    public String getmUrl() {
        return mUrl;
    }
}