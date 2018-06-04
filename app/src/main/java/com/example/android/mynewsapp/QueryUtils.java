package com.example.android.mynewsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static HttpURLConnection urlConnection = null;
    private static InputStream inputStream = null;

    private QueryUtils() {
    }
    /**
     * Query the Guardian API and return a list of {@link Article} objects.
     */
    public static List<Article> fetchArticlesFromServer(String apiUrl) {
        // Create URL object
        URL url = createUrl(apiUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Problem making the HTTP request", exception);
        }
        // Extract relevant fields from the JSON response and create a list of {@link Article}s
        List<Article> articles = extractArticlesFromJson(jsonResponse);
        // Return the list of {@link Articles}s
        return articles;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String inputUrl) {
        URL url = null;
        try {
            url = new URL(inputUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error building the URL");
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error, response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Can't connect to Guardian API", exception);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader
                    (inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Article> extractArticlesFromJson(String jsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding articles to
        List<Article> articles = new ArrayList<>();

        try {
            // Create a JSONObject from the JSON response string
            JSONObject jsonResponseObject = new JSONObject(jsonResponse);
            JSONObject jsonResults = jsonResponseObject.getJSONObject("response");
            JSONArray responseArray = jsonResults.getJSONArray("results");
            for (int i = 0; i < responseArray.length(); i++) {
                JSONObject article = responseArray.getJSONObject(i);
                String section = article.getString("sectionName");
                JSONObject fields = article.getJSONObject("fields");
                String author = fields.getString("byline");
                String title = fields.getString("headline");
                String trail = fields.getString("trailText");
                String published = fields.getString("firstPublicationDate");
                String url = fields.getString("shortUrl");
                Bitmap thumbnail = null;
                try {
                    thumbnail = getThumbnail(fields.getString("thumbnail"));
                } catch (IOException exception) {
                    Log.e(LOG_TAG, "Error getting thumbnail", exception);
                }
                articles.add(new Article(title, getDateFromString(published), section, author, trail, thumbnail, url));
            }
        } catch (JSONException exception) {
            Log.e(LOG_TAG, "Problem with parsing JSON", exception);
        }
        // Return the list of articles
        return articles;
    }

    private static Date getDateFromString(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Date date = null;
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private static Bitmap getThumbnail(String downloadUrl) throws IOException {
        Bitmap thumbnail = null;
        if (downloadUrl == null) {
            return thumbnail;
        }
        try {
            URL url = createUrl(downloadUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            thumbnail = BitmapFactory.decodeStream(inputStream);
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Error downloading thumbnail", exception);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return thumbnail;
    }
}