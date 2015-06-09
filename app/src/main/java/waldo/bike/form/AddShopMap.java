package waldo.bike.form;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import Utilities.Constants;
import Utilities.GlobalState;
import Utilities.Utility;
import waldo.bike.bikeshops.BikeShopsDetector;
import waldo.bike.bikeshops.R;

public class AddShopMap extends FragmentActivity implements AdapterView.OnItemClickListener, Observer{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final String LOG_TAG = AddShopMap.class.getSimpleName();
    private static double mNewShopLat;
    private static double mNewShopLng;
    private static String mAddress;
    private static String mTestAddress;
    private static AutoCompleteTextView mAutoCompleteTextView;
    private static TextView mResultView;
    private static int DROPDOWN_THRESHOLD = 3;//we display the list of locations after the user types in 3 characters
    //the Google Analytics tracker
    Tracker mGaTracker;
    private String screenName = "AddShopMap Activity";
    //called each time an item from the dropdown list is clicked
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
      //  Log.i(LOG_TAG, "In onItemClick");
        //hide the keyboard when an item from the dropdown is selected
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mAutoCompleteTextView.getWindowToken(),0);
      //  Log.i(LOG_TAG,"Keyboard hidden");
        String str = (String) adapterView.getItemAtPosition(position);
      //  Log.i(LOG_TAG,"str is " + str);
        Double[] coordinatesArray = Utility.getCoordinatesFromAddressName(getApplicationContext(), str, getApplication());
     //   Log.i(LOG_TAG, "Lat/lng of address " + str + " = " + coordinatesArray[0] + "/" + coordinatesArray[1]);
        if (coordinatesArray[0] != null && coordinatesArray[1] != null) {
            mNewShopLat = coordinatesArray[0];
            mNewShopLng = coordinatesArray[1];
            LatLng newUserLatLng = new LatLng(coordinatesArray[0], coordinatesArray[1]);

            mAddress = str;
            //move the marker to the selected address

            mMap.moveCamera(CameraUpdateFactory.newLatLng(newUserLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.SHOP_ZOOM));
            Marker userMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(coordinatesArray[0], coordinatesArray[1])).title(Constants.NEW_SHOP_NAME));
            userMarker.showInfoWindow();//we always display the title of the user's marker
        }
        else {
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.invalid_address), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_map);
        //set the animation between activities
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
        //creating the search view
        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mAutoCompleteTextView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item_places));
        mAutoCompleteTextView.setOnItemClickListener(this);
        //set the width to be 0.9 of the screen width
        mAutoCompleteTextView.setWidth(Utility.getAutocompleteViewWidth(getApplicationContext()));
        //the sync will be made and the dropdown will be shown only if the input has 3 characters minimum
        mAutoCompleteTextView.setThreshold(DROPDOWN_THRESHOLD);
      //  mAutoCompleteTextView.getText().toString();
      //  mAutoCompleteTextView.setHeight(Utility.getAutocompleteViewHeight(getApplicationContext()));
        mAutoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //remove the text when the "X" button is pressed
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mAutoCompleteTextView.getRight() - mAutoCompleteTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        clearViewText();
                        return true;
                    }
                }
                return false;
            }
        });
       // String firstLine = resultsTextView.getText().toString();
        //loading the map
        setUpMapIfNeeded();
    }


    @Override
    protected void onResume() {
        super.onResume();
/*        //initialise the GA tracker
        mGaTracker = ((BikeShopsDetector) getApplication()).getTracker(
                BikeShopsDetector.TrackerName.APP_TRACKER);
        //report to GA that the screen has been opened
        mGaTracker.setScreenName(screenName);
        mGaTracker.send(new HitBuilders.AppViewBuilder().build());*/
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
        //disable the toolbar from the right bottom corner
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(Constants.NEW_SHOP_NAME)
                );
                mNewShopLat = latLng.latitude;
                mNewShopLng = latLng.longitude;
                //hide the keyboard when the user clicks the map
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mAutoCompleteTextView.getWindowToken(),0);
            //    Log.i(LOG_TAG,"Position is " + latLng.latitude + " / " + latLng.longitude);
            }
        });
        //TODO: Test the address generated by getAddressNameFromCoordinates.
        LatLng userLatLng = new LatLng(Double.valueOf(GlobalState.USER_LAT), Double.valueOf(GlobalState.USER_LNG));
        mNewShopLat = Double.valueOf(GlobalState.USER_LAT);
        mNewShopLng = Double.valueOf(GlobalState.USER_LNG);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(Constants.SHOP_ZOOM));
        Marker userMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(GlobalState.USER_LAT), Double.valueOf(GlobalState.USER_LNG))).title(Constants.NEW_SHOP_NAME));
        userMarker.showInfoWindow();//we always display the title of the user's marker
    }


    public void openFormActivity(View view) {
            Bundle bundle = new Bundle();
            //moved it here to make sure we get the right address every time
            mTestAddress = Utility.getAddressNameFromCoordinates(getApplicationContext(),mNewShopLat,mNewShopLng, getApplication());
            if (mTestAddress != Constants.RETURN_ERROR_STRING) {
                mAddress = mTestAddress;
            }
            bundle.putDouble(Constants.ADD_SHOP_BUNDLE_LAT_KEY, mNewShopLat);
            bundle.putDouble(Constants.ADD_SHOP_BUNDLE_LNG_KEY, mNewShopLng);
            bundle.putString(Constants.ADD_SHOP_BUNDLE_ADDRESS_KEY, mAddress);
       //     Log.i(LOG_TAG,"lat/lng/address: " + mNewShopLat + "/" + mNewShopLng + "/" + mAddress);
            Intent formIntent = new Intent(getApplicationContext(), AddShopFormActivity.class);
            formIntent.putExtras(bundle);
            startActivity(formIntent);
    }

    public void clearViewText() {
        mAutoCompleteTextView.clearListSelection();
        mAutoCompleteTextView.setText("");
    }

    @Override
    public void update(Observable observable, Object data) {
        mResultView = (TextView) findViewById(R.id.autocomplete);
     //   Log.i(LOG_TAG,"Received notification");
        if (mResultView != null) {
          //  Log.i(LOG_TAG,"Text is: " + mResultView.getText());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
      //  Log.i(LOG_TAG,"in onConfigurationChanged");
        // Checks whether a hardware keyboard is available
        if (newConfig.keyboard == Configuration.KEYBOARDHIDDEN_NO) {
          //  Log.i(LOG_TAG,"Keyboard is on");
        } else if (newConfig.keyboard == Configuration.KEYBOARDHIDDEN_YES) {
          //  Log.i(LOG_TAG,"Keyboard is off");
        }
    }


}
