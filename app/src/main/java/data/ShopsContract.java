package data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Narcis11 on 28.12.2014.
 * Class used to define the table and column names. Note: The _ID column is by default added by the SQLite database engine.
 */
public class ShopsContract {

    //this variables are used for the Content Provider

    //the name of the content provider (named as the package of the app)
    public static final String CONTENT_AUTHORITY = "waldo.bike.bikeshops";
    // Use CONTENT_AUTHORITY to create the base of all URIs which apps will use to contact
    // the content provider. All URIs must begin with "content://"
    //content://waldo.bike.waldo
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //used for pointing to the shops table
    public static final String PATH_SHOPS = "shops";

    public static final class ShopsEntry implements BaseColumns {
        public static final String TABLE_NAME = "shops";
        public static final String COLUMN_SHOP_NAME = "shop_name";//text
        public static final String COLUMN_SHOP_ADDRESS = "shop_address";//text
        public static final String COLUMN_SHOP_LATITUDE = "shop_latitude";//text
        public static final String COLUMN_SHOP_LONGITUDE = "shop_longitude";//text
        public static final String COLUMN_IS_OPEN = "is_open";//integer (1 or 0, SQLite does not support boolean types
        public static final String COLUMN_DISTANCE_TO_USER = "distance_to_user";//in meters, stored as integer
        public static final String COLUMN_DISTANCE_DURATION = "distance_duration";//in minutes, stored as integer
        public static final String COLUMN_PLACE_ID = "place_id";//id of each place, stored as text
        public static final String COLUMN_IS_PARTNER = "is_partner";//if we have a partnership with the bikeshop, stored as INTEGER (1 or 0)
        public static final String COLUMN_DISCOUNT_VALUE = "discount_value";//the current discount they now have for certain products, stored as integer
        public static final String COLUMN_LOGO_URL = "logo_url";//the url where we can find the shop's, stored as TEXT
        public static final String COLUMN_WEBSITE = "website";//the shop's website, stored as TEXT
        public static final String COLUMN_PHONE_NUMBER = "phone_number";//the shop's phone number, stored as TEXT
        public static final String COLUMN_OPENING_HOURS = "opening_hours";//the opening hours of the shop with info for each day. stored as TEXT, each day is divided by "|"
        public static final String COLUMN_RATING = "rating";//the shop's rating, stored as REAL
        public static final String COLUMN_SHOP_PROMO_TEXT = "promo_text";//the promotional text, stored as TEXT
        public static final String COLUMN_SHOP_CAMERA_BEARING = "camera_bearing";//the streetview camera's bearing, stored as REAL
        public static final String COLUMN_SHOP_CAMERA_TILT = "camera_tilt";//the streetview camera's tilt, stored as REAL
        public static final String COLUMN_SHOP_CAMERA_ZOOM = "camera_zoom";//the streetview camera's zoom, stored as REAL
        public static final String COLUMN_SHOP_CAMERA_POSITION = "camera_position";//the streetview camera's position, stored as String
        //some coding for the Content Provider
        //content://waldo.bike.bikeshops/shops
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHOPS).build();
        //these two indicate if the URI returns a list of items (directory) or an item
        //vnd.android.cursor.dir/waldo.bike.bikeshops/shops
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_SHOPS;
        //vnd.android.cursor.item/waldo.bike.bikeshops/shops
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_SHOPS;
        //used in the query method from Shops Provider
        public static final String SORT_ORDER = COLUMN_DISTANCE_TO_USER + " ASC";
        //used for manipulating a single row
        public static Uri buildShopsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }
}
