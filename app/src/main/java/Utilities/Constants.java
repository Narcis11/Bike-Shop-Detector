package Utilities;

/**
 * Created by Narcis11 on 07.12.2014.
 */
public class Constants {
    public static final String PREVIOUS_STATE_CONNECTED="CONNECTED";
    public static final String PREVIOUS_STATE_DISCONNECTED="DISCONNECTED";
    public static final String API_KEY="AIzaSyA2rM5Kq8ch0L-N0nALu9xx7O4GYgtMjow";
    public static final String NOT_AVAILABLE="NOT AVAILABLE";

    //separators of the result string from the API call
    public static final String PIPE_SEPARATOR = "|";
    public static final String SLASH_SEPARATOR = "/";
    public static final String COMMA_SEPARATOR = ",";
    public static final String HASH_SEPARATOR = "#";

    //keys used in bundle communication
    public static final String BUNDLE_SHOP_LAT = "bundle_shop_lat_key";
    public static final String BUNDLE_SHOP_LNG = "bundle_shop_lng_key";
    public static final String BUNDLE_SHOP_NAME = "bundle_shop_name_key";
    public static final String BUNDLE_USER_LAT = "bundle_user_lat_key";
    public static final String BUNDLE_USER_LNG = "bundle_user_lng_key";
    public static final String BUNDLE_FRAGMENT = "fragment_key";
    public static final String BUNDLE_ACTIVITY = "activity_key";
    public static final String CALLED_FROM_FRAGMENT = "fragment_call";
    public static final String CALLED_FROM_MAIN_ACTIVITY = "activity_call";

    //used in the Maps Activity
    public static final float  CITY_ZOOM = 12;
    public static final float  USER_SHOP_ZOOM = 13; //used when we have the shop's marker and the user's marker on the map
    public static final String USERS_NAME = "My location";
}
