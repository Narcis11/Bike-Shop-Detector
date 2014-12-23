package waldo.bike.waldo;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import Places.FetchGooglePlaces;
import Utilities.Constants;
import Utilities.GlobalState;
import Utilities.Utility;

/**
 * Created by Narcis11 on 20.12.2014.
 */
public class ShopsFragment extends Fragment{

    public ArrayAdapter<String> mShopsAdapter;
    private static final String LOG_TAG = ShopsFragment.class.getSimpleName();
    public ShopsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Log.i(LOG_TAG,"Lat/Lng in fragment = " + GlobalState.latitude + "/" + GlobalState.longitude);
        mShopsAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_shops,
                        R.id.list_item_shops_textview,
                        new ArrayList<String>()
                );

        Log.i(LOG_TAG,"Size of mShopsAdapter = " + mShopsAdapter.getCount());

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_shops);
        listView.setAdapter(mShopsAdapter);

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
       // updateShopList();
    }




    private void updateShopList() {
        String[] coordinates = new String[2];
        coordinates[0] = GlobalState.latitude;
        coordinates[1] = GlobalState.longitude;
        Log.i(LOG_TAG,"Lat/lng in updateShopList - " + coordinates[0] + "/" + coordinates[1]);
        new FetchGooglePlaces(getActivity(), mShopsAdapter).execute(coordinates);
    }

}

