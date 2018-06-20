package com.example.android.theguardiannews;

import java.util.Date;

/**
 *  An {@Link Article} object contains data of a single article
 */

public class Article {
    private String mTitle;
    private String mSection;
    private String mAuthor;
    private Date mDate;
    private String mImageUrl;
    private String mArticleUrl;

    /**
     *
     * @param title         Tile of the article
     * @param section       Name of the section
     * @param author        Name(s) of the author(s)
     * @param date          Date of publication
     * @param imageUrl      Url of the image of the article
     * @param articleUrl    Url of the article
     */
    public Article(String title, String section, String author, Date date, String imageUrl, String articleUrl) {
        mTitle = title;
        mSection = section;
        mAuthor = author;
        mDate = date;
        mImageUrl = imageUrl;
        mArticleUrl = articleUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public Date getDate() {
        return mDate;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getArticleUrl() {
        return mArticleUrl;
    }
}


