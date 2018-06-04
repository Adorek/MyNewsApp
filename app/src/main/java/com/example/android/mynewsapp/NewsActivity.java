package com.example.android.mynewsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Article>> {

    /** URL for articles data from the Guardian API */
    private static final String NEWS_URL =
            "http://content.guardianapis.com/search?&order-by=newest&page-size=25&show-fields=headline,trailText,firstPublicationDate,shortUrl,thumbnail,byline&api-key=test";


    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /** Adapter for the list of articles */
    private ArticleAdapter mArticleAdapter;

    /**Show progress ring when the data is loading */
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mProgressBar = (ProgressBar) findViewById(R.id.loading);

        // Find a reference to the {@link ListView} in the layout
        ListView articlesListView = (ListView) findViewById(R.id.news_list);

        // Create a new adapter that takes an empty list of articles as input
        mArticleAdapter = new ArticleAdapter(this, new ArrayList<Article>());

        mEmptyStateTextView = (TextView) findViewById(R.id.empty);
        articlesListView.setEmptyView(mEmptyStateTextView);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        articlesListView.setAdapter(mArticleAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a Guardian website of given article.
        articlesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current article that was clicked on
                Article currentArticle = mArticleAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentArticle.getmUrl());
                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        LoaderManager loaderManager = getLoaderManager();

        // If there is a network connection, fetch data
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            loaderManager.initLoader(1, null, this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new ArticleLoader(this, NEWS_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        // Hide loading indicator
        mProgressBar.setVisibility(View.GONE);
        // Set empty state text to display "No articles found."
        mEmptyStateTextView.setText(R.string.no_articles_found);
        // Clear the adapter of previous articles
        mArticleAdapter.clear();
        if (articles != null && !articles.isEmpty()) {
            mArticleAdapter.addAll(articles);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        // Loader reset, so we can clear out our existing data.
        mArticleAdapter.clear();
    }
}
