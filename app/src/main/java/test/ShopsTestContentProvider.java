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
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_PLACE_ID,"fdafjdasofda423423");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_IS_PARTNER,1);
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_DISCOUNT_VALUE,12);
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_WEBSITE,"www.test.ro");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_PHONE_NUMBER,"021.321.38.14");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_RATING,3.3);
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_PROMO_TEXT,"Cel mai şmecheros magazin din lume!#");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_CAMERA_BEARING, 120);
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_CAMERA_TILT,20);
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_CAMERA_ZOOM,4);
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_CAMERA_POSITION,"44.4378258,26.0946376");
        Uri insertUri = mContext.getContentResolver().insert(ShopsContract.ShopsEntry.CONTENT_URI,insertValues);
        long positionId = ContentUris.parseId(insertUri);
        Log.i(LOG_TAG,"Insert uri is: " + insertUri);
        Log.i(LOG_TAG,"Row number inserted = " + positionId);
        assertTrue( positionId != -1);
    }

    public void testDeleteProvider() throws Throwable {
        String whereClause = ShopsContract.ShopsEntry.COLUMN_WEBSITE + "='www.test.ro'";
        int deletedRows = mContext.getContentResolver().delete(ShopsContract.ShopsEntry.CONTENT_URI,null,null);
        Log.i(LOG_TAG,"No of rows deleted = " + deletedRows);
    }

    public void testUpdateContentProvider() throws Throwable {
        ContentValues updateValues = new ContentValues();
        updateValues.put(ShopsContract.ShopsEntry.COLUMN_LOGO_URL, "https://edinburghcriticalmass.files.wordpress.com/2012/11/bike-to-work.gif?w=68&h=68&crop=1");
        String placeID = " = 'ChIJcd1nibH4sUARnqDjRwzgiMA' ";
        String whereClause = ShopsContract.ShopsEntry.COLUMN_PLACE_ID + placeID ;
       // String whereClause = ShopsContract.ShopsEntry.COLUMN_WEBSITE + " IS NOT NULL" ;
        int updatedRows = mContext.getContentResolver().update(ShopsContract.ShopsEntry.CONTENT_URI,updateValues,whereClause,null);
        Log.i(LOG_TAG,"No of rows updated = " + updatedRows);
    }


    public void testReadContentProvider() {
        String columnValue = "";
        final String querySelection = ShopsContract.ShopsEntry.COLUMN_PLACE_ID + "=?";
        final String[] querySelectionArgs = new String[1];
        querySelectionArgs[0] = "ChIJcd1nibH4sUARnqDjRwzgiMA";
        Cursor contentCursor = mContext.getContentResolver().query(
                ShopsContract.ShopsEntry.CONTENT_URI,
                null,
                querySelection,
                querySelectionArgs,
                null
        );
        List<String> columnNames = new ArrayList<String>();
        columnNames.add(ShopsContract.ShopsEntry._ID);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_NAME);
      /*  columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_IS_OPEN);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_DISTANCE_DURATION);*/
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_IS_PARTNER);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_PROMO_TEXT);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_LOGO_URL);
/*        columnNames.add(ShopsContract.ShopsEntry.COLUMN_PLACE_ID);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_IS_PARTNER);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_PHONE_NUMBER);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_RATING);*/
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
