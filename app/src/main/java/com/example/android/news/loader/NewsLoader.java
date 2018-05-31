package com.example.android.news.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.news.QueryUtils;
import com.example.android.news.entity.New;

import java.io.IOException;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<New>> {

    private String mUrl;

    private static final String LOG_TAG = NewsLoader.class.getSimpleName();

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<New> loadInBackground() {

        if (mUrl == null){

            return null;

        }

        List<New> news = null;

        try {
            news = QueryUtils.fetchNewData(mUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with fetch new data ", e);
        }

        return news;
    }
}
