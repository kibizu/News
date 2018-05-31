package com.example.android.news.entity;


import android.graphics.Bitmap;

public class New {

    private String mSectionName;
    private String mWebPubblicationDate;
    private String mWebTitle;
    private String mWebUrl;
    private String mTrialText;
    private String mAuthor;
    private String mThumbnailUrl;

    /**
     * Constructs a new {@link New} object
     *
     * @param sectionName is the section of the story or article
     * @param webPubblicationDate is the pubblication date
     * @param webTitle is the title of the story or article
     * @param webUrl is the url of the story or article
     * @param trialText is a short text of the story or article
     * @param thumbnailUrl is the web url for the thumbnail of the article
     */

    public New(String sectionName, String webPubblicationDate, String webTitle, String webUrl, String trialText, String thumbnailUrl) {
        mSectionName = sectionName;
        mWebPubblicationDate = webPubblicationDate;
        mWebTitle = webTitle;
        mWebUrl = webUrl;
        mTrialText = trialText;
        mThumbnailUrl = thumbnailUrl;
    }

    /**
     * Constructs a new {@link New} object
     *
     * @param sectionName is the section of the story or article
     * @param webPubblicationDate is the pubblication date
     * @param webTitle is the title of the story or article
     * @param webUrl is the url of the story or article
     * @param trialText is a short text of the story or article
     * @param author is the author of the story or article
     * @param thumbnailUrl is the web url for the thumbnail of the article
     */

    public New(String sectionName, String webPubblicationDate, String webTitle, String webUrl, String trialText, String author, String thumbnailUrl) {
        mSectionName = sectionName;
        mWebPubblicationDate = webPubblicationDate;
        mWebTitle = webTitle;
        mWebUrl = webUrl;
        mTrialText = trialText;
        mAuthor = author;
        mThumbnailUrl = thumbnailUrl;
    }

    public String getmSectionName() {

        return mSectionName;
    }

    public String getmWebPubblicationDate() {
        return mWebPubblicationDate;
    }

    public String getmWebTitle() {
        return mWebTitle;
    }

    public String getmWebUrl() {
        return mWebUrl;
    }

    public String getmTrialText() {
        return mTrialText;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public String getmThumbnailUrl() { return mThumbnailUrl; }
}
