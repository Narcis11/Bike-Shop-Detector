package waldo.bike.waldo;

import android.app.Activity;
import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import Utilities.Constants;
import Utilities.DeviceConnection;
import Utilities.GlobalState;
import Utilities.Utility;
import data.ShopsContract;
import slidermenu.SliderDrawerItem;
import slidermenu.SliderDrawerListAdapter;
import socialmedia.TwitterAsyncTask;
import com.facebook.Session;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
         {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static Context mContext;
    //state variables, used to control application behaviour
    private static boolean mFirstLoad = true;
    private static boolean mFirstLoadForGPS = true;
    private static String mNetworkState = Constants.NETWORK_STATE_CONNECTED; //main activity only loads if there's Internet connection, so it's safe to assign this value
    private static int mPreviousOrientation = 0;
    private static boolean mIsGpsMessageDisplayed;
    private static boolean mIsInternetMessageDisplayed;
    private IntentFilter mIntentFilter;
    private static boolean mFirstGPSConnection = true; //used to control fragment behaviour in onLocationChanged()
    private static boolean isGPSConnected = false;//used to control fragment behaviour in onResume()
    private static String ViewAllShopsMap = "MapsActivity";
    private static String AddShopMap = "AddShopMap";
    Animation mAnimation;
    private static String fragmentTag = "ShopsFragment";
    static TextView mInfoTextView;
    TextView mLocationView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    //used to store the user's coordinates
    private static String[] mLatLng = new String[2];
     //these variables are used for the slider menu
     private DrawerLayout mDrawerLayout;
     private ListView mDrawerList;
     private ActionBarDrawerToggle mDrawerToggle;
     private View mHeaderView;
     private View mFooterView;
     private View mDividerFooterView;
     // nav drawer title
     private CharSequence mDrawerTitle;

     // used to store app title
     private CharSequence mTitle;

     // slide menu items
     private String[] navMenuTitles;
     private TypedArray navMenuIcons;

     private ArrayList<SliderDrawerItem> navDrawerItems;
     private SliderDrawerListAdapter adapter;

    @Override
    protected void onDestroy() {
         super.onDestroy();
    }

             @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         if (savedInstanceState == null) {
                 getFragmentManager().beginTransaction()
                      .add(R.id.container, new ShopsFragment(),fragmentTag)
                      .commit();
         }
        //instantiante the action bar
        //TODO: Load the action bar from the XML resource
        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.drawable.waldo_action_bar);
        actionBar.setTitle("");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        mLocationView = new TextView(this);

       // setContentView(mLocationView); CRASHES BECAUSE OF THIS LINE
        //register the Google Api Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mContext = getApplicationContext();
        //instantiate the intent filter used by the broadcast receiver
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constants.BROADCAST_ACTION);
        if (mFirstLoadForGPS) {
            mPreviousOrientation = Utility.getScreenOrientation(mContext);
        }

     //this piece of code is used for creating the slider menu
                 mTitle = mDrawerTitle = getTitle();

                 // load slide menu items
                 navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_array);

                 // nav drawer icons from resources
                 navMenuIcons = getResources()
                         .obtainTypedArray(R.array.nav_drawer_icons_array);

                 mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                 mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
                 mHeaderView = getLayoutInflater().inflate(R.layout.header_sliding_menu,null);
                 mFooterView = getLayoutInflater().inflate(R.layout.footer_sliding_menu,null);
                 mDividerFooterView = getLayoutInflater().inflate(R.layout.list_divider_view,null);
                 navDrawerItems = new ArrayList<SliderDrawerItem>();

                 // adding nav drawer items to array
                 // Add a shop
                 navDrawerItems.add(new SliderDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0,-1)));
                 // View all shops
                 navDrawerItems.add(new SliderDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1,-1)));
                 //Website
                 navDrawerItems.add(new SliderDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2,-1)));
                 //Facebook
               //  navDrawerItems.add(new SliderDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3,-1)));
                 //Twitter
              //   navDrawerItems.add(new SliderDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4,-1)));

                 // Recycle the typed array
                 navMenuIcons.recycle();
                 mHeaderView.setBackgroundColor(getResources().getColor(R.color.header_background));
                 mDrawerList.addHeaderView(mHeaderView);//in pre-KitKat versions, we must add the header before the setAdapter is called;
                 mDrawerList.addFooterView(mDividerFooterView);
                 mDrawerList.addFooterView(mFooterView);
                 // setting the nav drawer list adapter
                 adapter = new SliderDrawerListAdapter(getApplicationContext(),
                         navDrawerItems);
                 mDrawerList.setAdapter(adapter);
                 // enabling action bar app icon and behaving it as toggle button
                 getActionBar().setDisplayHomeAsUpEnabled(true);
                 getActionBar().setHomeButtonEnabled(true);
                 mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                         R.drawable.ic_drawer, //nav menu toggle icon
                         R.string.app_name, // nav drawer open - description for accessibility
                         R.string.app_name // nav drawer close - description for accessibility
                 ){
                     public void onDrawerClosed(View view) {
                         getActionBar().setTitle(mTitle);
                         // calling onPrepareOptionsMenu() to show action bar icons
                         invalidateOptionsMenu();
                     }

                     public void onDrawerOpened(View drawerView) {
                         getActionBar().setTitle(mDrawerTitle);
                         // calling onPrepareOptionsMenu() to hide action bar icons
                         invalidateOptionsMenu();
                     }
                 };
                 mDrawerLayout.setDrawerListener(mDrawerToggle);
                 mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

           /*      if (savedInstanceState == null) {
                     // on first time display view for first nav item
                     displayView(0);
                 }*/
                 mAnimation = AnimationUtils.loadAnimation(mContext,R.anim.internet_connected);
                 mInfoTextView = (TextView) findViewById(R.id.info_textview);

                 try {
                     PackageInfo info = getPackageManager().getPackageInfo(
                             "waldo.bike.waldo",
                             PackageManager.GET_SIGNATURES);
                     for (Signature signature : info.signatures) {
                         MessageDigest md = MessageDigest.getInstance("SHA");
                         md.update(signature.toByteArray());
                         Log.i(LOG_TAG,"KeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
                     }
                 }
                 catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                 } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                 }
    }



    @Override
    protected void onStart() {
        super.onStart();
      //  Log.i(LOG_TAG,"in onStart()");
        if (mPreviousOrientation  != 4 ) {
            mGoogleApiClient.connect();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
        unregisterReceiver(mBroadcastReceiver);
    //    Log.i(LOG_TAG,"in onPause");
        //random value used to prevent the GPS from disconnecting
        //at every orientation change (onPause() is called before onStop())
        mPreviousOrientation = 4;
        mFirstLoadForGPS = false;
    }

             @Override
    protected void onStop() {
        super.onStop();
        if (mPreviousOrientation != 4 ) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
        //register the broadcast receiver
        registerReceiver(mBroadcastReceiver,mIntentFilter);
        DeviceConnection deviceConnection = new DeviceConnection(mContext);
      //  Log.i(LOG_TAG,"onResume()|mNetworkState = " + mNetworkState);
        //onResume is called by the system from onReceive whenever there's a network change
        //we don't display the message if the user turns on wifi when data connection is turned on or viceversa
        //if (!mFirstLoad) {
            //checking if GPS is enabled
            if (!deviceConnection.checkGpsEnabled()) {
                mIsGpsMessageDisplayed = true;
                mInfoTextView.setVisibility(View.VISIBLE);
                mInfoTextView.setText(mContext.getResources().getString(R.string.no_gps));
                mInfoTextView.setTextColor(Color.WHITE);
                mInfoTextView.setBackgroundColor(Color.RED);
            }
            else if (deviceConnection.checkGpsEnabled() ) {
                mIsGpsMessageDisplayed = false;
                if (!mIsInternetMessageDisplayed) {
                    mInfoTextView.setVisibility(View.INVISIBLE);
                }
            }
          //  }

        mFirstLoad = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //start the settings activity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

     private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
            @Override
             public void onReceive(Context context, Intent intent) {
                DeviceConnection deviceConnection = new DeviceConnection(mContext);
                if (deviceConnection.checkInternetConnected() && (!mNetworkState.equals(Constants.NETWORK_STATE_CONNECTED))) {
                    mIsInternetMessageDisplayed = false;
                    if (!mIsGpsMessageDisplayed) {
                        mInfoTextView.setVisibility(View.INVISIBLE);
                    }
                    else { //if both GPS and Internet are turned off, and the user turns on only the Internet, we still have to display the message for the GPS
                        mInfoTextView.setVisibility(View.VISIBLE);
                        mInfoTextView.setText(mContext.getResources().getString(R.string.no_gps));
                        mInfoTextView.setTextColor(Color.WHITE);
                        mInfoTextView.setBackgroundColor(Color.RED);
                    }
                }
                else if (deviceConnection.checkInternetDisConnected() && !(deviceConnection.checkInternetConnected()
                        || deviceConnection.checkInternetConnecting())) {
                    mNetworkState = Constants.NETWORK_STATE_DISCONNECTED;
                    mIsInternetMessageDisplayed = true;
                    mInfoTextView.setVisibility(View.VISIBLE);
                    mInfoTextView.setText(mContext.getResources().getString(R.string.no_internet));
                    mInfoTextView.setTextColor(Color.WHITE);
                    mInfoTextView.setBackgroundColor(Color.RED);
                }
             }
     };
    //required methods for managing the location
    @Override
    public void onLocationChanged(Location location) {
        //mLocationView.setText("Location received: " + location.toString());
      //  Log.i(LOG_TAG,"firstGPSConnection is " + firstGPSConnection);
 /*      if (firstGPSConnection) { //only display the fragment if it's the first GPS connection
           getFragmentManager().beginTransaction()
                    .add(R.id.container, new ShopsFragment(),fragmentTag)
                    .commit(); //TO BE REMOVED
           Log.i(LOG_TAG,"Fragment created in onLocationChanged");
           mLatLng = Utility.getLatLng(location.toString());
           GlobalState.latitude = mLatLng[0];
           GlobalState.longitude = mLatLng[1];
        }*/
        //TODO: Find another logic for the user's positioning. We need to collect the coordinates more often.
        if (mFirstGPSConnection) {
            mLatLng = Utility.getLatLngFromLocation(location.toString());
            GlobalState.USER_LAT = mLatLng[0];
            GlobalState.USER_LNG = mLatLng[1];
            Log.i(LOG_TAG,"Location in GPS is " + location.toString());
            //ShopsFragment shopsFragment = new ShopsFragment();
            //shopsFragment.updateShopList(mContext);
        }
            mFirstGPSConnection = false;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG_TAG,"in onConnected GPS");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update the location every second
        isGPSConnected = true;
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "GoogleApiClient connection has failed");
    }

             /**
     * These methods are used for the Slider Menu
     */

             /***
              * Called when invalidateOptionsMenu() is triggered
              */
             @Override
             public boolean onPrepareOptionsMenu(Menu menu) {
                 // if nav drawer is opened, hide the action items
                 boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
                 menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
                 return super.onPrepareOptionsMenu(menu);
             }

             @Override
             public void setTitle(CharSequence title) {
                 mTitle = title;
                 getActionBar().setTitle(mTitle);
             }

             /**
              * When using the ActionBarDrawerToggle, you must call it during
              * onPostCreate() and onConfigurationChanged()...
              */

             @Override
             protected void onPostCreate(Bundle savedInstanceState) {
                 super.onPostCreate(savedInstanceState);
                 // Sync the toggle state after onRestoreInstanceState has occurred.
                 mDrawerToggle.syncState();
             }

             @Override
             public void onConfigurationChanged(Configuration newConfig) {
                 super.onConfigurationChanged(newConfig);
                 // Pass any configuration change to the drawer toggls
                 mDrawerToggle.onConfigurationChanged(newConfig);
             }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
       //     Log.i(LOG_TAG,"Button pressed at position " + position);
            if (position == 1) { //add a shop
                openMap(AddShopMap);
            }
            else if (position == 2) { //view all shops
                openMap(ViewAllShopsMap);
            }
            else if (position == 3) {
                openWebsite();
            }
 /*           else if (position == 4) {
                openFacebook();
            }
            else if (position == 5) {
                openTwitter();
            }*/
        }
    }

    private void openFacebook() {
        String url = "https://www.facebook.com/waldotheknight";
        //367189063436001 is the profile id of Waldo
        String uri = "fb://page/367189063436001";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // If a Facebook app is installed, use it. Otherwise, launch
        // a browser
        intent.setData(Uri.parse(uri));
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() == 0) {
            intent.setData(Uri.parse(url));
        }
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {//no app is installed to service this request
            Toast.makeText(mContext, getResources().getString(R.string.no_app_available), Toast.LENGTH_SHORT).show();
        }
    }
    private void openTwitter() {
        String url = "http://www.twitter.com/waldotheknight";
        //2846625313 is Waldo's twitter id
        String uri = "twitter://user?user_id=2846625313";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        final PackageManager packageManager = getPackageManager();
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() == 0) {
            intent.setData(Uri.parse(url));
        }
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {//no app is installed to service this request
            Toast.makeText(mContext, getResources().getString(R.string.no_app_available), Toast.LENGTH_SHORT).show();
        }
    }

    private void openWebsite() {
        String url = "http://www.waldo.bike/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(intent);
            //close the drawer only when we are ready to open the browser (no exception thrown)
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, getResources().getString(R.string.no_app_available), Toast.LENGTH_SHORT).show();
        }
    }

    public void openMap(String mapToOpen) {

        if (mapToOpen.equals(ViewAllShopsMap)) {
            Cursor cursor = mContext.getApplicationContext().getContentResolver().query(
                    ShopsContract.ShopsEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
       /* Because we open the map's view according to the user's location*/
            if (GlobalState.USER_LAT.equals("") && GlobalState.USER_LNG.equals("")) {
                Toast.makeText(mContext, R.string.no_user_location, Toast.LENGTH_SHORT).show();
            } else {
                if (cursor.getCount() > 0) {
                    //close the drawer only when we are ready to open the map
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    Intent intent = new Intent(this, MapsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, R.string.data_not_fetched, Toast.LENGTH_SHORT).show();
                }
            }

        }
        else if (mapToOpen.equals(AddShopMap)) {
            if (GlobalState.USER_LAT.equals("") && GlobalState.USER_LNG.equals("")) {
                Toast.makeText(mContext, R.string.no_user_location, Toast.LENGTH_SHORT).show();
            }
            else {
                //close the drawer only when we are ready to open the map
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                Intent intent = new Intent(mContext, waldo.bike.form.AddShopMap.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         if ((keyCode == KeyEvent.KEYCODE_BACK) && mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
             mDrawerLayout.closeDrawer(Gravity.LEFT);
         }
         return super.onKeyDown(keyCode, event);
    }

    public void twitterFollowTask (View v) {
        DeviceConnection deviceConnection = new DeviceConnection(mContext);
        if (deviceConnection.checkInternetConnected()) {
            new TwitterAsyncTask().execute();
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
        else {
            Toast.makeText(mContext,getResources().getString(R.string.no_internet),Toast.LENGTH_SHORT).show();
        }
    }

    }
