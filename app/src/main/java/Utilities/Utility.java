package Utilities;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;

import waldo.bike.bikeshops.BikeShopsDetector;
import waldo.bike.bikeshops.R;

/**
 * Created by nmihai on 15.12.2014.
 */
public class Utility {
        private static final String LOG_TAG = Utility.class.getSimpleName();
        private static String mKmSign = "km";
        private static String mMeterSign = "m";
        private static String mMileSign = "mi";
        private static String mFeetSign = "ft";
        public static String getPreferredRangeMetric(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return prefs.getString(context.getString(R.string.pref_range_key),
                    context.getString(R.string.pref_range_default));
        }

    public static String getPreferredRangeImperial(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_range_key),
                context.getString(R.string.pref_range_imperial_default));
    }
    public static String getPreferredUnit(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_unit_key),
                context.getString(R.string.pref_unit_default_metric));
    }

    public static String formatPreferredRangeImperialToMetric(String imperialRange) {
        final String oneKilometer = "1 km";
        final String twoKilometers = "2 km";
        final String threeKilometers = "3 km";
        final String fourKilometers = "4 km";
        final String fiveKilometers = "5 km";
        final String tenKilometers = "10 km";
        final String oneMi = "1 mile";
        final String twoMi = "2 miles";
        final String threeMi = "3 miles";
        final String fourMi = "4 miles";
        final String fiveMi = "5 miles";
        final String sixMi = "6 miles";
        switch (imperialRange){
            case(oneMi): {
                return oneKilometer;
            }
            case(twoMi): {
                return twoKilometers;
            }
            case(threeMi): {
                return threeKilometers;
            }
            case(fourMi): {
                return fourKilometers;
            }
            case(fiveMi): {
                return fiveKilometers;
            }
            case(sixMi): {
                return tenKilometers;
            }
            //sometimes, the input is correct
            default: return imperialRange;
        }
    }

    public static String formatPreferredRangeMetricToImperial(String metricRange) {
        final String oneKilometer = "1 km";
        final String twoKilometers = "2 km";
        final String threeKilometers = "3 km";
        final String fourKilometers = "4 km";
        final String fiveKilometers = "5 km";
        final String tenKilometers = "10 km";
        final String oneMi = "1 mile";
        final String twoMi = "2 miles";
        final String threeMi = "3 miles";
        final String fourMi = "4 miles";
        final String fiveMi = "5 miles";
        final String sixMi = "6 miles";
        switch (metricRange){
            case(oneKilometer): {
                return oneMi;
            }
            case(twoKilometers): {
                return twoMi;
            }
            case(threeKilometers): {
                return threeMi;
            }
            case(fourKilometers): {
                return fourMi;
            }
            case(fiveKilometers): {
                return fiveMi;
            }
            case(tenKilometers): {
                return sixMi;
            }
            //sometimes, the input is correct
            default: return metricRange;
        }
    }

    public static String formatPreferredSpeedImperialToMetric (String imperialSpeed) {
        final String fiveKm = "5 km/h";
        final String fourteenKm = "14 km/h";
        final String seventeenKm = "17 km/h";
        final String twentyKm = "20 km/h";
        final String twentyThreeKm = "23 km/h";
        final String twentySixKm = "26 km/h";
        final String twentyNineKm = "29 km/h";
        final String thirtyTwoKm = "32 km/h";
        final String threeMi = "3 mph";
        final String nineMi = "9 mph";
        final String tenMi = "10 mph";
        final String twelveMi = "12 mph";
        final String fourteenMi = "14 mph";
        final String sixteenMi = "16 mph";
        final String eighteenMi = "18 mph";
        final String twentyMi = "20 mph";

        switch(imperialSpeed) {
            case (threeMi): {
                return fiveKm;
            }
            case (nineMi): {
                return fourteenKm;
            }
            case (tenMi): {
                return seventeenKm;
            }
            case (twelveMi): {
                return twentyKm;
            }
            case (fourteenMi): {
                return twentyThreeKm;
            }
            case (sixteenMi): {
                return twentySixKm;
            }
            case (eighteenMi): {
                return twentyNineKm;
            }
            case (twentyMi): {
                return thirtyTwoKm;
            }
            default: return imperialSpeed;

        }
    }

    public static String formatPreferredSpeedMetricToImperial(String metricSpeed) {
        final String fiveKm = "5 km/h";
        final String fourteenKm = "14 km/h";
        final String seventeenKm = "17 km/h";
        final String twentyKm = "20 km/h";
        final String twentyThreeKm = "23 km/h";
        final String twentySixKm = "26 km/h";
        final String twentyNineKm = "29 km/h";
        final String thirtyTwoKm = "32 km/h";
        final String threeMi = "3 mph";
        final String nineMi = "9 mph";
        final String tenMi = "10 mph";
        final String twelveMi = "12 mph";
        final String fourteenMi = "14 mph";
        final String sixteenMi = "16 mph";
        final String eighteenMi = "18 mph";
        final String twentyMi = "20 mph";

        switch(metricSpeed) {
            case (fiveKm): {
                return threeMi;
            }
            case (fourteenKm): {
                return nineMi;
            }
            case (seventeenKm): {
                return tenMi;
            }
            case (twentyKm): {
                return twelveMi;
            }
            case (twentyThreeKm): {
                return fourteenMi;
            }
            case (twentySixKm): {
                return sixteenMi;
            }
            case (twentyNineKm): {
                return eighteenMi;
            }
            case (thirtyTwoKm): {
                return twentyMi;
            }
            default: return metricSpeed;

        }
    }
    public static String getPreferredSpeed (Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_speed_key),
                context.getString(R.string.pref_speed_default_metric));
    }

    public static boolean getPreferredNotification(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        return prefs.getBoolean(displayNotificationsKey,
               Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));
    }

    public static float formatPreferredSpeedMetric (Context context) {
        String speed = getPreferredSpeed(context);
        final String fiveKm = "5 km/h";
        final String fourteenKm = "14 km/h";
        final String seventeenKm = "17 km/h";
        final String twentyKm = "20 km/h";
        final String twentyThreeKm = "23 km/h";
        final String twentySixKm = "26 km/h";
        final String twentyNineKm = "29 km/h";
        final String thirtyTwoKm = "32 km/h";
        final float returnFive = 5000;
        final float returnFourteen = 14000;
        final float returnSeventeen = 17000;
        final float returnTwenty = 20000;
        final float returnTwentyThree = 23000;
        final float returnTwentySix = 26000;
        final float returnTwentyNine = 29000;
        final float returnThirtyTwo = 32000;
        switch (speed) {
            case fiveKm: {
                return returnFive;
            }
            case fourteenKm: {
                return returnFourteen;
            }
            case seventeenKm: {
                return returnSeventeen;
            }
            case twentyKm: {
                return returnTwenty;
            }
            case twentyThreeKm: {
                return returnTwentyThree;
            }
            case twentySixKm: {
                return returnTwentySix;
            }
            case twentyNineKm: {
                return returnTwentyNine;
            }
            case thirtyTwoKm: {
                return returnThirtyTwo;
            }
            default: return returnSeventeen;
        }
    }

    public static float formatPreferredSpeedImperial(Context context) {
        String speed = getPreferredSpeed(context);
        final String threeMi = "3 mph";
        final String nineMi = "9 mph";
        final String tenMi = "10 mph";
        final String twelveMi = "12 mph";
        final String fourteenMi = "14 mph";
        final String sixteenMi = "16 mph";
        final String eighteenMi = "18 mph";
        final String twentyMi = "20 mph";
        //the return distances represent the equivalent of 3,9 etc. miles in meters
        //we calculate in metric units, and display in imperial ones
        final float returnThree = 5000;
        final float returnNine = 14000;
        final float returnTen = 17000;
        final float returnTwelve = 20000;
        final float returnFourteen = 23000;
        final float returnSixteen = 26000;
        final float returnEighteen = 29000;
        final float returnTwenty = 32000;

        switch(speed) {
            case threeMi: {
                return returnThree;
            }
            case nineMi: {
                return returnNine;
            }
            case tenMi: {
                return returnTen;
            }
            case twelveMi: {
                return returnTwelve;
            }
            case fourteenMi: {
                return returnFourteen;
            }
            case sixteenMi: {
                return returnSixteen;
            }
            case eighteenMi: {
                return returnEighteen;
            }
            case twentyMi: {
                return returnTwenty;
            }
            default: return returnTen;
        }

    }

    //used for determining the radius used in the Nearby Search. Returns 10.000 if no range is selected
    public static String formatPreferredRangeMetric(Context context) {
        String range = getPreferredRangeMetric(context);
        // we can't use getResources().getString(), because the switch statement requires constant expressions
        final String oneKilometer = "1 km";
        final String twoKilometers = "2 km";
        final String threeKilometers = "3 km";
        final String fourKilometers = "4 km";
        final String fiveKilometers = "5 km";
        final String tenKilometers = "10 km";
        final String returnOneKm = "1000";
        final String returnTwoKm = "2000";
        final String returnThreeKm = "3000";
        final String returnFourKm = "4000";
        final String returnFiveKm = "5000";
        final String returnTenKm = "10000";
        switch (range) {
            case oneKilometer: {
                return returnOneKm;
            }
            case twoKilometers: {
                return returnTwoKm;
            }
            case threeKilometers: {
                return returnThreeKm;
            }
            case fourKilometers:{
                return returnFourKm;
            }
            case fiveKilometers:{
                return returnFiveKm;
            }
            case tenKilometers:{
                return returnTenKm;
            }
            default: return returnTenKm;

        }
    }

    public static String formatPreferredRangeImperial(Context context) {
        String range = getPreferredRangeImperial(context);
        final String oneMi = "1 mile";
        final String twoMi = "2 miles";
        final String threeMi = "3 miles";
        final String fourMi = "4 miles";
        final String fiveMi = "5 miles";
        final String sixMi = "6 miles";
        //the equivalent in meters
        final String returnOne = "1609";
        final String returnTwo = "3218";
        final String returnThree = "4828";
        final String returnFour = "6437";
        final String returnFive = "8046";
        final String returnSix = "10000";

        switch(range) {
            case oneMi: {
                return returnOne;
            }
            case twoMi: {
                return returnTwo;
            }
            case threeMi: {
                return returnThree;
            }
            case fourMi: {
                return returnFour;
            }
            case fiveMi: {
                return returnFive;
            }
            case sixMi: {
                return returnSix;
            }
            default: return returnSix;
        }
    }
    //this method parses the location String ang extracts the latitude and longitude
    public static String[] getLatLngFromLocation (String location) {
        String[] latLng = new String[2];
        String latitudeReference = "mLatitude=";
        String longitudeReference = "mLongitude=";
        String altitudeReference = "mHasAltitude";
        latLng[0] = location.substring(location.indexOf(latitudeReference) + latitudeReference.length(), location.indexOf(longitudeReference) - 1);
        latLng[1] = location.substring(location.indexOf(longitudeReference) + longitudeReference.length(), location.indexOf(altitudeReference) - 1);
        return latLng;
    }

    public static int getScreenOrientation(Context context)
    {   //landscape = 2; portrait = 1;
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation;
    }

    public static double calculateDistanceDuration (int distanceToShop, Context context) {
        String preferredUnit = getPreferredUnit(context);
        float formattedSpeed;
        String metric = "Metric";
        if (preferredUnit.equals(metric)) {
            formattedSpeed = formatPreferredSpeedMetric(context);
        }
        else {
            formattedSpeed = formatPreferredSpeedImperial(context);
        }
        float minutes = 60;
        float distance = distanceToShop;
        float distanceDuration = (distance * minutes)/formattedSpeed;
        return distanceDuration;
    }

    public static String formatDistanceDuration (String distanceDuration) {
        if (distanceDuration.indexOf(".") > 0) {
            String minute = distanceDuration.substring(0, distanceDuration.indexOf("."));
            String decimals = "";
            if (distanceDuration.substring(distanceDuration.indexOf(".") + 1).length() > 1) {
                decimals = distanceDuration.substring(distanceDuration.indexOf(".") + 1, distanceDuration.indexOf(".") + 3);
            } else {
                decimals = distanceDuration.substring(distanceDuration.indexOf(".") + 1, distanceDuration.indexOf(".") + 2);
            }
            String seconds = String.valueOf((Integer.valueOf(decimals) * 60) / 100);
            return minute + "min " + seconds + "s";
        }
        else {
            return distanceDuration + "min";
        }
    }

    public static String formatDistanceMetric (String distance) {
        String doublezero = "00";
        if (Integer.valueOf(distance) >= 1000) {
            if (distance.length() <5) {
                //for distances between 1000-9999
                String km = distance.substring(0,1);
                String meters =  distance.substring(1, 3);
            //    Log.i(LOG_TAG,"Distance/Km/m = " + distance + " / " + km + " / " + roundedMeters );
                if (!meters.equals(doublezero)) {
                    return km + Constants.COMMA_SEPARATOR + meters + " " + mKmSign;
                }
                else {
                    return km + " " + mKmSign;
                }
            }
            else {

                //distances of 10.000 and above. Improbable to encounter, but still...
                String km = distance.substring(0,2);
                int meters = Math.round(Integer.valueOf(distance.substring(2)));
                if (meters > 0) {
                    return km + Constants.COMMA_SEPARATOR + String.valueOf(meters) + " " + mKmSign;
                }
                else {
                    return km + " " + mKmSign;
                }
            }
        }
        else {
            return distance + " " + mMeterSign;
        }
    }

    public static String formatDistanceImperial (String distance) {
        if (Integer.valueOf(distance) > 1609) {
            //TODO: There is a bug here for the hardcoded values from the GlobalState. Fix it!
            String doublezero = "00";
            Double reference = 1610.0;
            String calculatedYards;
            Double distanceDouble = Double.valueOf(distance);
            Double miles = distanceDouble/reference;
            String calculatedDistance = String.valueOf(miles);
            String calculatedMiles = calculatedDistance.substring(0, 1);
            if (calculatedDistance.length() == 1) { //x
                calculatedYards = doublezero;
            }
            else if (calculatedDistance.length() == 3) { //x.y
                calculatedYards = calculatedDistance.substring(2, 3);
            }
            else {
                calculatedYards = calculatedDistance.substring(2, 4);
            }
            if (!calculatedYards.equals(doublezero)) { //x.adsadadas (câte or fi)
                return calculatedMiles + Constants.DOT_SEPARATOR + calculatedYards + " " + mMileSign;
            }
            else {
                System.out.println(calculatedMiles +  " " + mMileSign);
                return calculatedMiles +  " " + mMileSign;
            }

        }
        else {
            double reference = 3.28;
            Double feet = Double.valueOf(distance) * reference;
            return String.valueOf(Math.round(feet)) + " " + mFeetSign;
        }
    }
    public static void displayStatus (String status, Context context) {
        if (status.equals(Constants.ZERO_RESULTS)) {
            Toast.makeText(context,R.string.api_zero_results,Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context,R.string.api_error,Toast.LENGTH_SHORT).show();
        }
    }

    public static String getCountryCodeFromCoordinates(Context context) {
        String countryCode = "";
        if (GlobalState.USER_LAT.equals("") || GlobalState.USER_LNG.equals("") ) {
            return Constants.RETURN_ERROR_STRING;
        }
        else {
            Geocoder geocoder = new Geocoder(context);
            try {

                List<Address> addressList = geocoder.getFromLocation(Double.valueOf(GlobalState.USER_LAT), Double.valueOf(GlobalState.USER_LNG), 1);
                countryCode = addressList.get(0).getCountryName().substring(0,2).toLowerCase();
                return countryCode;
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
        return Constants.RETURN_ERROR_STRING;
    }

    public static Double[] getCoordinatesFromAddressName(Context context, String address, Application application) {
        Geocoder geocoder = new Geocoder(context);
        Double[] coordinatesArray = new Double[5];
        Tracker gaTracker = ((BikeShopsDetector) application).getTracker(
                BikeShopsDetector.TrackerName.APP_TRACKER);;
        try {
            List<Address> coordinates = geocoder.getFromLocationName(address,1);
            if (coordinates.size() > 0) {
                coordinatesArray[0] = coordinates.get(0).getLatitude();
                coordinatesArray[1] = coordinates.get(0).getLongitude();
            }
        }
        catch (IOException e) {
            gaTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription("IOException in Utility, getCoordinatesFromAddressName")
                    .setFatal(false)
                    .build());
        }
        catch (IndexOutOfBoundsException e) {
            gaTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription("IndexOutOfBoundsException in Utility, getCoordinatesFromAddressName")
                    .setFatal(false)
                    .build());
        }
        return coordinatesArray;
    }

    public static String getAddressNameFromCoordinates (Context context, double latitude, double longitude, Application application) {
        Geocoder geocoder = new Geocoder(context);
        String streetAddress;
        Tracker gaTracker = ((BikeShopsDetector) application).getTracker(
                BikeShopsDetector.TrackerName.APP_TRACKER);;
        try {
            List<Address> address = geocoder.getFromLocation(latitude, longitude, 1);
            streetAddress =  address.get(0).getAddressLine(0);
            return streetAddress;
        }//if an error is received, we just return the error_string and inform the user that this address is unavailable
        catch (IOException e) {
            gaTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription("IOException in Utility, getAddressNameFromCoordinates")
                    .setFatal(false)
                    .build());
            return Constants.RETURN_ERROR_STRING;
        }
        catch (Exception e) {
            gaTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription("Exception in Utility, getAddressNameFromCoordinates")
                    .setFatal(false)
                    .build());
            return Constants.RETURN_ERROR_STRING;
        }

    }

    public static String getTodayFromOpeningHours (String schedule, Application application) {
        //Log.i(LOG_TAG,"Length of opening hours: " + String.valueOf(schedule.length()));
        String[] openingHours = schedule.split(Constants.HASH_SEPARATOR);
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String today = "";
        Tracker gaTracker = ((BikeShopsDetector) application).getTracker(
                BikeShopsDetector.TrackerName.APP_TRACKER);;
        try {
            for (int i = 0; i < openingHours.length; i++) {
                if (dayOfWeek == 1) {
                    today = openingHours[6];//Sunday
                } else {//rest of the week
                    if (dayOfWeek - 2 == i) today = openingHours[i];
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            gaTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription("IndexOutOfBoundsException in Utility, getTodayFromOpeningHours")
                    .setFatal(false)
                    .build());
            return ""; //the schedule is invalid
        }
        catch (Exception e) {
            gaTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription("Exception in Utility, getTodayFromOpeningHours")
                    .setFatal(false)
                    .build());
            return ""; //unknown error occurred, we don't display any schedule
        }
        return today;
    }

    public static float convertPixelsToDp(Context context, float px){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    public static int convertDpToPixels(Context context, int dp)
    {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    public static int getAutocompleteViewWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        double screenWidthFraction = 0.9;
        String viewString = String.valueOf(screenWidthFraction * displayMetrics.widthPixels);
        String viewFinal = viewString.substring(0,viewString.indexOf(Constants.DOT_SEPARATOR));
        int viewWidth = Integer.valueOf(viewFinal) ;
        return viewWidth;
    }

    public static int getAutocompleteViewHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        double screenHeightFraction = 0.12;
        String viewString = String.valueOf(screenHeightFraction * displayMetrics.widthPixels);
        String viewFinal = viewString.substring(0,viewString.indexOf(Constants.DOT_SEPARATOR));
        int viewWidth = Integer.valueOf(viewFinal) ;
        return viewWidth;
    }
    //return the screen's absolute width in pixels
    public static int getScreenWidth (Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    //used to extract the promo text for the shop detail or shop map activity
    public static String getPromoText(String promoText, int activityNumber) {
        if (activityNumber == 1) {
            return promoText.substring(0,promoText.indexOf(Constants.HASH_SEPARATOR));
        }
        else if (activityNumber == 2) {
            return promoText.substring(promoText.indexOf(Constants.HASH_SEPARATOR) + 1);
        }
        else {
            return "";
        }
    }

    //used to determine the right padding for the Like View depending on the screen density in the onResume method from the main activity
    public static int[] getLikeViewPaddingOnResume (Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int[] paddingArray = new int[4]; //left, top, right, bottom
        switch (metrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW: {
                    paddingArray[0] = 8;
                    paddingArray[1] = 0;
                    paddingArray[2] = 105;
                    paddingArray[1] = 0;
                    break;
            }
            case DisplayMetrics.DENSITY_MEDIUM: {
                    paddingArray[0] = 11;
                    paddingArray[1] = 0;
                    paddingArray[2] = 140;
                    paddingArray[1] = 0;
                    break;
            }
            case DisplayMetrics.DENSITY_HIGH: {
                    paddingArray[0] = 16;
                    paddingArray[1] = 0;
                    paddingArray[2] = 211;
                    paddingArray[1] = 0;
                    break;
            }
            case DisplayMetrics.DENSITY_XHIGH: {
                    paddingArray[0] = 21;
                    paddingArray[1] = 0;
                    paddingArray[2] = 280;
                    paddingArray[1] = 0;
                    break;
            }
            case DisplayMetrics.DENSITY_XXHIGH: {
                    paddingArray[0] = 32;
                    paddingArray[1] = 0;
                    paddingArray[2] = 422;
                    paddingArray[1] = 0;
                    break;
            }
            default: { //we return the density_high values by default
                    paddingArray[0] = 16;
                    paddingArray[1] = 0;
                    paddingArray[2] = 211;
                    paddingArray[1] = 0;
                    break;
            }
        }
        return paddingArray;
    }

    //used in the onActivityResult method from the Main activity to determine the right padding based on the screen density
    public static int getPaddingRightLikeView(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        final int LOW_PADDING = 110;
        final int MEDIUM_PADDING = 146;
        final int HIGH_PADDING = 220;
        final int XHIGH_PADDING = 293;
        final int XXHIGH_PADDING = 440;
        switch (metrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW: {
                return LOW_PADDING;
            }
            case DisplayMetrics.DENSITY_MEDIUM: {
                return MEDIUM_PADDING;
            }
            case DisplayMetrics.DENSITY_HIGH: {
                return HIGH_PADDING;
            }
            case DisplayMetrics.DENSITY_XHIGH: {
                return XHIGH_PADDING;
            }
            case DisplayMetrics.DENSITY_XXHIGH: {
                return XXHIGH_PADDING;
            }
            default: return HIGH_PADDING;
        }
    }

    public static void sendNotification(String title, String body, String uri, Context context) {
        Random generator = new Random();
        long[] vibratePattern = {0,200,1000};//[ms] delay, duration, sleep
        int notificationID = 11;//we need a new notification each time
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        //  Ringtone r = RingtoneManager.getRingtone(context, notificationSound);
        if(notificationSound == null){ //in case there's no alarm/ringtone sound set
            notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if(notificationSound == null){
                notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            else {
                notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
            }
        }

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher) //icon of notification
                        .setAutoCancel(true) //eliminates the notification from the status bar
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                        .setSound(notificationSound) //play the sound
                        .setVibrate(vibratePattern)
                        .setContentTitle(title) //title of notification
                        .setContentText(body)
                        ; //text of notification

        //determine whether we should open a website
        if (uri.equals("")) {//no website to open
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
            notificationBuilder.setContentIntent(resultPendingIntent);
        }
        else {
            // open the URI
            Intent resultIntent = new Intent(Intent.ACTION_VIEW);
            resultIntent.setData(Uri.parse(uri));
            PendingIntent pending = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pending);
        }
        NotificationManager notificationManager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
        //  r.play();
        notificationManager.notify(notificationID, notificationBuilder.build());
    }
}
