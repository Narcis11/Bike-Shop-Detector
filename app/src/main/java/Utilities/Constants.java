package Utilities;

/**
 * Created by Narcis11 on 07.12.2014.
 */
public class Constants {

    //statuses
    public static final String NETWORK_STATE_CONNECTED="CONNECTED";
    public static final String NETWORK_STATE_DISCONNECTED="DISCONNECTED";
    public static final String OK_STATUS = "OK";
    public static final String ZERO_RESULTS = "ZERO_RESULTS";
    public static final String NOT_AVAILABLE="NOT AVAILABLE";
    public static final String RETURN_ERROR_STRING = "ERROR";

    //separators of the result string from the Places API call
    public static final String PIPE_SEPARATOR = "|";
    public static final String SLASH_SEPARATOR = "/";
    public static final String COMMA_SEPARATOR = ",";
    public static final String HASH_SEPARATOR = "#";
    public static final String DOLLAR_SEPARATOR = "$";
    public static final String DOT_SEPARATOR = ".";

    //keys used in bundle communication
    public static final String BUNDLE_SHOP_LAT = "bundle_shop_lat_key";
    public static final String BUNDLE_SHOP_LNG = "bundle_shop_lng_key";
    public static final String BUNDLE_SHOP_NAME = "bundle_shop_name_key";
    public static final String BUNDLE_SHOP_PLACE_ID = "bundle_shop_placeid_key";
    public static final String BUNDLE_IS_PARTNER = "bundle_is_partner";
    public static final String BUNDLE_PROMO_TEXT = "bundle_promo_text";
    public static final String BUNDLE_WEBSITE = "bundle_website";
    public static final String BUNDLE_WEBVIEW_TITLE = "bundle_webview_title";
    public static final String BUNDLE_SHOP_CAMERA_BEARING = "bundle_camera_bearing";
    public static final String BUNDLE_SHOP_CAMERA_TILT = "bundle_camera_tilt";
    public static final String BUNDLE_SHOP_CAMERA_ZOOM = "bundle_camera_zoom";
    public static final String BUNDLE_SHOP_CAMERA_POSITION = "bundle_camera_position";
    public static final String BUNDLE_USER_LAT = "bundle_user_lat_key";
    public static final String BUNDLE_USER_LNG = "bundle_user_lng_key";
    public static final String BUNDLE_FRAGMENT = "fragment_key";
    public static final String BUNDLE_ACTIVITY = "activity_key";
    public static final String CALLED_FROM_FRAGMENT = "fragment_call";
    public static final String CALLED_FROM_MAIN_ACTIVITY = "activity_call";
    public static final String ADD_SHOP_BUNDLE_LAT_KEY = "lat_key";
    public static final String ADD_SHOP_BUNDLE_LNG_KEY = "lng_key";
    public static final String ADD_SHOP_BUNDLE_ADDRESS_KEY = "address_key";
    //the bundle used for the sync adapter receiver
    public static final String SYNC_BUNDLE_STATUS_KEY = "sync_status";
    public static final String SYNC_BUNDLE_STATUS_STOPPED = "stopped";
    public static final String SYNC_BUNDLE_STATUS_ZERO = "ZERO_RESULTS";
    public static final String SYNC_BUNDLE_STATUS_ACTION = "sync_receiver";
    public static final String SYNC_BUNDLE_RESULT_KEY = "sync_result";
    //used in the Maps Activity
    public static final float  CITY_ZOOM = 11;
    public static final float  SHOP_ZOOM = 18;
    public static final float  USER_SHOP_ZOOM = 13; //used when we have the shop's marker and the user's marker on the map
    public static final String USERS_NAME = "My location";
    public static final String API_KEY="AIzaSyCHf4BobJQWDa39E_VJtUkwkuimFfsz7Z0";
    public static final String NEW_SHOP_NAME = "New shop's location";

    //used in network communication
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";
    public static final String PROVIDER = "waldoprovider";
    public static final String PLACE_TYPE = "bicycle_store";

    //used in the list view from the main activity
    public static final String SHOP_OPEN = "Open";
    public static final String SHOP_CLOSED = "Closed";
    public static final String SHOP_UNAVAILABLE = "";

    //used by the activities that listen to network changes
    public static final String BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    //used for the Twitter authentification
    public static final String CONSUMER_KEY = "amGg88FlHOkWJwFdxcZuwNCNd";
    public static final String CONSUMER_SECRET = "npKv9jr06nWfCCNNbixntIxeZmcuLgQSlebZOYJ82jytuLWHrl";
    //Used in the Twitter Async Task
    public static final String TWITTER_FOLLOW = "follow";
    public static final String TWITTER_UNFOLLOW = "unfollow";
    //the channel used for this app
    public static final String PARSE_PUSH_CHANNEL = "";
}
