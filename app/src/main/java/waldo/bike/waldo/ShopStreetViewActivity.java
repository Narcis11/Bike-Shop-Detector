package waldo.bike.waldo;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import Utilities.Constants;


public class ShopStreetViewActivity extends FragmentActivity
        implements OnStreetViewPanoramaReadyCallback {
    private String mActionBarTitle = "Street View";
    private static final String LOG_TAG =  ShopStreetViewActivity.class.getSimpleName();
    private Bundle mBundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_street_view);
        getActionBar().setTitle(mActionBarTitle);
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetview_shop);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop_street_view, menu);
        return true;
    }


    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        Bundle bundle = getIntent().getExtras();
        Double shopLat = Double.valueOf(bundle.getString(Constants.BUNDLE_SHOP_LAT));
        Double shopLng = Double.valueOf(bundle.getString(Constants.BUNDLE_SHOP_LNG));
        LatLng shopLatLng = new LatLng(shopLat, shopLng);
        streetViewPanorama.setPosition(shopLatLng);
        //the user can now manipulate the image
        streetViewPanorama.setPanningGesturesEnabled(true);
    }

    @Nullable
    @Override
    //called when the user goes back from this activity to shop detail activity
    public Intent getParentActivityIntent() {
        Log.i(LOG_TAG,"In getParentActivityIntent");
        mBundle = getIntent().getExtras();
        if (mBundle != null && !mBundle.isEmpty()) {
            //we need to resend the bundle
            Intent shopDetailsIntent = new Intent(this,ShopDetailActivity.class);
            //data from the bundle is extracted in the onCreate from ShopDetailActivity, so we need to send it again.
            shopDetailsIntent.putExtras(mBundle);
            return shopDetailsIntent;
        }
        return null;
    }
}
