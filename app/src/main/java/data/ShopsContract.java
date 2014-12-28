package data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Narcis11 on 28.12.2014.
 * Class used to define the table and column names. Note: The _ID column is by default added by the SQLite database engine.
 */
public class ShopsContract {

    public static final class ShopsEntry implements BaseColumns {
        public static final String TABLE_NAME="shops";
        public static final String COLUMN_SHOP_NAME="shop_name";//text
        public static final String COLUMN_SHOP_ADDRESS="shop_address";//text
        public static final String COLUMN_SHOP_LATITUDE="shop_latitude";//text
        public static final String COLUMN_SHOP_LONGITUDE="shop_longitude";//text
        public static final String COLUMN_IS_OPEN="is_open";//integer (1 or 0, SQLite does not support boolean types
        public static final String COLUMN_DISTANCE_TO_USER="distance_to_user";//in meters, stored as integer
        public static final String COLUMN_DISTANCE_DURATION="distance_duration";//in minutes, stored as integer

    //this part is used for the Content Provider
        //the name of the content provider (named as the package of the app)
        public static final String CONTENT_AUTHORITY = "waldo.bike.waldo";
        // Use CONTENT_AUTHORITY to create the base of all URIs which apps will use to contact
        // the content provider. All URIs must begin with "content://"
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
        //used for pointing to the shops table
        public static final String PATH_SHOPS = "shops";
    }
}
