package com.example.android.news.activity;

import android.content.Loader;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.news.entity.New;
import com.example.android.news.adapter.NewsAdapter;
import com.example.android.news.loader.NewsLoader;
import com.example.android.news.R;
import com.example.android.news.RecyclerViewEmptySupport;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<New>> {

    /**
     * Constant value for the new loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEW_LOADER_ID = 1;

    private static final String LOG_TAG = NewsActivity.class.getName();

    /** URL for new data from the The Guardian dataset */
    private static final String GUARDIAN_REQUEST_URL = "http://content.guardianapis.com/search";

    private TextView mEmptyStateTextView;

    private ProgressBar mProgressBarView;

    private ConnectivityManager cm;

    private NetworkInfo activeNetwork;

    private List<New> newsList;

    private boolean isConnected;

    private RecyclerViewEmptySupport newsRecyclerView = null;

    /** Adapter for the list of news */
    private NewsAdapter mAdapter = null;

    private Toolbar toolbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        newsRecyclerView = findViewById(R.id.list);

        mProgressBarView = findViewById(R.id.loading_spinner);

        mEmptyStateTextView =  findViewById(R.id.list_empty);

        newsList = new ArrayList<New>();

        mAdapter = new NewsAdapter(this, newsList);

        newsRecyclerView.setAdapter(mAdapter);

        int numOfColumns = calculateNoOfColumns(getApplicationContext());

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, numOfColumns);
        newsRecyclerView.setLayoutManager(mLayoutManager);
        newsRecyclerView.addItemDecoration(new GridSpacingItemDecoration(numOfColumns, dpToPx(0), true));
        newsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {

            mEmptyStateTextView.setVisibility(View.GONE);

            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEW_LOADER_ID, null, this);

        } else {

            newsRecyclerView.setEmptyView((TextView)findViewById(R.id.list_empty), getBaseContext().getResources().getString(R.string.no_connection));

            mProgressBarView.setVisibility(View.GONE);

        }

        mAdapter.setOnItemClickListener(new NewsAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {

                New currentNew = mAdapter.getItem(position);

                Uri newUri = Uri.parse(currentNew.getmWebUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);

            }

            @Override
            public void onItemLongClick(int position, View v) {

                New currentNew = mAdapter.getItem(position);

                Uri newUri = Uri.parse(currentNew.getmWebUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);

            }
        });

        initToolBar();
    }

    public void initToolBar(){

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setSubtitle(DateFormat.getDateInstance(DateFormat.FULL).format(new Date()));
        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleAppearance);
        setSupportActionBar(toolbar);

    }

    @Override
    public Loader<List<New>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String keyword = sharedPrefs.getString(getString(R.string.settings_search_key),"");
        String orderBy  = sharedPrefs.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));
        String pageSize = sharedPrefs.getString(getString(R.string.settings_page_size_key), getString(R.string.settings_page_size_default));
        String selectSection = sharedPrefs.getString(getString(R.string.settings_select_section_key),"");

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("show-tags","contributor");
        uriBuilder.appendQueryParameter("show-fields", "trailText,thumbnail");
        uriBuilder.appendQueryParameter("page-size","10");

        if(!keyword.isEmpty()) {
            uriBuilder.appendQueryParameter(getString(R.string.settings_search_key), keyword);
        }
        uriBuilder.appendQueryParameter(getString(R.string.settings_order_by_key),orderBy);
        uriBuilder.appendQueryParameter(getString(R.string.settings_page_size_key),pageSize);

        if(!selectSection.isEmpty()){

            uriBuilder.appendQueryParameter(getString(R.string.settings_select_section_key), selectSection);

        }

        // Create a new loader for the given URL
        return new NewsLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<New>> loader, List<New> news) {

        cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {

            newsRecyclerView.setEmptyView((TextView)findViewById(R.id.list_empty), getBaseContext().getResources().getString(R.string.no_data));

        } else {

            newsRecyclerView.setEmptyView((TextView)findViewById(R.id.list_empty), getBaseContext().getResources().getString(R.string.no_connection));

        }

        mProgressBarView.setVisibility(View.GONE);

        // Clear the adapter of previous new data
        newsList.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            newsList.addAll(news);
        }

        mAdapter = new NewsAdapter(this, newsList);
        newsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<New>> loader) {

        newsList.clear();

    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 150);
        return noOfColumns;
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
