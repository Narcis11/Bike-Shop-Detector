package Places;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import Utilities.Constants;
import Utilities.GlobalState;
import Utilities.Utility;
import data.ShopsContract;
import waldo.bike.waldo.R;

/**
 * Created by Narcis11 on 20.12.2014.
 */
public class FetchGooglePlaces extends AsyncTask<String, Void, Void> {
    private final Context mContext;
    private final String LOG_TAG = FetchGooglePlaces.class.getSimpleName();
    private ArrayAdapter<String> mShopsAdapter;
    private boolean DEBUG = true;
    public FetchGooglePlaces (Context context) {
            mContext = context;
    }
    @Override
    protected Void doInBackground(String... params) {
        if (params.length == 0) {
            return null;//no input, so we return null
        }
        String preferredUnit = Utility.getPreferredUnit(mContext);
        String metric = "Metric";
        String radius = "";
        if (preferredUnit.equals(metric)) {
            radius = Utility.formatPreferredRangeMetric(mContext);
        }
        else {
            radius = Utility.formatPreferredRangeImperial(mContext);
        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String placesJsonStr = "";//used for storing the response from the API call
        //used for querying the Google Places API
        final String types = "bicycle_store";
        final String key = Constants.API_KEY;
        final String latLng = params[0] + "," + params[1];
        final String output = "json";
        try {
            //the query parameters used in the API places call
            final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/" + output + "?";
            final String QUERY_LOCATION = "location";
            final String QUERY_RADIUS = "radius";
            final String QUERY_KEY = "key";
            final String QUERY_TYPES = "types";

            //the query parameters used in the API directions call
            final String BASE_DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/" + output + "?";
            //build up the URI
            Uri builtPlacesUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_LOCATION,latLng)
                    .appendQueryParameter(QUERY_RADIUS,radius)
                    .appendQueryParameter(QUERY_KEY,key)
                    .appendQueryParameter(QUERY_TYPES,types)
                    .build();
            Log.i(LOG_TAG, "Uri is: " + builtPlacesUri.toString());

            URL url = new URL(builtPlacesUri.toString());

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
            getPlaceDataFromJson(placesJsonStr);
        }
        catch(JSONException e) {
            Log.i(LOG_TAG,"return failed in doInBackground");
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // This will only happen if there was an error getting or parsing the response.
        return null;
    }

    private void getPlaceDataFromJson(String placeJsonStr)
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
        int dummyDistance = 300;
        int dummyDuration = 3;


        try {
            JSONObject placesJson = new JSONObject(placeJsonStr);
            GlobalState.FETCH_STATUS = placesJson.getString(API_STATUS);
            Log.i(LOG_TAG,"Status is " + GlobalState.FETCH_STATUS);
            if ( GlobalState.FETCH_STATUS.equals(Constants.OK_STATUS)) { //we only parse if the result is OK
            JSONArray placesArray = placesJson.getJSONArray(API_RESULT); //root node
                Vector<ContentValues> cVVector = new Vector<ContentValues>(placesArray.length());
            String[] resultStrs = new String[100];//we assume that we'll never get more than 100 results.
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
             //   Log.i(LOG_TAG, "Lat/Lng = " + latitude + "/" + longitude);
                //getting info from opening_hours
                try {
                    //some shops don't have opening hours, that's why we put this request into a try/catch
                    JSONObject openingHours = placeDetails.getJSONObject(API_OPENING_HOURS);
                    openNow = openingHours.getString(API_OPEN_NOW);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Opening Hours JSON Exception: " + e.getMessage());
                }
                //*************Creating dummy data********************
                dummyDistance+=150 + i*20;
                dummyDuration+=3;
                //main info from the root object
                id = placeDetails.getString(API_ID);
                placeName = placeDetails.getString(API_NAME);
                address = placeDetails.getString(API_ADDRESS);
                resultStrs[i] = placeName + Constants.COMMA_SEPARATOR + " " + address + ", open? " + openNow + Constants.PIPE_SEPARATOR + latitude + Constants.SLASH_SEPARATOR + longitude;
             //   Log.i(LOG_TAG, "Parsed result is: " + resultStrs[i]);
                GlobalState.ALL_SHOPS_INFO += Constants.HASH_SEPARATOR + placeName + Constants.DOLLAR_SEPARATOR + latitude + Constants.DOLLAR_SEPARATOR + longitude;
                //inserting the data
                ContentValues shopsValues = new ContentValues();
                int isShopOpen = 2; //means that this info is not available
                if ( !openNow.equals(Constants.NOT_AVAILABLE) && openNow != null) {
                    isShopOpen = Boolean.valueOf(openNow) ? 1 : 0;
                }

                shopsValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_NAME,placeName);
                shopsValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS,address);
                shopsValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE,latitude);
                shopsValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE,longitude);
                shopsValues.put(ShopsContract.ShopsEntry.COLUMN_IS_OPEN, isShopOpen);
                shopsValues.put(ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER,dummyDistance);
                shopsValues.put(ShopsContract.ShopsEntry.COLUMN_DISTANCE_DURATION,dummyDuration);
                cVVector.add(shopsValues);
            }
                if (cVVector.size() > 0) {
                    //we empty the database before inserting the new data
                    mContext.getContentResolver().delete(ShopsContract.ShopsEntry.CONTENT_URI,null,null);
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    int rowsInserted;
                    rowsInserted = mContext.getContentResolver().bulkInsert(
                            ShopsContract.ShopsEntry.CONTENT_URI,
                            cvArray);
                    Log.i(LOG_TAG,"No of bulk rows inserted = " + rowsInserted);
                    if (DEBUG) {
                        Cursor shopsCursor = mContext.getContentResolver().query(
                                ShopsContract.ShopsEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null
                        );
                        Log.i(LOG_TAG,"No of rows in shops = " + shopsCursor.getCount());
                        if (shopsCursor.moveToFirst()) {
                            ContentValues resultValues = new ContentValues();
                            DatabaseUtils.cursorRowToContentValues(shopsCursor, resultValues);
                            Log.i(LOG_TAG, "Query succeeded! **********");
                            for (String key : resultValues.keySet()) {
                                Log.i(LOG_TAG, key + ": " + resultValues.getAsString(key));
                            }
                        } else {
                            Log.i(LOG_TAG, "Query failed! :( **********");
                        }
                        shopsCursor.close();
                    }
                }
            }


        }
        catch(JSONException e) {
            Log.e(LOG_TAG,"Caught JSON Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


}