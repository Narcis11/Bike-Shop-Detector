package test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.ShopsContract;
import data.ShopsDbHelper;

/**
 * Created by Narcis11 on 28.12.2014.
 * Used to test the database with sql-specific methods.
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
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_NAME,"De ziua neveste-mii");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS,"Strada Pierzaniei");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE,"44.777777");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE,"26.123456");
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_IS_OPEN,0);
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER,20000);
        insertValues.put(ShopsContract.ShopsEntry.COLUMN_DISTANCE_DURATION,50);
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
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + tableName + " order by " + ShopsContract.ShopsEntry.SORT_ORDER +" ;", null);
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
                columnValue =  columnValue + " " + cursor.getString(cursor.getColumnIndex(columnNames.get(i))) + ",";
            }

        }
        else {
            Log.i(LOG_TAG, "Nu exista inregistrari in tabela " + tableName);
        }

        if (!columnValue.equals("")) {
            Log.i(LOG_TAG, "Randul din " + tableName + " este: " + columnValue);
        }
        cursor.close();
        sqLiteDatabase.close();
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}
