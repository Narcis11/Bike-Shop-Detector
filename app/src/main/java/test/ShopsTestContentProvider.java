package test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import data.ShopsContract;
import data.ShopsDbHelper;

/**
 * Created by Narcis11 on 28.12.2014.
 * Used to test the database with ContentProvider-specific methods.
 */
public class ShopsTestContentProvider extends AndroidTestCase {
    final static String LOG_TAG = ShopsTestContentProvider.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(ShopsDbHelper.DATABASE_NAME);
        SQLiteDatabase sqLiteDatabase = new ShopsDbHelper(mContext).getReadableDatabase();
        assertEquals(sqLiteDatabase.isOpen(), true);
        sqLiteDatabase.close();
    }

    public void testInsertContentProvider() throws Throwable {
        ContentValues insertValues = new ContentValues();
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_NAME,"Bicle La gica");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS,"Strada Matache de la Playa");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE,"44.4360611");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE,"26.1227012");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_IS_OPEN,1);
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER,1100);
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_DISTANCE_DURATION,10);
        Uri insertUri = mContext.getContentResolver().insert(ShopsContract.ShopsEntry.CONTENT_URI,insertValues);
        long positionId = ContentUris.parseId(insertUri);
        Log.i(LOG_TAG,"Insert uri is: " + insertUri);
        Log.i(LOG_TAG,"Row number inserted = " + positionId);
        assertTrue( positionId != -1);
    }

    public void testDeleteProvider() throws Throwable {
        String whereClause = ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER + "<1200";
        int deletedRows = mContext.getContentResolver().delete(ShopsContract.ShopsEntry.CONTENT_URI,whereClause,null);
        Log.i(LOG_TAG,"No of rows deleted = " + deletedRows);
    }

    public void testUpdateDb() throws Throwable {
        SQLiteDatabase sqLiteDatabase = new ShopsDbHelper(mContext).getWritableDatabase();
        ContentValues updateValues = new ContentValues();
        updateValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE,"42.45801");
        updateValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE,"22.45000");
        sqLiteDatabase.update(ShopsContract.ShopsEntry.TABLE_NAME, updateValues, "_id = 1", null);
        sqLiteDatabase.close();
    }


    public void testReadContentProvider() {
        String columnValue = "";
        Cursor contentCursor = mContext.getContentResolver().query(
                ShopsContract.ShopsEntry.CONTENT_URI,
                null,
                null,
                null,
                ShopsContract.ShopsEntry.SORT_ORDER
        );
        List<String> columnNames = new ArrayList<String>();
        columnNames.add(ShopsContract.ShopsEntry._ID);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_NAME);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_IS_OPEN);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_DISTANCE_DURATION);
   //     ShopsTest.validateCursor(contentCursor,columnNames);

        if (contentCursor.moveToFirst()) {
            for (int i = 0; i < columnNames.size(); i++) {
                columnValue =  columnValue + " " + contentCursor.getString(contentCursor.getColumnIndex(columnNames.get(i))) + ",";
            }
        }
        else {
            Log.i(LOG_TAG, "Nu exista inregistrari in tabela shops.");
        }
        if (!columnValue.equals("")) {
            Log.i(LOG_TAG, "Randul din shops este: " + columnValue);
        }
        contentCursor.close();
    }
}
