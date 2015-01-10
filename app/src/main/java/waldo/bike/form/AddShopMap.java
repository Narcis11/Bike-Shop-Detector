package waldo.bike.form;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import Utilities.Constants;
import Utilities.GlobalState;
import Utilities.Utility;
import waldo.bike.waldo.R;

public class AddShopMap extends FragmentActivity implements AdapterView.OnItemClickListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final String LOG_TAG = AddShopMap.class.getSimpleName();
    Marker mMarker;
    ImageButton mDeleteButton;
    ImageButton mNextButton;
    private static double mNewShopLat;
    private static double mNewShopLng;
    private static String mAddress;
    private static String mTestAddress;
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.i(LOG_TAG,"In onItemClick");
        String str = (String) adapterView.getItemAtPosition(position);
        Log.i(LOG_TAG,"str is " + str);
        Double[] coordinatesArray = Utility.getCoordinatesFromAddressName(getApplicationContext(), str);
        Log.i(LOG_TAG,"Lat/lng of address " + str+ " = " + coordinatesArray[0] + "/" + coordinatesArray[1]);
        mNewShopLat = coordinatesArray[0];
        mNewShopLng = coordinatesArray[1];
        mAddress = str;
        //move the marker to the selected address
        LatLng newUserLatLng = new LatLng(coordinatesArray[0], coordinatesArray[1]);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newUserLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.SHOP_ZOOM));
        Marker userMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(coordinatesArray[0], coordinatesArray[1])).title(Constants.NEW_SHOP_NAME));
        userMarker.showInfoWindow();//we always display the title of the user's marker
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_map);
        //creating the search view
        final AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item_places));
        autoCompView.setOnItemClickListener(this);
/*        autoCompView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                autoCompView.setThreshold(5);
                autoCompView.showDropDown();
                return false;
            }
        });*/

        //loading the map
        setUpMapIfNeeded();
      //  displayButtons();
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
    //TODO: Display the results from the API call in the UI and add onClick behaviour
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
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                //delete all markers and add a new one when the map is long clicked
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(Constants.NEW_SHOP_NAME)
                );
                mNewShopLat = latLng.latitude;
                mNewShopLng = latLng.longitude;
                Log.i(LOG_TAG,"Position is " + latLng.latitude + " / " + latLng.longitude);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(LOG_TAG,"in onMarkerClick");
                if (marker != null) {
                    mMarker = marker;
                    Log.i(LOG_TAG,"MARKER != NULL");
                    if (mDeleteButton != null && mNextButton != null) {
                        mDeleteButton.setVisibility(View.INVISIBLE);
                        mNextButton.setVisibility(View.INVISIBLE);
                        Log.i(LOG_TAG,"After delete of buttons");
                    }
                    else {
                        Log.i(LOG_TAG,"Buttons are null");
                    }
                    displayButtons();
                }
                return false;
            }
        });
        LatLng userLatLng = new LatLng(Double.valueOf(GlobalState.USER_LAT), Double.valueOf(GlobalState.USER_LNG));
        mNewShopLat = Double.valueOf(GlobalState.USER_LAT);
        mNewShopLng = Double.valueOf(GlobalState.USER_LNG);
        mTestAddress = Utility.getAddressNameFromCoordinates(getApplicationContext(),mNewShopLat,mNewShopLng);
        if (mTestAddress != Constants.RETURN_ERROR_STRING) {
            mAddress = mTestAddress;
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.SHOP_ZOOM));
        Marker userMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(GlobalState.USER_LAT), Double.valueOf(GlobalState.USER_LNG))).title(Constants.NEW_SHOP_NAME));
        userMarker.showInfoWindow();//we always display the title of the user's marker
    }

    private void displayButtons() {
        ImageButton deleteButton = (ImageButton) findViewById(R.id.deleteButton);
        ImageButton nextButton = (ImageButton) findViewById(R.id.nextButton);
        deleteButton.setVisibility(ImageButton.VISIBLE);
        nextButton.setVisibility(ImageButton.VISIBLE);
        mDeleteButton = deleteButton;
        mNextButton = nextButton;
    }

    public void openFormActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putDouble(Constants.ADD_SHOP_BUNDLE_LAT_KEY,mNewShopLat);
        bundle.putDouble(Constants.ADD_SHOP_BUNDLE_LNG_KEY, mNewShopLng);
        bundle.putString(Constants.ADD_SHOP_BUNDLE_ADDRESS_KEY, mAddress);
        Intent formIntent = new Intent(getApplicationContext(), AddShopFormActivity.class);
        formIntent.putExtras(bundle);
        startActivity(formIntent);
    }

    public void deleteMarker(View view) {
        mMap.clear();
        if (mDeleteButton != null && mNextButton != null) {
            mDeleteButton.setVisibility(ImageButton.INVISIBLE);
            mNextButton.setVisibility(ImageButton.INVISIBLE);
        }
    }
}
