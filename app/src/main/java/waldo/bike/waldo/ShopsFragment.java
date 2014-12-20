package waldo.bike.waldo;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import Utilities.Constants;
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
        //adding some dummy data for the moment
        mShopsAdapter =
                new ArrayAdapter<String>(
                  getActivity(),
                  R.layout.list_item_shops,
                  R.id.list_item_shops_textview,
                  new ArrayList<String>()
                );
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

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_shops);
        listView.setAdapter(shopsAdapter);

        return rootView;
    }


}

