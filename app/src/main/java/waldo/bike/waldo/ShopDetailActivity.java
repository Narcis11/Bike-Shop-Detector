package waldo.bike.waldo;

import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

import Utilities.Constants;
import Utilities.Utility;
import data.ShopsContract;


public class ShopDetailActivity extends FragmentActivity
        implements OnStreetViewPanoramaReadyCallback {

    private static final String LOG_TAG = ShopDetailActivity.class.getSimpleName();
    Double mShopLat;
    Double mShopLng;
    String mShopName;
    Bundle mBundle;
    String mPlaceid;
    private static final String[] QUERY_COLUMS = {
            ShopsContract.ShopsEntry.COLUMN_SHOP_NAME,
            ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS,
            ShopsContract.ShopsEntry.COLUMN_WEBSITE,
            ShopsContract.ShopsEntry.COLUMN_PHONE_NUMBER,
            ShopsContract.ShopsEntry.COLUMN_OPENING_HOURS,
            ShopsContract.ShopsEntry.COLUMN_RATING
    };
    public static final int COL_SHOP_NAME = 0;
    public static final int COL_SHOP_ADDRESS = 1;
    public static final int COL_SHOP_WEBSITE= 2;
    public static final int COL_SHOP_PHONE_NUMBER = 3;
    public static final int COL_SHOP_OPENING_HOURS = 4;
    public static final int COL_SHOP_RATING = 5;

    private static final String querySelection = ShopsContract.ShopsEntry.COLUMN_PLACE_ID + "=?";
    private static final String[] querySelectionArgs = new String[1];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        mBundle = getIntent().getExtras();
        mShopLat = Double.valueOf(mBundle.getString(Constants.BUNDLE_SHOP_LAT));
        mShopLng = Double.valueOf(mBundle.getString(Constants.BUNDLE_SHOP_LNG));
        mShopName = mBundle.getString(Constants.BUNDLE_SHOP_NAME);
        mPlaceid = mBundle.getString(Constants.BUNDLE_SHOP_PLACE_ID);
        getActionBar().setTitle(mShopName);
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        querySelectionArgs[0] = mPlaceid;
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView shopNameTextView = (TextView) findViewById(R.id.detail_shopname);
        TextView shopAddressTextView = (TextView) findViewById(R.id.detail_shopaddress);
        TextView shopPhoneNumberTextView = (TextView) findViewById(R.id.detail_shopphonenumber);
        TextView shopOpeningHoursTextView = (TextView) findViewById(R.id.detail_shopopeninghours);
        TextView shopWebsiteTextView = (TextView) findViewById(R.id.detail_shopwebsite);

        Cursor shopDetailCursor = getApplicationContext().getContentResolver().query(
                ShopsContract.ShopsEntry.CONTENT_URI,
                QUERY_COLUMS,
                querySelection,
                querySelectionArgs,
                null
        );
        //set the views' text
        if (shopDetailCursor.moveToFirst()) {
            shopNameTextView.setText(shopDetailCursor.getString(COL_SHOP_NAME));
            shopAddressTextView.setText(shopDetailCursor.getString(COL_SHOP_ADDRESS));
            shopPhoneNumberTextView.setText(shopDetailCursor.getString(COL_SHOP_PHONE_NUMBER));
            if (shopDetailCursor.getString(COL_SHOP_OPENING_HOURS) != null)
            shopOpeningHoursTextView.setText(Utility.getTodayFromOpeningHours(shopDetailCursor.getString(COL_SHOP_OPENING_HOURS)));
            shopWebsiteTextView.setText(shopDetailCursor.getString(COL_SHOP_WEBSITE));
        }
        else {
            Log.i(LOG_TAG,"*****Cursor is null!*****");
        }
/*        for (String i : QUERY_COLUMS) {
            Log.i(LOG_TAG, String.valueOf(shopDetailCursor.getColumnIndex(QUERY_COLUMS[i])));
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        LatLng shopLatLng = new LatLng(mShopLat, mShopLng);
        streetViewPanorama.setPosition(shopLatLng);
        //the user can just click on the image, he can't manipulate it yet
        streetViewPanorama.setPanningGesturesEnabled(false);
        streetViewPanorama.setOnStreetViewPanoramaClickListener(new StreetViewPanorama.OnStreetViewPanoramaClickListener() {
            @Override
            public void onStreetViewPanoramaClick(StreetViewPanoramaOrientation streetViewPanoramaOrientation) {
                Intent shopStreetViewIntent = new Intent(getApplicationContext(),ShopStreetViewActivity.class);
                shopStreetViewIntent.putExtras(mBundle);
                startActivity(shopStreetViewIntent);
            }
        });
    }

    public void openMap(View v) {
        Intent openMapIntent = new Intent(getApplicationContext(),MapsActivity.class);
        Bundle bundle = new Bundle();
        String latitude = String.valueOf(mShopLat);
        String longitude = String.valueOf(mShopLng);
        bundle.putString(Constants.BUNDLE_SHOP_LAT,latitude);
        bundle.putString(Constants.BUNDLE_SHOP_LNG,longitude);
        bundle.putString(Constants.BUNDLE_FRAGMENT,Constants.CALLED_FROM_FRAGMENT);
        bundle.putString(Constants.BUNDLE_SHOP_NAME,mShopName);
        openMapIntent.putExtras(bundle);
        startActivity(openMapIntent);
    }
}
