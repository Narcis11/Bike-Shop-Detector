package waldo.bike.waldo;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import Utilities.Constants;
import Utilities.GlobalState;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final String LOG_TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        Bundle bundle = getIntent().getExtras();
        String fragmentCall = "";
        String allShopsName = "";
        String allShopsLat = "";
        String allShopsLng = "";

        //(fragmentCall = bundle.getString(Constants.BUNDLE_FRAGMENT) ) != null && fragmentCall.equals(Constants.CALLED_FROM_FRAGMENT)
        if ( bundle != null && !bundle.isEmpty() ) {
            Log.i(LOG_TAG, "Preparing to extract bundles");
            Double shopLat = Double.valueOf(bundle.getString(Constants.BUNDLE_SHOP_LAT));
            Double shopLng = Double.valueOf(bundle.getString(Constants.BUNDLE_SHOP_LNG));
            Double userLat = Double.valueOf(GlobalState.USER_LAT);
            Double userLng = Double.valueOf(GlobalState.USER_LNG);
            String shopName = bundle.getString(Constants.BUNDLE_SHOP_NAME);
            LatLng shopLatLng = new LatLng(shopLat, shopLng);
            LatLng userLatLng = new LatLng(userLat, userLng);

            mMap.addMarker(new MarkerOptions().position(shopLatLng).title(shopName));
            mMap.addMarker(new MarkerOptions().position(userLatLng).title(Constants.USERS_NAME));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(shopLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.USER_SHOP_ZOOM));//*; //zoom to the position
        }
        else {
            //if there's no bundle, then the call is from the main activity (View all shops button)
            Log.i(LOG_TAG, "GlobalState.ALL_SHOPS_INFO in maps activity: " + GlobalState.ALL_SHOPS_INFO);
                String[] allShopsInfo = GlobalState.ALL_SHOPS_INFO.substring(1).split(Constants.HASH_SEPARATOR);
                //ceva de genul "HyperSport,44.481649,26.09269|"
                Log.i(LOG_TAG, "allShopsInfo[1] = " + allShopsInfo[1]);
                Log.i(LOG_TAG, "allShopsInfo.length = " + allShopsInfo.length);
                for (int i = 0; i < allShopsInfo.length; i++) {
                    Log.i(LOG_TAG, "i is " + i);
                    allShopsName = allShopsInfo[i].substring(0, allShopsInfo[i].indexOf(Constants.COMMA_SEPARATOR));
                    allShopsLat = allShopsInfo[i].substring(allShopsInfo[i].indexOf(Constants.COMMA_SEPARATOR) + 1, allShopsInfo[i].lastIndexOf(Constants.COMMA_SEPARATOR));
                    allShopsLng = allShopsInfo[i].substring(allShopsInfo[i].lastIndexOf(Constants.COMMA_SEPARATOR) + 1);

                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(allShopsLat), Double.valueOf(allShopsLng))).title(allShopsName));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(allShopsLat), Double.valueOf(allShopsLng))));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.CITY_ZOOM));
                }
        }
    }
}
