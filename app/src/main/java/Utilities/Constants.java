package Utilities;

/**
 * Created by Narcis11 on 07.12.2014.
 */
public class Constants {

    //statuses
    public static final String PREVIOUS_STATE_CONNECTED="CONNECTED";
    public static final String PREVIOUS_STATE_DISCONNECTED="DISCONNECTED";
    public static final String OK_STATUS = "OK";
    public static final String ZERO_RESULTS = "ZERO_RESULTS";
    public static final String NOT_AVAILABLE="NOT AVAILABLE";

    //separators of the result string from the API call
    public static final String PIPE_SEPARATOR = "|";
    public static final String SLASH_SEPARATOR = "/";
    public static final String COMMA_SEPARATOR = ",";
    public static final String HASH_SEPARATOR = "#";
    public static final String DOLLAR_SEPARATOR = "$";

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
    public static final float  CITY_ZOOM = 11;
    public static final float  SHOP_ZOOM = 18;
    public static final float  USER_SHOP_ZOOM = 13; //used when we have the shop's marker and the user's marker on the map
    public static final String USERS_NAME = "My location";
    public static final String API_KEY="AIzaSyCHf4BobJQWDa39E_VJtUkwkuimFfsz7Z0";

    //used in network communication
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final String PROVIDER = "waldoprovider";

    //used in the list view from the main activity
    public static final String SHOP_OPEN = "Open";
    public static final String SHOP_CLOSED = "Closed";
    public static final String SHOP_UNAVAILABLE = "";

    //used in the AddShopMap
    public static final String ADD_SHOP_TITLE = "New shop title";

    //Colours
    public static final String COLOR_BLUE_WALDO_DARK = "#1e85fb";

    //used in every method that returns a String
    public static final String RETURN_ERROR_STRING = "ERROR";


}
