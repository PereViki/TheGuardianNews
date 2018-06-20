package com.example.android.theguardiannews;

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
import java.util.Date;
import java.text.SimpleDateFormat;


/**
 * Helper methods related to requesting and receiving article data from The Guardian.
 */

public class QueryUtils {

    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Article> extractFeatureFromJson(String articleJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding articles to
        List<Article> articles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(articleJSON);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of articles.
            JSONArray articlesArray = baseJsonResponse.getJSONObject("response").getJSONArray("results");


            // For each article in the articlesArray, create an {@link Article} object
            for (int i = 0; i < articlesArray.length(); i++) {

                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentArticle = articlesArray.getJSONObject(i);

                // Extract the value for the key called "webTitle"
                String title = currentArticle.getString("webTitle");

                // Extract the value for the key called "sectionName"
                String section = currentArticle.getString("sectionName");

                ///////AUTHOR
                // For a given article, extract the JSONObject associated with the
                // key called "tags", which represents a list of authors
                String author = "";
                JSONArray authorArray = currentArticle.getJSONArray("tags");
                for (int j = 0; j < authorArray.length(); j++ ) {

                    // Extract the value for the key called "webTitle" associated with the author name
                    JSONObject currentTag = authorArray.getJSONObject(j);
                    if(author.equals("")) {
                        author = currentTag.getString("webTitle");
                    } else {
                        author = author + ", " + currentTag.getString("webTitle");
                    }
                }
                ///////AUTHOR END

                // Extract the value for the key called "webPublicationDate" for date
                String dateText = currentArticle.getString("webPublicationDate");

                //Format date
                Date date = null;
                try {
                    date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).parse(dateText);
                } catch (Exception e) {
                    // If an error is thrown when executing the above statement in the "try" block,
                    // catch the exception here, so the app doesn't crash. Print a log message
                    // with the message from the exception.
                    Log.e("QueryUtils", "Problem parsing the news date", e);
                }

                // Extract the value for the key called "webUrl"
                String url = currentArticle.getString("webUrl");

                // Extract the value for the key called "thumbnail"
                String imageUrl = currentArticle.getJSONObject("fields").getString("thumbnail");


                // Create a new {@link Article} object with the title, section, author,
                // date, url, imageUrl from the JSON response.
                Article article = new Article(title,section,author,date,imageUrl,url);

                // Add the new {@link Article} to the list of articles.
                articles.add(article);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the article JSON results", e);
        }

        // Return the list of earthquakes
        return articles;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
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
     * Query the The Guardian dataset and return a list of {@link Article} objects.
     */
    public static List<Article> fetchEarthquakeData(String requestUrl) {

        Log.i(LOG_TAG, "TEST: fetchEarthquakeData() called");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Article}s
        List<Article> articles = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Article}s
        return articles;
    }

}

