package waldo.bike.waldo;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import Places.FetchGooglePlaces;
import Utilities.Constants;
import Utilities.GlobalState;
import Utilities.Utility;
import data.ShopsContract;
import sync.SyncAdapter;

/**
 * Created by Narcis11 on 20.12.2014.
 */
public class ShopsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public ArrayAdapter<String> mShopsAdapter;
    private static final String LOG_TAG = ShopsFragment.class.getSimpleName();
    private String shopLatitude = "";
    private String shopLongitude = "";
    private String shopName = "";
    private static final int SHOPS_LOADER_ID = 0;//loader identifier
    //These are the columns used by the CursorLoader in the query.
    //TODO: Add the distance to user and distance duration after you finish the Directions API call
    public static final String[] SHOPS_COLUMNS = {
            ShopsContract.ShopsEntry.TABLE_NAME + "." + ShopsContract.ShopsEntry._ID,
            ShopsContract.ShopsEntry.COLUMN_SHOP_NAME,
            ShopsContract.ShopsEntry.COLUMN_SHOP_ADDRESS,
            ShopsContract.ShopsEntry.COLUMN_IS_OPEN
    };

    // These indices are tied to SHOPS_COLUMNS.  If SHOPS_COLUMNS changes, these
    // must change.
    public static final int COL_SHOP_ID = 0;
    public static final int COL_SHOP_NAME = 1;
    public static final int COL_SHOP_ADDRESS = 2;
    public static final int COL_IS_OPEN = 3;
    public static final int COL_DISTANCE_TO_USER = 4;
    public static final int COL_DISTANCE_DURATION = 5;

    public ShopsFragment() {
    }


    //TODO: Sort the list depending on the distance of the user to the shop
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.i(LOG_TAG,"User's Lat/Lng in fragment = " + GlobalState.USER_LAT + "/" + GlobalState.USER_LNG);
        mShopsAdapter = GlobalState.GLOBAL_ADAPTER;
        mShopsAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_shops,
                        R.id.list_item_shops_textview,
                        new ArrayList<String>()
                );
    //    Log.i(LOG_TAG,"Size of mShopsAdapter = " + mShopsAdapter.getCount());

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_shops);
        listView.setAdapter(mShopsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String placeDetails = mShopsAdapter.getItem(position);
                String coordinates = placeDetails.substring(placeDetails.indexOf(Constants.PIPE_SEPARATOR) + 1);
                shopLatitude = coordinates.substring(0, coordinates.indexOf(Constants.SLASH_SEPARATOR));
                shopLongitude = coordinates.substring(coordinates.indexOf(Constants.SLASH_SEPARATOR) + 1);
                shopName = placeDetails.substring(0,placeDetails.indexOf(Constants.COMMA_SEPARATOR));
                Intent openMap = new Intent(getActivity().getApplicationContext(),MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUNDLE_SHOP_LAT,shopLatitude);
                bundle.putString(Constants.BUNDLE_SHOP_LNG,shopLongitude);
                bundle.putString(Constants.BUNDLE_SHOP_NAME,shopName);
                bundle.putString(Constants.BUNDLE_FRAGMENT,Constants.CALLED_FROM_FRAGMENT);
                openMap.putExtras(bundle);
                startActivity(openMap);
            }
        });
        return rootView;
        /*
        String[] shops = {
                "Bike Nature - 0,7 km - 5 min",
                "Veloteca - 1 km - 7 min",
                "Moș Ion Roată - 1,4 km - 10 min",
                "Magazinul nr 3. - 1,8 km -  15 min",
                "Magazinul cu bomboane - 2,3 km - 20 min"
        };
        List<String> shopList = new ArrayList<String>(Arrays.asList(shops));
        // The ArrayAdapter takes data from a source and
        // populates the ListView it's attached to.
        ArrayAdapter<String> shopsAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_shops,
                R.id.list_item_shops_textview,
                shopList
        );
*/

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //tells the system that we have button(s) in the menu
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.shopsfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateShopList();
          //  GlobalState.GLOBAL_ADAPTER = mShopsAdapter;
          //  SyncAdapter.syncImmediately(getActivity());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG,"In fragment onStart()");
       // updateShopList();
    }

    public void updateShopList() {
        String[] coordinates = new String[2];
        coordinates[0] = GlobalState.USER_LAT;
        coordinates[1] = GlobalState.USER_LNG;
        Log.i(LOG_TAG,"Lat/lng in updateShopList - " + coordinates[0] + "/" + coordinates[1]);
        new FetchGooglePlaces(getActivity(), mShopsAdapter).execute(coordinates);
    }

    //loaders are initialised in onActivityCreated because their lifecycle is bound to the activity, not the fragment
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SHOPS_LOADER_ID,null,(android.app.LoaderManager.LoaderCallbacks<Cursor>) this);
        super.onActivityCreated(savedInstanceState);
    }

    //these three methods are required by the CursorLoader interface
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

