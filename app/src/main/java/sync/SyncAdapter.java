package sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import Utilities.Constants;
import Utilities.GlobalState;
import Utilities.Utility;
import waldo.bike.waldo.R;

/**
 * Created by Narcis11 on 25.12.2014.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = SyncAdapter.class.getSimpleName();
    private ArrayAdapter<String> mShopsAdapter;
    private Context mContext;
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    Log.i(LOG_TAG, "Starting sync...");
        String[] finalResult = new String[100];
        mShopsAdapter = GlobalState.GLOBAL_ADAPTER;
        String radius = Utility.formatPreferredRange(mContext);
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String placesJsonStr = "";//used for storing the response from the API call
        //used for querying the Google Places API
        final String types = "bicycle_store";
        final String key = Constants.API_KEY;
        final String latLng = GlobalState.USER_LAT + Constants.COMMA_SEPARATOR + GlobalState.USER_LNG;
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
            urlConnection.setRequestMethod(Constants.HTTP_GET);
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.i(LOG_TAG,"No input stream");
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
                Log.i(LOG_TAG,"buffer.length() == 0");
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
           finalResult = getPlaceDataFromJson(placesJsonStr);
            if (finalResult != null) {
                //GlobalState.GLOBAL_ADAPTER.clear();
                for (String placeDetailsString : finalResult) {
                    //we can't add nulls to the Adapter, otherwise it will issue a NullPointerException
                    if (placeDetailsString != null) {
                        GlobalState.GLOBAL_ADAPTER.add(placeDetailsString);
                    }
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the response.
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
            GlobalState.FETCH_STATUS = placesJson.getString(API_STATUS);
            Log.i(LOG_TAG,"Status is " + GlobalState.FETCH_STATUS);
            if ( GlobalState.FETCH_STATUS.equals(Constants.OK_STATUS)) { //we only parse if the result is OK
                JSONArray placesArray = placesJson.getJSONArray(API_RESULT); //root node

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
                    Log.i(LOG_TAG, "Lat/Lng = " + latitude + "/" + longitude);
                    //getting info from opening_hours
                    try {
                        //some shops don't have opening hours, that's why we put this request into a try/catch
                        JSONObject openingHours = placeDetails.getJSONObject(API_OPENING_HOURS);
                        openNow = openingHours.getString(API_OPEN_NOW);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Opening Hours JSON Exception: " + e.getMessage());
                    }


                    //main info from the root object
                    id = placeDetails.getString(API_ID);
                    placeName = placeDetails.getString(API_NAME);
                    address = placeDetails.getString(API_ADDRESS);

                    resultStrs[i] = placeName + Constants.COMMA_SEPARATOR + " " + address + ", open? " + openNow + Constants.PIPE_SEPARATOR + latitude + Constants.SLASH_SEPARATOR + longitude;
                    Log.i(LOG_TAG, "Parsed result is: " + resultStrs[i]);
                    GlobalState.ALL_SHOPS_INFO += Constants.HASH_SEPARATOR + placeName + Constants.DOLLAR_SEPARATOR + latitude + Constants.DOLLAR_SEPARATOR + longitude;
                }
                return resultStrs;
            }

            return  null; //result is NOT OK, so we return null and handle the error in onPostExecute();

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

    public static void syncImmediately(Context context) {
        Log.i(LOG_TAG, "In syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        if (ContentResolver.isSyncPending(getSyncAccount(context), context.getString(R.string.content_authority))  ||
                ContentResolver.isSyncActive(getSyncAccount(context), context.getString(R.string.content_authority))) {
            Log.i("ContentResolver", "SyncPending, canceling");
            ContentResolver.cancelSync(getSyncAccount(context), context.getString(R.string.content_authority));
        }
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */


        }
        return newAccount;
    }
}
