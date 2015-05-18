package waldo.bike.bikeshops;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

import Utilities.Constants;


public class ShopStreetViewActivity extends FragmentActivity
        implements OnStreetViewPanoramaReadyCallback {
    private String mActionBarTitle = "Street View";
    private static final String LOG_TAG =  ShopStreetViewActivity.class.getSimpleName();
    private Bundle mBundle;
    private boolean mIsPartner;
    private float mShopCameraBearing;
    private float mShopCameraTilt;
    private float mShopCameraZoom;
    private String mShopCameraPosition = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_street_view);
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
        getActionBar().setTitle(mActionBarTitle);
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetview_shop);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        mBundle = getIntent().getExtras();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop_street_view, menu);
        return true;
    }


    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        Double shopLat = Double.valueOf(mBundle.getString(Constants.BUNDLE_SHOP_LAT));
        Double shopLng = Double.valueOf(mBundle.getString(Constants.BUNDLE_SHOP_LNG));
        mIsPartner = mBundle.getBoolean(Constants.BUNDLE_IS_PARTNER);
        mShopCameraBearing = mBundle.getFloat(Constants.BUNDLE_SHOP_CAMERA_BEARING);
        mShopCameraTilt = mBundle.getFloat(Constants.BUNDLE_SHOP_CAMERA_TILT);
        mShopCameraZoom = mBundle.getFloat(Constants.BUNDLE_SHOP_CAMERA_ZOOM);
        mShopCameraPosition = mBundle.getString(Constants.BUNDLE_SHOP_CAMERA_POSITION);

       // Log.i(LOG_TAG, "In onStreetViewPanoramaReady. Bearing/tilt/zoom/position: " + mShopCameraBearing + "/" + mShopCameraTilt + "/" + mShopCameraZoom + "/" + mShopCameraPosition);
        if (mIsPartner) {
            if (!mShopCameraPosition.equals("")) {
                try {
                    shopLat = Double.valueOf(mShopCameraPosition.substring(0, mShopCameraPosition.indexOf(Constants.COMMA_SEPARATOR)).trim());
                    shopLng = Double.valueOf(mShopCameraPosition.substring(mShopCameraPosition.indexOf(Constants.COMMA_SEPARATOR) + 1));
                }
                catch (NumberFormatException e) {
                    //We revert to the default values if one of the coordinates is incorrect
                    shopLat = Double.valueOf(mBundle.getString(Constants.BUNDLE_SHOP_LAT));
                    shopLng = Double.valueOf(mBundle.getString(Constants.BUNDLE_SHOP_LNG));
                }
            }
            LatLng shopLatLng = new LatLng(shopLat, shopLng);
            //if the bearing/tilt/zoom is not sent from the server, the fallback value is 0 and it does not affect the camera
            StreetViewPanoramaCamera camera = new StreetViewPanoramaCamera.Builder()
                    .bearing(streetViewPanorama.getPanoramaCamera().bearing + mShopCameraBearing)//mShopCameraBearing
                    .tilt(streetViewPanorama.getPanoramaCamera().tilt + mShopCameraTilt)
                    .zoom(streetViewPanorama.getPanoramaCamera().zoom + mShopCameraZoom)
                    .build();
            streetViewPanorama.animateTo(camera, 2000);
            streetViewPanorama.setPosition(shopLatLng);
         //   Log.i(LOG_TAG,"After animate");
        }
        else {
            LatLng shopLatLng = new LatLng(shopLat, shopLng);
            streetViewPanorama.setPosition(shopLatLng);
        }
        //the user can now manipulate the image
        streetViewPanorama.setPanningGesturesEnabled(true);
    }

    @Nullable
    @Override
    //called when the user goes back from this activity to shop detail activity
    public Intent getParentActivityIntent() {
        //Log.i(LOG_TAG,"In getParentActivityIntent");

        if (mBundle != null && !mBundle.isEmpty()) {
            //we need to resend the bundle
            Intent shopDetailsIntent = new Intent(this,ShopDetailActivity.class);
            //data from the bundle is extracted in the onCreate from ShopDetailActivity, so we need to send it again.
            shopDetailsIntent.putExtras(mBundle);
            return shopDetailsIntent;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
    }
}
