package waldo.bike.bikeshops;

import android.app.LoaderManager;
import android.app.Notification;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

import Utilities.Constants;
import Utilities.Utility;
import data.ShopsContract;


public class ShopDetailActivity extends FragmentActivity
        implements OnStreetViewPanoramaReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ShopDetailActivity.class.getSimpleName();
    private Double mShopLat;
    private Double mShopLng;
    private String mShopName;
    private Bundle mBundle;
    private String mPlaceid;
    private String mPromoText;
    private String mShopOpeningHours;
    private String mTodayFromOpeningHours;
    private float mShopRating;
    private static final int ACTIVITY_INDEX = 1;
    private static Context mContext;
    //the views
    private TextView mShopNameTextView;
    private TextView mShopAddressTextView;
    private TextView mShopPhoneNumberTextView;
    private TextView mShopOpeningHoursTextView;
    private TextView mShopWebsiteTextView;
    private TextView mShopPromoText;
    private RatingBar mShopRatingBar;
    private LayerDrawable mRatingDrawable;
    //the Google Analytics tracker
    Tracker mGaTracker;
    private String screenName="Shop Detail Activity";
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
    public static String mShopPhoneNumber = "";
    public static String mShopWebsite = "";
    private static final String querySelection = ShopsContract.ShopsEntry.COLUMN_PLACE_ID + "=?";
    private static final String[] querySelectionArgs = new String[1];
    private static final int SHOP_DETAILS_LOADER_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
        mContext = getApplicationContext();
        mBundle = getIntent().getExtras();
        mShopLat = Double.valueOf(mBundle.getString(Constants.BUNDLE_SHOP_LAT));
        mShopLng = Double.valueOf(mBundle.getString(Constants.BUNDLE_SHOP_LNG));
        mShopName = mBundle.getString(Constants.BUNDLE_SHOP_NAME);
        mPlaceid = mBundle.getString(Constants.BUNDLE_SHOP_PLACE_ID);
        mPromoText = mBundle.getString(Constants.BUNDLE_PROMO_TEXT,"");//if the value is null, the promo text is ""
        getActionBar().setTitle(mShopName);
        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        querySelectionArgs[0] = mPlaceid;
        getLoaderManager().initLoader(SHOP_DETAILS_LOADER_ID,null,this);//initiate the loader
		//adding a line for testing purposes
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGaTracker = ((BikeShopsDetector) getApplication()).getTracker(
                BikeShopsDetector.TrackerName.APP_TRACKER);
        //report to GA that this screen has been opened
        mGaTracker.setScreenName(screenName);
        mGaTracker.send(new HitBuilders.AppViewBuilder().build());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float heightPx = displaymetrics.heightPixels;
        //Log.i(LOG_TAG,"Screen height is: " + String.valueOf(displaymetrics.heightPixels) + "| dp = " +  Utility.convertPixelsToDp(mContext,heightPx));
        mShopNameTextView = (TextView) findViewById(R.id.detail_shopname);
        mShopAddressTextView = (TextView) findViewById(R.id.detail_shopaddress);
        mShopPhoneNumberTextView = (TextView) findViewById(R.id.detail_shopphonenumber);
        mShopOpeningHoursTextView = (TextView) findViewById(R.id.detail_shopopeninghours);
        mShopWebsiteTextView = (TextView) findViewById(R.id.detail_shopwebsite);
        mShopPromoText = (TextView) findViewById(R.id.detail_promo_text);
        mShopRatingBar = (RatingBar) findViewById(R.id.detail_rating);
        mRatingDrawable = (LayerDrawable) mShopRatingBar.getProgressDrawable();
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
                Intent shopStreetViewIntent = new Intent(mContext,ShopStreetViewActivity.class);
                shopStreetViewIntent.putExtras(mBundle);
                startActivity(shopStreetViewIntent);
            }
        });

    }

    public void openMap(View v) {
        Intent openMapIntent = new Intent(mContext,MapsActivity.class);
        openMapIntent.putExtras(mBundle);
        startActivity(openMapIntent);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
    }

    public void callShop (View v) {
        String intentUri = "tel:" + mShopPhoneNumber;
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        // Build and send a tracked event to GA.
        mGaTracker.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.ga_shopnumber_category_id))
                .setAction(getString(R.string.ga_shopnumber_action_id))
                .setLabel(mShopPhoneNumber + " - " + mShopName)
                .build());
        callIntent.setData(Uri.parse(intentUri));
        startActivity(callIntent);
    }

    public void openShopWebsite(View v) {
        Intent intent = new Intent(mContext, WebActivity.class);
        mBundle.putString(Constants.BUNDLE_WEBSITE,mShopWebsite);//the URL to open
        mBundle.putString(Constants.BUNDLE_WEBVIEW_TITLE,mShopName);//the title of the WebActivity
        try {
            // Build and send a tracked event to GA.
            mGaTracker.send(new HitBuilders.EventBuilder()
                    .setCategory(getString(R.string.ga_shopwebsite_category_id))
                    .setAction(getString(R.string.ga_shopwebsite_action_id))
                    .setLabel(mShopWebsite)
                    .build());
            intent.putExtras(mBundle);
            startActivity(intent);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, getResources().getString(R.string.no_app_available), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new android.content.CursorLoader(
                mContext,
                ShopsContract.ShopsEntry.CONTENT_URI,
                QUERY_COLUMS,
                querySelection,
                querySelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor shopDetailCursor) {
        if (shopDetailCursor.moveToFirst()) {
            mShopNameTextView.setText(shopDetailCursor.getString(COL_SHOP_NAME));
            mShopAddressTextView.setText(shopDetailCursor.getString(COL_SHOP_ADDRESS));
            //setting up the phone number
            mShopPhoneNumber = (shopDetailCursor.getString(COL_SHOP_PHONE_NUMBER) != null) ? shopDetailCursor.getString(COL_SHOP_PHONE_NUMBER) : "";
            if (!mShopPhoneNumber.equals("")) {
                mShopPhoneNumberTextView.setVisibility(View.VISIBLE);
                mShopPhoneNumberTextView.setText(mShopPhoneNumber);
            }
            //setting up the opening hours
            mShopOpeningHours =  (shopDetailCursor.getString(COL_SHOP_OPENING_HOURS) != null) ? shopDetailCursor.getString(COL_SHOP_OPENING_HOURS) : "";
            mTodayFromOpeningHours = Utility.getTodayFromOpeningHours(mShopOpeningHours, getApplication());
            if (!mTodayFromOpeningHours.equals("")) {
                mShopOpeningHoursTextView.setVisibility(View.VISIBLE);
                mShopOpeningHoursTextView.setText(mTodayFromOpeningHours);
            }
            //setting up the website
            mShopWebsite = (shopDetailCursor.getString(COL_SHOP_WEBSITE) != null) ? shopDetailCursor.getString(COL_SHOP_WEBSITE) : "";
            if (!mShopWebsite.equals("")) {
                mShopWebsiteTextView.setVisibility(View.VISIBLE);
                mShopWebsiteTextView.setText(mShopWebsite);
            }
            //setting up the rating
            mShopRating = shopDetailCursor.getFloat(COL_SHOP_RATING);
            if (mShopRating > 0) {
                mShopRatingBar.setVisibility(View.VISIBLE);
                mRatingDrawable.getDrawable(0).setColorFilter(getResources().getColor(R.color.shop_detail), PorterDuff.Mode.SRC_ATOP);//background
                mRatingDrawable.getDrawable(1).setColorFilter(getResources().getColor(R.color.shop_detail), PorterDuff.Mode.SRC_ATOP);//secondary progress
                mRatingDrawable.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);//progress
                mShopRatingBar.setRating(mShopRating);
            }
            //setting up the promo text
            if (!mPromoText.equals(""))
                mShopPromoText.setText(Utility.getPromoText(mPromoText,ACTIVITY_INDEX));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
