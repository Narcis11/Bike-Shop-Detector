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

    public static boolean getPreferredNotification(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        return prefs.getBoolean(displayNotificationsKey,
               Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));
    }
    //used for determining the radius used in the Nearby Search. Returns 10.000 if no range is selected
    public static String formatPreferredRange(Context context) {
        String range = getPreferredRange(context);
        final String oneKilometer = "1 km";
        final String twoKilometers = "2 km";
        final String threeKilometers = "3 km";
        final String fourKilometers = "4 km";
        final String fiveKilometers = "5 km";
        final String tenKilometers = "10 km";
        switch (range) {
            case oneKilometer: {
                return "1000";
            }
            case twoKilometers: {
                return "2000";
            }
            case threeKilometers: {
                return "3000";
            }
            case fourKilometers:{
                return "4000";
            }
            case fiveKilometers:{
                return "5000";
            }
            case tenKilometers:{
                return "10000";
            }
            default: return "10000";

        }
    }

    public static String[] getLatLng (String location) {
        String[] latLng = new String[2];
        String latitudeReference = "mLatitude=";
        String longitudeReference = "mLongitude=";
        String altitudeReference = "mHasAltitude";
        latLng[0] = location.substring(location.indexOf(latitudeReference) + latitudeReference.length(), location.indexOf(longitudeReference) - 1);
        latLng[1] = location.substring(location.indexOf(longitudeReference) + longitudeReference.length(), location.indexOf(altitudeReference) - 1);
        return latLng;
    }
}
