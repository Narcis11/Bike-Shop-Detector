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
    public static final String CONTENT_AUTHORITY = "waldo.bike.waldo";
    // Use CONTENT_AUTHORITY to create the base of all URIs which apps will use to contact
    // the content provider. All URIs must begin with "content://"
    //content://waldo.bike.waldo
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //used for pointing to the shops table
    public static final String PATH_SHOPS = "shops";

    public static final class ShopsEntry implements BaseColumns {
        public static final String TABLE_NAME="shops";
        public static final String COLUMN_SHOP_NAME="shop_name";//text
        public static final String COLUMN_SHOP_ADDRESS="shop_address";//text
        public static final String COLUMN_SHOP_LATITUDE="shop_latitude";//text
        public static final String COLUMN_SHOP_LONGITUDE="shop_longitude";//text
        public static final String COLUMN_IS_OPEN="is_open";//integer (1 or 0, SQLite does not support boolean types
        public static final String COLUMN_DISTANCE_TO_USER="distance_to_user";//in meters, stored as integer
        public static final String COLUMN_DISTANCE_DURATION="distance_duration";//in minutes, stored as integer

    //some coding for the Content Provider
        //content://waldo.bike.waldo/shops
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHOPS).build();
        //these two indicate if the URI returns a list of items (directory) or an item
        //vnd.android.cursor.dir/waldo.bike.waldo/shops
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_SHOPS;
        //vnd.android.cursor.item/waldo.bike.waldo/shops
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_SHOPS;
        //used in the query method from Shops Provider
        public static final String SORT_ORDER = COLUMN_DISTANCE_TO_USER + " ASC";
        //used for querying a single row
        public static Uri buildShopsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }
}
