package waldo.bike.bikeshops;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import Utilities.Constants;
import Utilities.GlobalState;
import Utilities.Utility;
import data.ShopsContract;

public class MapsActivity extends FragmentActivity{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final String LOG_TAG = MapsActivity.class.getSimpleName();
    private Bundle mBundle;
    private TextView mInfoTextView;
    private View mInfoWindow;
    private String mShopName;
    private boolean mIsPartner;
    private String mPromoText;
    private static final int ACTIVITY_INDEX = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //we also have to check here in case the user returns to the running app through some other means, such as through the back button

        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
       // Bundle bundle = getIntent().getExtras();
        if (mBundle != null && !mBundle.isEmpty()) {
            Log.i(LOG_TAG, "Should go back");
            Intent shopDetailsIntent = new Intent(this,ShopDetailActivity.class);
            //data from the bundle is extracted in the onCreate method from ShopDetailActivity, so we need to send it again.
           // Log.i(LOG_TAG,mBundle.getString(Constants.BUNDLE_SHOP_PLACE_ID));
            shopDetailsIntent.putExtras(mBundle);
            return shopDetailsIntent;
        }
        else {
            Intent mainActivityIntent = new Intent(this,MainActivity.class);
            return mainActivityIntent;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        if (mInfoTextView != null && mInfoTextView.getVisibility() == View.VISIBLE) mInfoTextView.setVisibility(View.GONE);
        super.onStop();
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */

    private void setUpMap() {
        mBundle = getIntent().getExtras();
        String allShopsName = "";
        String allShopsLat = "";
        String allShopsLng = "";
        if ( mBundle != null && !mBundle.isEmpty() ) { //call for a shop
            //display the textview
            mInfoTextView = (TextView) findViewById(R.id.info_map_textview);
            mInfoTextView.setVisibility(View.VISIBLE);
            //call from fragment
            Double shopLat = Double.valueOf(mBundle.getString(Constants.BUNDLE_SHOP_LAT));
            Double shopLng = Double.valueOf(mBundle.getString(Constants.BUNDLE_SHOP_LNG));
            mShopName = mBundle.getString(Constants.BUNDLE_SHOP_NAME);
            mIsPartner = mBundle.getBoolean(Constants.BUNDLE_IS_PARTNER);
            mPromoText = mBundle.getString(Constants.BUNDLE_PROMO_TEXT,"");
            LatLng shopLatLng = new LatLng(shopLat, shopLng);
            getActionBar().setTitle(mShopName);
            mMap.addMarker(new MarkerOptions().position(shopLatLng).title(mShopName));
            //we style the info window only for partner shops
            if (mIsPartner) {
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        Log.i(LOG_TAG,"In getInfoContents");
                        mInfoWindow = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                        TextView titleText = (TextView) mInfoWindow.findViewById(R.id.infowindow_title);
                        TextView contentText = (TextView) mInfoWindow.findViewById(R.id.infowindow_content);
                        titleText.setText(mShopName);
                        contentText.setText(Utility.getPromoText(mPromoText,ACTIVITY_INDEX));
                        return mInfoWindow;
                    }
                });
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(shopLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.SHOP_ZOOM));//*; //zoom to the position
        }
        else {
            Log.i(LOG_TAG,"Loading all shops map");
            //if there's no bundle, then the call is from the main activity (View all shops button)
            Cursor shopsCursor;
            getActionBar().setTitle(getResources().getString(R.string.title_activity_all_shops));
            shopsCursor = getApplicationContext().getContentResolver().query(
                    ShopsContract.ShopsEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    ShopsContract.ShopsEntry.SORT_ORDER
            );
            if (shopsCursor.moveToFirst()) {
                for (int i = 0; i < shopsCursor.getCount(); i++) {
                    shopsCursor.moveToPosition(i);//without it, the cursor would remain at the first position and retrieve the same shop in each iteration
                    allShopsName = shopsCursor.getString(shopsCursor.getColumnIndex(ShopsContract.ShopsEntry.COLUMN_SHOP_NAME));
                    allShopsLat = shopsCursor.getString(shopsCursor.getColumnIndex(ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE));
                    allShopsLng = shopsCursor.getString(shopsCursor.getColumnIndex(ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(allShopsLat), Double.valueOf(allShopsLng))).title(allShopsName));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(GlobalState.USER_LAT), Double.valueOf(GlobalState.USER_LNG))));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.CITY_ZOOM));
                }
            }
            shopsCursor.close();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
    }

}
