package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Narcis11 on 28.12.2014.
 */
public class ShopsDbHelper extends SQLiteOpenHelper {

    // If we change the database schema in the future, we must increment the database version.
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "shops.db";
    private final String LOG_TAG = ShopsDbHelper.class.getSimpleName();
    public ShopsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SHOPS_TABLE = "CREATE TABLE " + ShopsContract.ShopsEntry.TABLE_NAME + " (" +
                ShopsContract.ShopsEntry._ID + " INTEGER PRIMARY KEY, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_NAME + " TEXT, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS + " TEXT, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE + " TEXT, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE + " TEXT, " +
                ShopsContract.ShopsEntry.COLUMN_IS_OPEN + " INTEGER, " +
                ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER + " INTEGER, " +
                ShopsContract.ShopsEntry.COLUMN_DISTANCE_DURATION + " REAL, "  +
                ShopsContract.ShopsEntry.COLUMN_PLACE_ID + " TEXT, " +
                ShopsContract.ShopsEntry.COLUMN_IS_PARTNER + " INTEGER, " +
                ShopsContract.ShopsEntry.COLUMN_DISCOUNT_VALUE + " INTEGER, " +
                ShopsContract.ShopsEntry.COLUMN_LOGO_URL + " TEXT, " +
                ShopsContract.ShopsEntry.COLUMN_WEBSITE + " TEXT, " +
                ShopsContract.ShopsEntry.COLUMN_PHONE_NUMBER + " TEXT, " +
                ShopsContract.ShopsEntry.COLUMN_OPENING_HOURS + " TEXT, " +
                ShopsContract.ShopsEntry.COLUMN_RATING + " REAL, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_PROMO_TEXT + " TEXT, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_CAMERA_BEARING + " REAL, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_CAMERA_TILT + " REAL, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_CAMERA_ZOOM + " REAL, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_CAMERA_POSITION + " TEXT " +
                ");";
/*                " NOT NULL (" + ShopsContract.ShopsEntry.COLUMN_SHOP_NAME + ", " + ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE + ", " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE + ") ON CONFLICT IGNORE);";*/
        db.execSQL(SQL_CREATE_SHOPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This method is called when the database schema needs to be modified. As we use our database to store data from a server, and not user-generated one,
        //we simply drop the table and recreate the database.
        final String DROP_TABLE_COMMAND = "DROP TABLE IF EXISTS " + ShopsContract.ShopsEntry.TABLE_NAME;
        db.execSQL(DROP_TABLE_COMMAND);
        onCreate(db);
    }
}
