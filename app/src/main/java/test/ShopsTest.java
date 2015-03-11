package test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.test.AndroidTestCase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Utilities.GlobalState;
import Utilities.Utility;
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

    public void testCountRowsDb() {
        SQLiteDatabase sqLiteDatabase = new ShopsDbHelper(mContext).getWritableDatabase();
        String tableName = ShopsContract.ShopsEntry.TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + tableName + " where " + ShopsContract.ShopsEntry.COLUMN_IS_PARTNER + " = 1;", null);
        int noOfRows = cursor.getCount();
        Log.i(LOG_TAG,"No of rows in the shops table = " + noOfRows);
        cursor.close();
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

    public void testMetricDistanceToUser() {
        String distance = "";
       for (int i = 0; i < 15.000; i ++) {
            distance = String.valueOf(i);
            try {
                Utility.formatDistanceMetric(distance);
            }
            catch (Exception e) {
                Log.i(LOG_TAG,"Error for: " + distance);
                e.printStackTrace();
            }
        }

    }

    public void testImperialDistanceToUser() {
        String distance = "";
        for (int i = 0; i < 15000;i++) {
            distance = String.valueOf(i);
             try {
                //Log.i(LOG_TAG,distance);
                Utility.formatDistanceImperial(distance);
            }
            catch (Exception e) {
                Log.i(LOG_TAG,"Error for: " + distance);
                e.printStackTrace();
            }
        }
       // Utility.formatDistanceImperial(String.valueOf(1932));
    }

    public void testCountryCode() {
        HashMap hm = new HashMap();
        Geocoder geocoder = new Geocoder(getContext()); //50.854975,4.3753899
        String LAT_KEY;
        String LNG_KEY;

            hm.put("lat0", Double.valueOf(52.3747157));//amsterdam
            hm.put("lng0", Double.valueOf(4.8986142));
            hm.put("lat1", Double.valueOf(50.854975));//bruxelles
            hm.put("lng1", Double.valueOf(4.3753899));
            hm.put("lat2", 48.2092);//austria
            hm.put("lng2", 16.3728);
        hm.put("lat3",53.9678);//belarus
        hm.put("lng3", 27.5766);
        hm.put("lat4",42.7105);//bulgaria
        hm.put("lng4", 23.3238);
        hm.put("lat5",45.8150);//croatia/zagreb
        hm.put("lng5", 15.9785);
        hm.put("lat6",50.0878);//praga
        hm.put("lng6", 14.4205);
        hm.put("lat7",55.6763);//copenhaga
        hm.put("lng7", 12.5681);
        hm.put("lat8",59.4389);//tallinn
        hm.put("lng8", 24.7545);
        hm.put("lat9",60.1699);//helsinki /finlanda
        hm.put("lng9", 24.9384);
        hm.put("lat10",48.8567);//paris/franta
        hm.put("lng10", 2.3510);
        hm.put("lat11",52.5235);//berlin/germania
        hm.put("lng11", 13.4115);
        hm.put("lat12",37.9792);//atena /grecia
        hm.put("lng12", 23.7166);
        hm.put("lat13",47.4984);//budapesta   /ungaria
        hm.put("lng13", 19.0408);
        hm.put("lat14",64.1353);//islanda
        hm.put("lng14", 21.8952);
        hm.put("lat15",53.3441);//dublin  /irlanda
        hm.put("lng15", -6.2675);
        hm.put("lat16",41.8955);//roma    /italia
        hm.put("lng16", 12.4823);
        hm.put("lat17",56.9465);//riga/letonia
        hm.put("lng17", 24.1049);
        hm.put("lat18",47.1411);//liechtenstein
        hm.put("lng18", 9.5215);
        hm.put("lat19",54.6896);//vilnius /lithuania
        hm.put("lng19", 25.2799);
        hm.put("lat20",49.6100);//luxemburg
        hm.put("lng20", 6.1296);
        hm.put("lat21",42.0024);//skopye/macedonia
        hm.put("lng21", 21.4361);
        hm.put("lat22",35.9042);//malta
        hm.put("lng22", 14.5189);
        hm.put("lat23",47.0167);//chisinau/Basarabia
        hm.put("lng23", 28.8497);
        hm.put("lat24",43.7325);//monaco
        hm.put("lng24", 7.4189);
        hm.put("lat25",52.3738);//amsterdam/olanda
        hm.put("lng25", 4.8910);
        hm.put("lat26",59.9138);//oslo/norvegia
        hm.put("lng26", 10.7387);
        hm.put("lat27",52.2297);//varsovia/polonia
        hm.put("lng27", 21.0122 );
        hm.put("lat28",38.7072);//lisabona/portugalia
        hm.put("lng28", -9.1355);
        hm.put("lat29",55.7558);//moscova/rusia
        hm.put("lng29", 37.6176);
        hm.put("lat30",43.9424);//san marino
        hm.put("lng30", 12.4578);
        hm.put("lat31",44.8048);//belgrad/serbia
        hm.put("lng31", 20.4781);
        hm.put("lat32",48.2116);//slovacia/bratislava
        hm.put("lng32", 17.1547);
        hm.put("lat33",46.0514);//slovenia/ljublijana
        hm.put("lng33", 14.5060);
        hm.put("lat34",40.4167);//spania/madrid
        hm.put("lng34", -3.7033);
        hm.put("lat35",59.3328);//suedia/stockholm
        hm.put("lng35", 18.0645);
        hm.put("lat36",46.9480);// elvetia
        hm.put("lng36", 7.4481);
        hm.put("lat37",50.4422);// ucraina
        hm.put("lng37", 30.5367);
        hm.put("lat38",51.5105431);// londra/anglia ,
        hm.put("lng38", -0.1235498);

        for (int i = 0; i < 39; i++) {
            LAT_KEY = "lat" + String.valueOf(i);
            LNG_KEY = "lng" + String.valueOf(i);
            Log.i(LOG_TAG,"Position/Lat/Lng: " + i + "/" + String.valueOf( hm.get(LAT_KEY)) + "/" +String.valueOf( hm.get(LNG_KEY)));
            Double lat = (Double) hm.get(LAT_KEY);
            Double lng = (Double) hm.get(LNG_KEY);
            try {
                List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
                String countryCode = addressList.get(0).getCountryName().substring(0, 2).toLowerCase();
                Log.i(LOG_TAG,"Contry code: " + countryCode);
            }
            catch (IOException e) {

            }
            catch (IndexOutOfBoundsException a) {
                a.printStackTrace();
            }
            Log.i(LOG_TAG,"*****************************************************************************");
        }
    }
}
