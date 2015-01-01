package Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Display;

import waldo.bike.waldo.R;

/**
 * Created by nmihai on 15.12.2014.
 */
public class Utility {

        public static String getPreferredRange(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            return prefs.getString(context.getString(R.string.pref_range_key),
                    context.getString(R.string.pref_range_default));
        }

    public static String getPreferredUnit(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_unit_key),
                context.getString(R.string.pref_unit_default));
    }

    public static String getPrefferedSpeed (Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_speed_key),
                context.getString(R.string.pref_speed_default));
    }

    public static boolean getPreferredNotification(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        return prefs.getBoolean(displayNotificationsKey,
               Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));
    }

    public static int formatPreferredSpeedMetric (Context context) {
        String speed = getPrefferedSpeed(context);
        final String fourteenKm = "14 km";
        final String seventeenKm = "17 km";
        final String twentyKm = "20 km";
        final String twentyThreeKm = "23 km";
        final String twentySixKm = "26 km";
        final String twentyNineKm = "29 km";
        final String thirtyTwoKm = "32 km";
        final int returnFourteen = 14000;
        final int returnSeventeen = 17000;
        final int returnTwenty = 20000;
        final int returnTwentyThree = 23000;
        final int returnTwentySix = 26000;
        final int returnTwentyNine = 29000;
        final int returnThirtyTwo = 32000;
        switch (speed) {
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
    //used for determining the radius used in the Nearby Search. Returns 10.000 if no range is selected
    public static String formatPreferredRange(Context context) {
        String range = getPreferredRange(context);
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

    public static float calculateDistanceDuration (int distanceToShop, Context context) {
        int formattedSpeed = formatPreferredSpeedMetric(context);
        return (distanceToShop * 60)/formattedSpeed;
    }
}
