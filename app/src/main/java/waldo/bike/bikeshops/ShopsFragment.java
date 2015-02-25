package waldo.bike.bikeshops;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import Utilities.Constants;
import Utilities.DeviceConnection;
import Utilities.GlobalState;
import Utilities.Utility;
import data.ShopsContract;
import sync.SyncAdapter;
/**
 * Created by Narcis11 on 20.12.2014.
 */
public class ShopsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener {


    public SimpleCursorAdapter mShopsAdapter;
    private String mRadius;
    private static final String LOG_TAG = ShopsFragment.class.getSimpleName();
    private String mShopLatitude = "";
    private String mShopLongitude = "";
    private String mPlaceId;
    private String mShopName = "";
    private String mFormattedDuration = "";
    private String mFormattedDistance = "";
    private String mPreferredUnit = "";
    private boolean mIsPartner;
    private String mPromoText;
    private boolean mIsListRefreshed;
    private Double mNewSpeedDistanceToShop;
    private static final int SHOPS_LOADER_ID = 0;//loader identifier
    private ListView mListView;
    private boolean mIsSpeedChanged;
    private SwipeRefreshLayout swipeLayout;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    //used by Google Analytics
    private Tracker mGaTracker;
    public static final String[] SHOPS_COLUMNS = {
            ShopsContract.ShopsEntry.TABLE_NAME + "." + ShopsContract.ShopsEntry._ID,
            ShopsContract.ShopsEntry.COLUMN_SHOP_NAME,
            ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS,
            ShopsContract.ShopsEntry.COLUMN_IS_OPEN,
            ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER,
            ShopsContract.ShopsEntry.COLUMN_DISTANCE_DURATION,
            ShopsContract.ShopsEntry.COLUMN_SHOP_LATITUDE,
            ShopsContract.ShopsEntry.COLUMN_SHOP_LONGITUDE,
            ShopsContract.ShopsEntry.COLUMN_PLACE_ID,
            ShopsContract.ShopsEntry.COLUMN_IS_PARTNER,
            ShopsContract.ShopsEntry.COLUMN_SHOP_PROMO_TEXT,
            ShopsContract.ShopsEntry.COLUMN_DISCOUNT_VALUE
    };

    // These indices are tied to SHOPS_COLUMNS.  If SHOPS_COLUMNS changes, these
    // must change.
    public static final int COL_SHOP_ID = 0;
    public static final int COL_SHOP_NAME = 1;
    public static final int COL_SHOP_ADDRESS = 2;
    public static final int COL_IS_OPEN = 3;
    public static final int COL_DISTANCE_TO_USER = 4;
    public static final int COL_DISTANCE_DURATION = 5;
    public static final int COL_SHOP_LATITUDE = 6;
    public static final int COL_SHOP_LONGITUDE = 7;
    public static final int COL_PLACE_ID = 8;
    public static final int COL_IS_PARTNER = 9;
    public static final int COL_PROMO_TEXT = 10;
    public static final int COL_DISCOUNT_VALUE = 11;

    public ShopsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        mShopsAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_shops_cards,
                null,
                new String[] {
                        ShopsContract.ShopsEntry.COLUMN_SHOP_NAME,
                        ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS,
                        ShopsContract.ShopsEntry.COLUMN_IS_OPEN,
                        ShopsContract.ShopsEntry.COLUMN_DISTANCE_TO_USER,
                        ShopsContract.ShopsEntry.COLUMN_DISTANCE_DURATION,
                        ShopsContract.ShopsEntry.COLUMN_DISCOUNT_VALUE
                },
                new int[] {
                        R.id.list_item_shopname_textview,
                        R.id.list_item_shopaddress_textview,
                        R.id.list_item_shopisopen_textview,
                        R.id.list_item_distance_textview,
                        R.id.list_item_duration_textview,
                        R.id.list_item_discount_view
                },
                0
        );
        //we need to format the data from the database
        mShopsAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch (columnIndex) {
                    case COL_SHOP_NAME:
                        ((TextView) view).setText(cursor.getString(COL_SHOP_NAME));
                        return true;
                    case COL_IS_OPEN:
                        if (cursor.getInt(COL_IS_OPEN) == 1){
                            ((TextView) view).setText(Constants.SHOP_OPEN); //"Open"
                        }
                        else if (cursor.getInt(COL_IS_OPEN) == 0) {
                            ((TextView) view).setText(Constants.SHOP_CLOSED);//"Closed"
                        }
                        else {
                            ((TextView) view).setText(Constants.SHOP_UNAVAILABLE);//""
                        }
                        return true;
                    case COL_DISTANCE_TO_USER:
                    //    Log.i(LOG_TAG,"Shopname / distance: " + cursor.getString(COL_SHOP_NAME) + " / " + cursor.getString(COL_DISTANCE_TO_USER));
                        mPreferredUnit = Utility.getPreferredUnit(getActivity());
                        if (mPreferredUnit.equals(getResources().getString(R.string.unit_array_metric))) {
                            mFormattedDistance = Utility.formatDistanceMetric(cursor.getString(COL_DISTANCE_TO_USER));
                        }
                        else {
                            mFormattedDistance = Utility.formatDistanceImperial(cursor.getString(COL_DISTANCE_TO_USER));
                        }
                        ((TextView) view).setText(mFormattedDistance);
                        return true;
                    case COL_DISTANCE_DURATION:
                        if (!mIsSpeedChanged) {
                            mFormattedDuration = Utility.formatDistanceDuration(cursor.getString(COL_DISTANCE_DURATION));
                            ((TextView) view).setText(mFormattedDuration);
                        }
                        else {
                            int distanceToShop = Integer.valueOf(cursor.getString(COL_DISTANCE_TO_USER));
                            mNewSpeedDistanceToShop = Utility.calculateDistanceDuration(distanceToShop,getActivity());
                            mFormattedDuration = Utility.formatDistanceDuration(String.valueOf(mNewSpeedDistanceToShop));
                            ((TextView) view).setText(mFormattedDuration);
                          //  return true;
                        }
                        return true;
                    case COL_DISCOUNT_VALUE:
                        if ((cursor.getInt(COL_IS_PARTNER) == 1)) {
                            ((TextView) view).setText("-" + String.valueOf(cursor.getInt(COL_DISCOUNT_VALUE)) + "%");
                            view.setVisibility(View.VISIBLE);
                        }
                        else{
                            view.setVisibility(View.GONE);
                        }
                        return true;
                }
                return false;
            }
        });
    //    Log.i(LOG_TAG,"Size of mShopsAdapter = " + mShopsAdapter.getCount());

        // Get a reference to the ListView, and attach this adapter to it.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_shops);
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        mListView = listView;
        listView.setAdapter(mShopsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = mShopsAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    Intent openDetailActivity = new Intent(getActivity().getApplicationContext(), ShopDetailActivity.class);
                    Bundle bundle = new Bundle();
                    mShopName = cursor.getString(COL_SHOP_NAME);
                    mShopLatitude = cursor.getString(COL_SHOP_LATITUDE);
                    mShopLongitude = cursor.getString(COL_SHOP_LONGITUDE);
                    mPlaceId = cursor.getString(COL_PLACE_ID);
                    mIsPartner = (cursor.getInt(COL_IS_PARTNER) == 1);
                    mPromoText = cursor.getString(COL_PROMO_TEXT);
                    Log.i(LOG_TAG, "mIsPartner: " + mIsPartner );
                    //update the database row corresponding to this shop id
                   // updateShopList(getActivity(),mPlaceId);
                    //store the position
                    mPosition = position;
                    //set the event to GA
                    mGaTracker.send(new HitBuilders.EventBuilder()
                            .setCategory(getString(R.string.ga_open_shop_category_id))
                            .setAction(getString(R.string.ga_open_shop_action_id))
                            .setLabel(mShopName)
                            .build());
                    //assemble the bundle
                    bundle.putString(Constants.BUNDLE_SHOP_LAT, mShopLatitude);
                    bundle.putString(Constants.BUNDLE_SHOP_LNG, mShopLongitude);
                    bundle.putString(Constants.BUNDLE_SHOP_NAME,mShopName);
                    bundle.putString(Constants.BUNDLE_SHOP_PLACE_ID,mPlaceId);
                    bundle.putBoolean(Constants.BUNDLE_IS_PARTNER,mIsPartner);
                    bundle.putString(Constants.BUNDLE_FRAGMENT,Constants.CALLED_FROM_FRAGMENT);
                    bundle.putString(Constants.BUNDLE_PROMO_TEXT,mPromoText);
                    openDetailActivity.putExtras(bundle);
                    //lift off!
                    startActivity(openDetailActivity);
                }
         /*       if (cursor != null && cursor.moveToPosition(position)) {
                    mShopName = cursor.getString(COL_SHOP_NAME);
                    mShopLatitude = cursor.getString(COL_SHOP_LATITUDE);
                    mShopLongitude = cursor.getString(COL_SHOP_LONGITUDE);
                    Intent openMap = new Intent(getActivity().getApplicationContext(),MapsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUNDLE_SHOP_LAT,mShopLatitude);
                    bundle.putString(Constants.BUNDLE_SHOP_LNG,mShopLongitude);
                    bundle.putString(Constants.BUNDLE_SHOP_NAME,mShopName);
                    bundle.putString(Constants.BUNDLE_FRAGMENT,Constants.CALLED_FROM_FRAGMENT);
                    openMap.putExtras(bundle);
                    startActivity(openMap);
                }*/
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                DeviceConnection deviceConnection = new DeviceConnection(getActivity());
                //we only refresh when the user is at the top of the list and the Internet is connected and we have the last user's location
                if (firstVisibleItem == 0 && deviceConnection.checkInternetConnected() && !GlobalState.USER_LAT.equals("") && !GlobalState.USER_LNG.equals("")) {
                    swipeLayout.setEnabled(true);
                }
                else {
                    swipeLayout.setEnabled(false);
                }
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGaTracker = ((BikeShopsDetector)  getActivity().getApplication()).getTracker(
                BikeShopsDetector.TrackerName.APP_TRACKER);
        setHasOptionsMenu(true); //tells the system that we have button(s) in the menu

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.shopsfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG,"Position in onStart: " + mPosition);
       // Log.i(LOG_TAG, "In fragment onStart()");
       // updateShopList();
    }

    @Override
    public void onResume() {
        super.onResume();
        //onResume is called before the loader, so it's safe (read "doesn't affect the logic") to assign values to the booleans here
        mIsListRefreshed = false;
        mIsSpeedChanged = false;
        DeviceConnection deviceConnection = new DeviceConnection(getActivity());
        //refresh if the range has changed, we have an internet connection and the user's last location
        if (GlobalState.FRAGMENT_RANGE != null && !GlobalState.FRAGMENT_RANGE.equals(Utility.getPreferredRangeImperial(getActivity()))
                && deviceConnection.checkInternetConnected() && !GlobalState.USER_LAT.equals("") && !GlobalState.USER_LNG.equals("") ) {
            Log.i(LOG_TAG,"****UPDATED SHOP LIST****");
            Log.i(LOG_TAG,GlobalState.USER_LAT + " / " + GlobalState.USER_LNG);
            updateShopList(getActivity());
            mIsListRefreshed = true;
        }
        //we only restart the loader if the refresh caused by the change of range hasn't been performed. If it has, we already have an updated list
        if (GlobalState.FRAGMENT_SPEED != null && !GlobalState.FRAGMENT_SPEED.equals(Utility.getPreferredSpeed(getActivity())) && !mIsListRefreshed) {
            mIsSpeedChanged = true;
            getLoaderManager().restartLoader(SHOPS_LOADER_ID,null,this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //used to determine of a refresh of the displayed speed or range is necessary
        GlobalState.FRAGMENT_RANGE = Utility.getPreferredRangeImperial(getActivity());
        GlobalState.FRAGMENT_SPEED = Utility.getPreferredSpeed(getActivity());
    }

    public void updateShopList(Context context) {
        String[] coordinates = new String[2];
        coordinates[0] = GlobalState.USER_LAT;
        coordinates[1] = GlobalState.USER_LNG;
        Log.i(LOG_TAG,"Lat/lng in updateShopList - " + coordinates[0] + "/" + coordinates[1]);
      //  new FetchGooglePlaces(getActivity()).execute(coordinates);
        SyncAdapter.syncImmediately(context);
/*        if (placeId != null) {//get details for one shop
            SyncAdapter.syncImmediately(context,placeId);
        }
        else {//get all shops
            SyncAdapter.syncImmediately(context,null);
        }*/
    }

    //loaders are initialised in onActivityCreated because their lifecycle is bound to the activity, not the fragment
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //initiate loader to populate data in the Shops fragment
        getLoaderManager().initLoader(SHOPS_LOADER_ID,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        mShopsAdapter.swapCursor(data);
        Log.i(LOG_TAG,"Position is: " + mPosition);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            Log.i(LOG_TAG,"Scrolled to " + mPosition);
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mShopsAdapter.swapCursor(null);
    }
    //ShopsContract.ShopsEntry.CONTENT_URI
    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new android.content.CursorLoader(
                getActivity(),
                ShopsContract.ShopsEntry.CONTENT_URI,
                SHOPS_COLUMNS,
                null,
                null,
                ShopsContract.ShopsEntry.SORT_ORDER
        );
    }

    @Override
    public void onRefresh() {
            Log.i(LOG_TAG, "In onRefresh()");
            swipeLayout.setColorSchemeResources(R.color.waldo_light_blue);
            ShopsFragment shopsFragment = new ShopsFragment();
            shopsFragment.updateShopList(getActivity());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(false);
                }
            }, 3000);
        }

}

