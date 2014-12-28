package test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import data.ShopsContract;
import data.ShopsDbHelper;

/**
 * Created by Narcis11 on 28.12.2014.
 */
public class ShopsTest extends AndroidTestCase {
    final static String LOG_TAG = ShopsTest.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(ShopsDbHelper.DATABASE_NAME);
        SQLiteDatabase sqLiteDatabase = new ShopsDbHelper(mContext).getReadableDatabase();
        assertEquals(sqLiteDatabase.isOpen(), true);
        sqLiteDatabase.close();
    }

    public void testInsertDb() throws Throwable {
        SQLiteDatabase sqLiteDatabase = new ShopsDbHelper(mContext).getWritableDatabase();
        ContentValues insertValues = new ContentValues();
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_NAME,"Bicle La gica");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS,"Strada Matache de la Playa");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE,"44.4360611");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE,"26.1227012");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_IS_OPEN,1);
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER,1457);
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_DISTANCE_DURATION,13);
        long positionID = sqLiteDatabase.insert(ShopsContract.ShopsEntry.TABLE_NAME,null,insertValues);
        assertTrue( positionID != -1);
        Log.i(LOG_TAG, "Inserted row id is " + positionID);
        sqLiteDatabase.close();
    }

    public void testUpdateDb() throws Throwable {
        SQLiteDatabase sqLiteDatabase = new ShopsDbHelper(mContext).getWritableDatabase();
        ContentValues updateValues = new ContentValues();
        updateValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE,"42.45801");
        updateValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE,"22.45000");
        sqLiteDatabase.update(ShopsContract.ShopsEntry.TABLE_NAME, updateValues, "_id = 1", null);
        sqLiteDatabase.close();
    }


    public void testReadDb() {
        SQLiteDatabase sqLiteDatabase = new ShopsDbHelper(mContext).getWritableDatabase();
        String tableName = ShopsContract.ShopsEntry.TABLE_NAME;
        String columnValue = "";
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + tableName + " order by _id desc;", null);
        List<String> columnNames = new ArrayList<String>();
        columnNames.add(ShopsContract.ShopsEntry._ID);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_NAME);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_IS_OPEN);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER);
        columnNames.add(ShopsContract.ShopsEntry.COLUMN_DISTANCE_DURATION);
        if (cursor.moveToFirst()) {

            for (int i = 0; i < columnNames.size(); i++) {
                columnValue =  columnValue + " " + cursor.getString(cursor.getColumnIndex(columnNames.get(i))) + ",";//

            }

        }
        else {
            Log.i(LOG_TAG, "Nu exista inregistrari in tabela " + tableName );
        }

        if (!columnValue.equals("")) {
            Log.i(LOG_TAG, "Randul din " + tableName + " este: " + columnValue);
        }
        cursor.close();
        sqLiteDatabase.close();
    }
}
