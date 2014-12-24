package Places;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import Utilities.Constants;
import Utilities.Utility;

/**
 * Created by Narcis11 on 20.12.2014.
 */
public class FetchGooglePlaces extends AsyncTask<String, Void, String[]> {
    private final Context mContext;
    private final String LOG_TAG = FetchGooglePlaces.class.getSimpleName();
    private ArrayAdapter<String> mShopsAdapter;

    public FetchGooglePlaces (Context context, ArrayAdapter<String> shopsAdapter) {
            mContext = context;
            mShopsAdapter = shopsAdapter;
    }

    @Override
    protected String[] doInBackground(String... params) {
        if (params.length == 0) {
            return null;//no input, so we return null
        }
        String radius = Utility.formatPreferredRange(mContext);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String placesJsonStr = "";//used for storing the response from the API call
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
            Log.i(LOG_TAG, "Uri is: " + builtUri.toString());

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
            placesJsonStr = buffer.toString();
            //   Log.i(LOG_TAG,"Response is: " + placesJsonStr);
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
        try {
            return getPlaceDataFromJson(placesJsonStr);
        }
        catch(JSONException e) {
            Log.i(LOG_TAG,"return failed in doInBackground");
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the response.
        return null;
    }

    private String[] getPlaceDataFromJson(String placeJsonStr)
            throws JSONException {

        final String API_RESULT = "results";//root
        final String API_STATUS = "status";//we'll perform some checks on this one
        // Location information
        final String API_ID = "id";
        final String API_NAME = "name";
        final String API_OPENING_HOURS = "opening_hours";//root
        final String API_OPEN_NOW = "open_now"; //child of opening_hours
        final String API_ADDRESS = "vicinity";



        final String API_GEOMETRY = "geometry";
        //child of geometry
        final String API_LOCATION = "location";
        //children of location
        final String API_COORD_LAT = "lat";
        final String API_COORD_LONG = "lng";



        try {
            JSONObject placesJson = new JSONObject(placeJsonStr);
            JSONArray placesArray = placesJson.getJSONArray(API_RESULT); //root node

            String[] resultStrs = new String[50];//we assume that we'll never get more than 50 results.
            for (int i = 0; i < placesArray.length(); i++) {
                // These are the values that will be collected.
                String id;
                String placeName;
                String address;
                String openNow = Constants.NOT_AVAILABLE;
                String latitude;
                String longitude;


                // placeDetails is the whole object representing a shop
                JSONObject placeDetails = placesArray.getJSONObject(i);
                JSONObject geometry = placeDetails.getJSONObject(API_GEOMETRY); //geometry object
                JSONObject location = geometry.getJSONObject(API_LOCATION); //location object
                latitude = location.getString(API_COORD_LAT);
                longitude = location.getString(API_COORD_LONG);
                Log.i(LOG_TAG,"Lat/Lng = " + latitude + "/" + longitude);
                //getting info from opening_hours
                try {
                    //some shops don't have opening hours, that's why we put this request into a try/catch
                    JSONObject openingHours = placeDetails.getJSONObject(API_OPENING_HOURS);
                    openNow = openingHours.getString(API_OPEN_NOW);
                }
                catch(JSONException e) {
                    Log.e(LOG_TAG,"Opening_Hours - Caught JSON Exception: " + e.getMessage());
                }


                //main info from the root object
                id = placeDetails.getString(API_ID);
                placeName = placeDetails.getString(API_NAME);
                address = placeDetails.getString(API_ADDRESS);

                resultStrs[i] = placeName + ", " + address + ", open? " + openNow + Constants.COORDINATES_SEPARATOR + latitude + Constants.LAT_LNG_SEPARATOR + longitude;
                Log.i(LOG_TAG,"Parsed result is: " + resultStrs[i]);
            }

            return resultStrs;
        }
        catch(JSONException e) {
            Log.e(LOG_TAG,"Caught JSON Exception: " + e.getMessage());
            e.printStackTrace();
        }
        finally {

           // return null;//fetch failed, nothing to return;
        }
        Log.i(LOG_TAG,"Returned null from getPlaceDataFromJson.");
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {
        super.onPostExecute(result);
        if (result != null) {
            Log.i(LOG_TAG,"result != null in onPostExecute()");
            mShopsAdapter.clear();
            for (String placeDetailsString : result) {
                //we can't add nulls to the Adapter, otherwise it will issue a NullPointerException
                if (placeDetailsString != null) {
                    mShopsAdapter.add(placeDetailsString);
                }
            }
            //Warning for later changes: if you use addAll() instead of this loop, you'll probably get a NullPointerException
            //if the result contains nulls. You must always check for nulls when passing data to the adapter.

        }
    }
}