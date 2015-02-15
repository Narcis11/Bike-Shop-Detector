package waldo.bike.form;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Observable;
import java.util.Observer;

import Utilities.Constants;
import Utilities.GlobalState;
import Utilities.Utility;
import waldo.bike.waldo.R;

public class AddShopMap extends FragmentActivity implements AdapterView.OnItemClickListener, Observer{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private static final String LOG_TAG = AddShopMap.class.getSimpleName();
    Marker mMarker;
    ImageButton mDeleteButton;
    ImageButton mNextButton;
    private static double mNewShopLat;
    private static double mNewShopLng;
    private static String mAddress;
    private static String mTestAddress;
    private static AutoCompleteTextView mAutoCompleteTextView;
    private static TextView mResultView;
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.i(LOG_TAG, "In onItemClick");
        String str = (String) adapterView.getItemAtPosition(position);
        Log.i(LOG_TAG,"str is " + str);
        Double[] coordinatesArray = Utility.getCoordinatesFromAddressName(getApplicationContext(), str);
        Log.i(LOG_TAG, "Lat/lng of address " + str + " = " + coordinatesArray[0] + "/" + coordinatesArray[1]);
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
        //creating the search view
        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mAutoCompleteTextView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item_places));
        mAutoCompleteTextView.setOnItemClickListener(this);
        mAutoCompleteTextView.setWidth(Utility.getAutocompleteViewWidth(getApplicationContext()));

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
                Log.i(LOG_TAG,"Position is " + latLng.latitude + " / " + latLng.longitude);
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(LOG_TAG, "in onMarkerClick");
                if (marker != null) {
                    mMarker = marker;
                    Log.i(LOG_TAG, "MARKER != NULL");
                    if (mDeleteButton != null && mNextButton != null) {
                        mDeleteButton.setVisibility(View.INVISIBLE);
                        mNextButton.setVisibility(View.INVISIBLE);
                        Log.i(LOG_TAG, "After delete of buttons");
                    } else {
                        Log.i(LOG_TAG, "Buttons are null");
                    }
                    //   displayButtons();
                }
                return false;
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
            mTestAddress = Utility.getAddressNameFromCoordinates(getApplicationContext(),mNewShopLat,mNewShopLng);
            if (mTestAddress != Constants.RETURN_ERROR_STRING) {
                mAddress = mTestAddress;
            }
            bundle.putDouble(Constants.ADD_SHOP_BUNDLE_LAT_KEY, mNewShopLat);
            bundle.putDouble(Constants.ADD_SHOP_BUNDLE_LNG_KEY, mNewShopLng);
            bundle.putString(Constants.ADD_SHOP_BUNDLE_ADDRESS_KEY, mAddress);
            Log.i(LOG_TAG,"lat/lng/address: " + mNewShopLat + "/" + mNewShopLng + "/" + mAddress);
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
        Log.i(LOG_TAG,"Received notification");
        if (mResultView != null) {
            Log.i(LOG_TAG,"Text is: " + mResultView.getText());
        }
    }


}
