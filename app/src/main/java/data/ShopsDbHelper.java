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
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "shops.db";
    private final String LOG_TAG = ShopsDbHelper.class.getSimpleName();
    public ShopsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SHOPS_TABLE = "CREATE TABLE " + ShopsContract.ShopsEntry.TABLE_NAME + " (" +
                ShopsContract.ShopsEntry._ID + " INTEGER PRIMARY KEY, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_NAME + " TEXT NOT NULL, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS + " TEXT, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE + " REAL NOT NULL, " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE + "REAL NOT NULL, " +
                ShopsContract.ShopsEntry.COLUMN_IS_OPEN + " INTEGER, " +
                ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER + " INTEGER, " +
                ShopsContract.ShopsEntry.COLUMN_DISTANCE_DURATION + " INTEGER, " +
                " NOT NULL (" + ShopsContract.ShopsEntry.COLUMN_SHOP_NAME + ", " + ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE + ", " +
                ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE + ") ON CONFLICT IGNORE);";
        Log.i(LOG_TAG,"Create statement is " + SQL_CREATE_SHOPS_TABLE);
        db.execSQL(SQL_CREATE_SHOPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This method is called when the database schema needs to be modified. As we use our database to store data from a server, and not user-generated one,
        //we simply drop the table and recreate the database.
        final String DROP_TABLE_COMMAND = "DROP TABLE IF EXISTS" + ShopsContract.ShopsEntry.TABLE_NAME;
        db.execSQL(DROP_TABLE_COMMAND);
        onCreate(db);
    }
}
