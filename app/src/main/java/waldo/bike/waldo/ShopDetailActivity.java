package waldo.bike.waldo;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

import Utilities.Constants;


public class ShopDetailActivity extends FragmentActivity
        implements OnStreetViewPanoramaReadyCallback {
    Double mShopLat;
    Double mShopLng;
    String mShopName;
    Bundle mBundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        mBundle = getIntent().getExtras();
        mShopLat = Double.valueOf(mBundle.getString(Constants.BUNDLE_SHOP_LAT));
        mShopLng = Double.valueOf(mBundle.getString(Constants.BUNDLE_SHOP_LNG));
        mShopName = mBundle.getString(Constants.BUNDLE_SHOP_NAME);
        getActionBar().setTitle(mShopName);
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
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
