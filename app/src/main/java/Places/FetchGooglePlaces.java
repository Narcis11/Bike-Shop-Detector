package Places;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Utilities.Constants;
import Utilities.Utility;

/**
 * Created by Narcis11 on 18.12.2014.
 * Used for retrieving the desired places using Google's API
 * Example URI:
 * https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=44.437919,26.127671&radius=3000&key=AIzaSyDfiTJ2PvK6eOjpm62eV6FlmX3HcInona0&types=bicycle_store
 */
public class FetchGooglePlaces extends AsyncTask<String, Void, String[]> {
    private final Context mContext;
    private final String LOG_TAG = FetchGooglePlaces.class.getSimpleName();
    public FetchGooglePlaces (Context context) {
        mContext = context;

    }

    @Override
    protected String[] doInBackground(String... params) {
        if (params.length == 0) {
            return null;//no input, so we return null
        }
        String radius = Utility.formatPreferredRange(mContext);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String[] placesJsonStr = new String[10];//used for storing the response from the API call
        //used for querying the Google Places API
        final String types = "bicycle_store";
        final String transferScheme = "https";
        final String key = Constants.API_KEY;
        final String latLng = params[0] + "," + params[1];
        final String output = "json";
        try {
            //the query parameters used in the call
            final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/" + output + "?";
            final String QUERY_LOCATION = "location";
            final String QUERY_RADIUS = "radius";
            final String QUERY_KEY = "key";
            final String QUERY_TYPES = "types";
            //build up the URI
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_LOCATION,latLng)
                    .appendQueryParameter(QUERY_RADIUS,radius)
                    .appendQueryParameter(QUERY_KEY,key)
                    .appendQueryParameter(QUERY_TYPES,types)
                    .build();
            Log.i(LOG_TAG,"Uri is: " + builtUri.toString());

            URL url = new URL(builtUri.toString());

            //Create the request to Google, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            placesJsonStr[0] = buffer.toString();
            Log.i(LOG_TAG,"Response is: " + placesJsonStr[0]);
        }
        catch(IOException e) {
            Log.e(LOG_TAG, "Error in fetching places: " + e);

        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return placesJsonStr;
    }
}
