package sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import Utilities.Constants;
import Utilities.GlobalState;
import Utilities.Utility;
import data.ShopsContract;
import waldo.bike.bikeshops.R;

/**
 * Created by Narcis11 on 25.12.2014.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = SyncAdapter.class.getSimpleName();
    private Context mContext;
    private boolean DEBUG = false;
    //indices of the fields collected from the Place Details API call
    final int PHONE_NUMBER_ID = 0;
    final int WEEKDAY_TEXT_ID = 1;
    final int RATING_ID = 2;
    final int WEBSITE_ID = 3;
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

            Log.i(LOG_TAG, "Starting sync...");
          //  if (extras.getString(Constants.BUNDLE_SHOP_PLACE_ID,"").equals("")) { //get all shops
                String[] finalResult = new String[100];
                String preferredUnit = Utility.getPreferredUnit(mContext);
                String metric = "Metric";
                String radius = "";
                radius = (preferredUnit.equals(metric)) ? Utility.formatPreferredRangeMetric(mContext) : Utility.formatPreferredRangeImperial(mContext);
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String placesJsonStr = "";//used for storing the response from the API call
                //used for querying the Google Places API
                final String types = Constants.PLACE_TYPE;
                final String key = Constants.API_KEY;
                final String latLng = GlobalState.USER_LAT + Constants.COMMA_SEPARATOR + GlobalState.USER_LNG;
                //final String latLng = "44.4391463,26.1428946";
                final String output = "json";
                try {
                    //******Getting the info for all shops*****
                    //the query parameters used in the Nearby search call
                    final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/" + output + "?";
                    final String QUERY_LOCATION = "location";
                    final String QUERY_RADIUS = "radius";
                    final String QUERY_KEY = "key";
                    final String QUERY_TYPES = "types";
                    //build up the URI
                    Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendQueryParameter(QUERY_LOCATION, latLng)
                            .appendQueryParameter(QUERY_RADIUS, radius)
                            .appendQueryParameter(QUERY_KEY, key)
                            .appendQueryParameter(QUERY_TYPES, types)
                            .build();
                    Log.i(LOG_TAG, "Places Uri is: " + builtUri.toString());

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
                        Log.i(LOG_TAG, "No input stream");
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
                        Log.i(LOG_TAG, "buffer.length() == 0");
                    }
                    placesJsonStr = buffer.toString();
                    //   Log.i(LOG_TAG,"Response is: " + placesJsonStr);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error in fetching places: " + e);

                } finally {
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

                //*****Parsing the info for all shops*******
                final String API_RESULT = "results";//root
                final String API_STATUS = "status";//we'll perform some checks on this one
                // Location information
                final String API_PLACE_ID = "place_id";
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
                int distanceToShop;
                double distanceDuration;
                String apiCallStatus = "";
                int isShopOpen = 2; //means that this info is not available
                try {
                    JSONObject placesJson = new JSONObject(placesJsonStr);
                    apiCallStatus = placesJson.getString(API_STATUS);
                    Log.i(LOG_TAG, "Status is " + apiCallStatus);
                    //we need an intent to signal when the sync has finished
                    Intent syncIntent = new Intent();
                    syncIntent.setAction(Constants.SYNC_BUNDLE_STATUS_ACTION);
                    if (apiCallStatus.equals(Constants.OK_STATUS)) { //we only parse if the result is OK
                        JSONArray placesArray = placesJson.getJSONArray(API_RESULT); //root node
                        Vector<ContentValues> cVVector = new Vector<ContentValues>(placesArray.length());
                        for (int i = 0; i < placesArray.length(); i++) {
                            // These are the values that will be collected.
                            String place_id;
                            String placeName;
                            String address;
                            //some shops do not have this piece of info, so we presume from the start that it's unavailable
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
                            //44.4391463,26.1428946
                            Location userLocation = new Location(Constants.PROVIDER);
                            //      Log.i(LOG_TAG, "User location = " + GlobalState.USER_LAT + "/" + GlobalState.USER_LNG);
              /*        userLocation.setLatitude(Double.valueOf("44.4391463"));
                        userLocation.setLongitude(Double.valueOf("26.1428946"));*/
                            userLocation.setLatitude(Double.valueOf(GlobalState.USER_LAT));
                            userLocation.setLongitude(Double.valueOf(GlobalState.USER_LNG));
                            Location shopLocation = new Location(Constants.PROVIDER);
                            shopLocation.setLatitude(Double.valueOf(latitude));
                            shopLocation.setLongitude(Double.valueOf(longitude));
                            distanceToShop = (int) Math.round(userLocation.distanceTo(shopLocation));
                            distanceDuration = Utility.calculateDistanceDuration(distanceToShop, getContext());
                            //main info from the root object
                            place_id = placeDetails.getString(API_PLACE_ID);
                            //Log.i(LOG_TAG,"place_id = " + place_id);
                            placeName = placeDetails.getString(API_NAME);
                            address = placeDetails.getString(API_ADDRESS);

                            if (!openNow.equals(Constants.NOT_AVAILABLE) && openNow != null) {
                                isShopOpen = Boolean.valueOf(openNow) ? 1 : 0;
                            }

                            String[] placeDetailRequest = getPlaceDetails(place_id);
                           // Log.i(LOG_TAG, "****Details response*** = " + placeDetailRequest[0] + "/" + placeDetailRequest[1] + "/" + placeDetailRequest[2] + "/" + placeDetailRequest[3]);
                            ContentValues shopsValues = new ContentValues();

                            //TODO: Get the is_partner, discount_value, promo_text and logo_url fields
                            //creating the vector and inserting the values
                            shopsValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_NAME, placeName);
                            shopsValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS, address);
                            shopsValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE, latitude);
                            shopsValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE, longitude);
                            shopsValues.put(ShopsContract.ShopsEntry.COLUMN_IS_OPEN, isShopOpen);
                            shopsValues.put(ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER, distanceToShop);
                            shopsValues.put(ShopsContract.ShopsEntry.COLUMN_DISTANCE_DURATION, distanceDuration);
                            shopsValues.put(ShopsContract.ShopsEntry.COLUMN_PLACE_ID, place_id);
                            shopsValues.put(ShopsContract.ShopsEntry.COLUMN_WEBSITE, placeDetailRequest[WEBSITE_ID]);
                            shopsValues.put(ShopsContract.ShopsEntry.COLUMN_PHONE_NUMBER, placeDetailRequest[PHONE_NUMBER_ID]);
                            shopsValues.put(ShopsContract.ShopsEntry.COLUMN_OPENING_HOURS, placeDetailRequest[WEEKDAY_TEXT_ID]);
                            shopsValues.put(ShopsContract.ShopsEntry.COLUMN_RATING, placeDetailRequest[RATING_ID]);
                            //Only for testing. Will be removed.
                            if (i < 3) {
                                Log.i(LOG_TAG,"Partner shop: " + placeName);
                                shopsValues.put(ShopsContract.ShopsEntry.COLUMN_IS_PARTNER, 1);
                                shopsValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_PROMO_TEXT,"-15% reducere la adidasi, pantofi. Vino acum să-ți ridici reducerea.#-20% reducere la ochi, nas, gură, urechi.");
                                shopsValues.put(ShopsContract.ShopsEntry.COLUMN_DISCOUNT_VALUE,(i+1)*5);
                            }
                            cVVector.add(shopsValues);
                        }
                        if (cVVector.size() > 0) {
                            //we empty the database before inserting the new data
                            mContext.getContentResolver().delete(ShopsContract.ShopsEntry.CONTENT_URI, null, null);
                            ContentValues[] cvArray = new ContentValues[cVVector.size()];
                            cVVector.toArray(cvArray);
                            int rowsInserted;
                            rowsInserted = mContext.getContentResolver().bulkInsert(
                                    ShopsContract.ShopsEntry.CONTENT_URI,
                                    cvArray);
                            Log.i(LOG_TAG, "No of bulk rows inserted = " + rowsInserted);
                            if (DEBUG) {
                                Cursor shopsCursor = mContext.getContentResolver().query(
                                        ShopsContract.ShopsEntry.CONTENT_URI,
                                        null,
                                        null,
                                        null,
                                        null
                                );
                                Log.i(LOG_TAG, "No of rows in shops = " + shopsCursor.getCount());
                                if (shopsCursor.moveToFirst()) {
                                    ContentValues resultValues = new ContentValues();
                                    DatabaseUtils.cursorRowToContentValues(shopsCursor, resultValues);
                                    Log.i(LOG_TAG, "Query succeeded! **********");
                                    for (String loopKey : resultValues.keySet()) {
                                        Log.i(LOG_TAG, loopKey + ": " + resultValues.getAsString(loopKey));
                                    }
                                } else {
                                    Log.i(LOG_TAG, "Query failed! :( **********");
                                }
                                shopsCursor.close();
                            }
                        }
                    }
                    else {//notify that the sync is complete (actually, it never happened
                        //we notify only when an error occured/there were no results. If the sync goes well, we remove the refresh circle in the onLoadFinished
                        //from Shop Fragment
                        syncIntent.putExtra(Constants.SYNC_BUNDLE_STATUS_KEY,Constants.SYNC_BUNDLE_STATUS_STOPPED);//the sync has stopped
                        syncIntent.putExtra(Constants.SYNC_BUNDLE_RESULT_KEY,apiCallStatus);//the status of the sync
                        mContext.sendBroadcast(syncIntent);
                    }

                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Caught JSON Exception: " + e.getMessage());
                    e.printStackTrace();
                }
            //}
/*        else {
                Log.i(LOG_TAG,"Updating for shop! ");
                String placeId = extras.getString(Constants.BUNDLE_SHOP_PLACE_ID);
                String[] placeDetailRequest = getPlaceDetails(placeId);
                //Log.i(LOG_TAG, "****Details response*** = " + placeDetailRequest[0] + "/" + placeDetailRequest[1] + "/" + placeDetailRequest[2] + "/" + placeDetailRequest[3]);
                ContentValues updateValues = new ContentValues();
                updateValues.put(ShopsContract.ShopsEntry.COLUMN_WEBSITE,placeDetailRequest[WEBSITE_ID]);
                updateValues.put(ShopsContract.ShopsEntry.COLUMN_PHONE_NUMBER,placeDetailRequest[PHONE_NUMBER_ID]);
                updateValues.put(ShopsContract.ShopsEntry.COLUMN_OPENING_HOURS,placeDetailRequest[WEEKDAY_TEXT_ID]);
                updateValues.put(ShopsContract.ShopsEntry.COLUMN_RATING,placeDetailRequest[RATING_ID]);
                String whereClause =  ShopsContract.ShopsEntry.COLUMN_PLACE_ID + "=" + "'"  + placeId + "'";
              //  Log.i(LOG_TAG,"where clause is " + whereClause);
                int updatedRows = mContext.getContentResolver().update(ShopsContract.ShopsEntry.CONTENT_URI,updateValues,whereClause,null);
                Log.i(LOG_TAG,"No of rows updated = " + updatedRows);
            }*/
        }

    public static void syncImmediately(Context context/*, String placeId*/) {
        Log.i(LOG_TAG, "In syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
/*        if (placeId != null) {
            bundle.putString(Constants.BUNDLE_SHOP_PLACE_ID,placeId);
        }*/
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

    private String[] getPlaceDetails (String place_id) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String placeDetailsJsonStr = "";//used for storing the response from the Place Details API call
        final String key = Constants.API_KEY;
        final String QUERY_KEY = "key";
        final String output = "json";
        final String placeId = "placeid";
        String returnPlaceDetails[] = new String[5];
        try {
            final String BASE_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/" + output + "?";
            Uri builtPlaceUri = Uri.parse(BASE_DETAILS_URL).buildUpon()
                    .appendQueryParameter(placeId,place_id)
                    .appendQueryParameter(QUERY_KEY,key)
                    .build();
           // Log.i(LOG_TAG, "Place Details Uri is: " + builtPlaceUri.toString());
            URL url = new URL(builtPlaceUri.toString());
            //Create the request to Google, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(Constants.HTTP_GET);
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                Log.i(LOG_TAG, "No input stream");
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
                Log.i(LOG_TAG, "buffer.length() == 0");
            }
            placeDetailsJsonStr = buffer.toString();
        }
        catch(IOException e) {
            Log.e(LOG_TAG, "Error in fetching place details for place_id: + " + place_id + ". Error: " + e);
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

       //******Beginning to parse the result*****
        final String API_RESULT = "result";
        final String API_PHONE_NUMBER = "formatted_phone_number";
        final String API_WEEKDAY_TEXT = "weekday_text";
        final String API_RATING = "rating";
        final String API_WEBSITE = "website";
        final String API_STATUS = "status";
        final String API_OPENING_HOURS = "opening_hours";
        final String API_REVIEWS = "reviews";
        final String API_ASPECTS = "aspects";
        Double sumOfRatings = 0.0;
        String placePhoneNumber = "";
        String placeOpeningHours = "";
        Double rating = 0.0;
        String website = "";
        String apiCallStatus = "";
        String placeReviews = "";
        JSONObject singleReviewJson;
        JSONArray aspectsArray;
        try {
            JSONObject placeDetailsJson = new JSONObject(placeDetailsJsonStr);
            apiCallStatus = placeDetailsJson.getString(API_STATUS);
            //we only parse if the result is OK. Otherwise, we return an empty string[].
            if (apiCallStatus.equals(Constants.OK_STATUS)) {
                //We have to wrap all of the extractions from the response in a try-catch phrase, because not all of the fields are available.
                JSONObject placeDetailsObject = placeDetailsJson.getJSONObject(API_RESULT);//root node
                try {//get phone number
                    placePhoneNumber = placeDetailsObject.getString(API_PHONE_NUMBER);
                }
                catch(JSONException phoneException) {
                    Log.i(LOG_TAG,phoneException.getMessage());
                }
                //get opening hours
                try {
                    JSONObject openingHours = placeDetailsObject.getJSONObject(API_OPENING_HOURS);
                    JSONArray jsonWeekdayArray = (JSONArray) openingHours.get(API_WEEKDAY_TEXT);
                    for (int i = 0; i < jsonWeekdayArray.length(); i++) {
                        //Log.i(LOG_TAG,"jsonWeekdayArray.get(i): " + jsonWeekdayArray.get(i));
                        placeOpeningHours = placeOpeningHours + jsonWeekdayArray.get(i) + Constants.HASH_SEPARATOR;
                    }
                }
                catch (JSONException e) {
                    Log.i(LOG_TAG,e.getMessage());
                }
                try {//get rating
                    rating = placeDetailsObject.getDouble(API_RATING);
                    returnPlaceDetails[RATING_ID] = String.valueOf(rating);
                }
                catch (JSONException e) {
                  //  Log.i(LOG_TAG,e.getMessage());
                  //  Log.i(LOG_TAG,"Wir rechnen den durchschnittlichen Wert");
                    try {//if there's no "rating" field in the root node, we calculate the mean rating from the reviews
                        JSONArray placeReviewsArray = (JSONArray) placeDetailsObject.get(API_REVIEWS);
                        for (int i = 0; i < placeReviewsArray.length(); i++) {
                            singleReviewJson = placeReviewsArray.getJSONObject(i);//a whole review
                            //aspectsArray = singleReviewJson.getJSONArray(API_ASPECTS);
                            rating = singleReviewJson.getDouble(API_RATING);
                            sumOfRatings += rating;
                        }
                        //format the result to one decimal before returning
                        BigDecimal returnValueRating = new BigDecimal(sumOfRatings/placeReviewsArray.length());
                        returnValueRating = returnValueRating.setScale(1, RoundingMode.HALF_UP);
                        returnPlaceDetails[RATING_ID] = returnValueRating.toString();
                    }
                    catch (JSONException jsonExcep) {
                        Log.i(LOG_TAG,jsonExcep.getMessage());
                    }

                }
                try {
                    website = placeDetailsObject.getString(API_WEBSITE);
                }
                catch (JSONException json) {
                    Log.i(LOG_TAG,json.getMessage());
                }
                returnPlaceDetails[PHONE_NUMBER_ID] = placePhoneNumber;
                returnPlaceDetails[WEEKDAY_TEXT_ID] = placeOpeningHours;
                returnPlaceDetails[WEBSITE_ID] = website;
            }
        }
        catch(JSONException e) {
            Log.e(LOG_TAG, "Parsing place details | JSON Exception: " + e.getMessage());
        }
        return returnPlaceDetails;
    }
}
