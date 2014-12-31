package data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Narcis11 on 28.12.2014.
 */
public class ShopsProvider extends ContentProvider {

    private ShopsDbHelper mShopsHelper;
    final String LOG_TAG = ShopsProvider.class.getSimpleName();
    @Override
    public boolean onCreate() {
        mShopsHelper = new ShopsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor shopsCursor;
        shopsCursor = mShopsHelper.getReadableDatabase().query(
                ShopsContract.ShopsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                ShopsContract.ShopsEntry.SORT_ORDER
        );
        //Tells the cursor to register a Content Observer to watch for changes that appear in that uri or any of its descendants
        //descendants = uris that are like 'uri%'
        shopsCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return shopsCursor;
    }

    @Override
    public String getType(Uri uri) {
        //We need all the data from the table, so we return the list item (directory)
        return ShopsContract.ShopsEntry.CONTENT_TYPE;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase sqLiteDatabase = mShopsHelper.getWritableDatabase();
        Uri returnUri;
        long _id = sqLiteDatabase.insert(ShopsContract.ShopsEntry.TABLE_NAME,null,values);
        if (_id > 0 ) {
            returnUri = ShopsContract.ShopsEntry.buildShopsUri(_id);
        }
        else {
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return  returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = mShopsHelper.getWritableDatabase();
        int rowsDeleted;
        rowsDeleted = sqLiteDatabase.delete(ShopsContract.ShopsEntry.TABLE_NAME,selection,selectionArgs);
        //a null selection deletes all rows
        //we only notify if rows were indeed deleted
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = mShopsHelper.getWritableDatabase();
        int rowsUpdated;
        rowsUpdated = sqLiteDatabase.update(ShopsContract.ShopsEntry.TABLE_NAME,values,selection,selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }
    //much faster than the regular method, because all of the inserts are done in a single transaction;
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase sqLiteDatabase = mShopsHelper.getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                long _id = sqLiteDatabase.insert(ShopsContract.ShopsEntry.TABLE_NAME,null,value);
                if (_id != -1 ){
                    returnCount++;
                }
            }
            sqLiteDatabase.setTransactionSuccessful();
        }
        catch (IllegalStateException e) {
              Log.e(LOG_TAG, "Error while inserting bulk! = " + e.getMessage());
              e.printStackTrace();
              return super.bulkInsert(uri, values);//in case of errors, we call the regular insert method
        }
        finally {
            sqLiteDatabase.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnCount;
    }
}
