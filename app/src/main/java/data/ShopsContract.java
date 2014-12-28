package data;

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
        public static final String COLUMN_SHOP_LATITUDE="shop_latitude";//real
        public static final String COLUMN_SHOP_LONGITUDE="shop_longitude";//real
        public static final String COLUMN_IS_OPEN="is_open";//integer (1 or 0, SQLite does not support boolean types
        public static final String COLUMN_DISTANCE_TO_USER="distance_to_user";//in meters, stored as integer
        public static final String COLUMN_DISTANCE_DURATION="distance_duration";//in minutes, stored as integer
    }
}
