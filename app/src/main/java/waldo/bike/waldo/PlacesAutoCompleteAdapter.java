package waldo.bike.waldo;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Calendar;
import java.lang.Math;
import Utilities.GlobalState;
import sync.SyncAdapter;

/**
 * Created by Narcis11 on 03.01.2015.
 */

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList;
    private boolean mFirstSync = true;
    private long    mFirstSyncDate;
    public static final int SYNC_INTERVAL = 1500;

    private static final String LOG_TAG = PlacesAutoCompleteAdapter.class.getSimpleName();
    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public String getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    GlobalState.SYNC_SHOPS = false;
                    GlobalState.INPUT = constraint.toString();
                   // Log.i(LOG_TAG,"Difference is = " + Math.abs(Math.abs( Math.abs(mSecondSyncDate) -  Math.abs(firstSyncDate))) );

                    //TODO: Fix sync delay!
                    SyncAdapter.syncImmediately(getContext());
                    //we sync only when we have 1,5s between key inputs
          /*          if (mFirstSync) {

                        mFirstSyncDate = System.currentTimeMillis();
                        mFirstSync = false;
                    }
                    else {
                        Log.i(LOG_TAG,"Diff in time is " + (System.currentTimeMillis() - mFirstSyncDate));
                        if (System.currentTimeMillis() - mFirstSyncDate > SYNC_INTERVAL) {
                            SyncAdapter.syncImmediately(getContext());
                        }
                        else {
                            Log.i(LOG_TAG,"Too early to sync");
                        }


                    }*/

                   // Log.i(LOG_TAG,"After sync call.");


                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    try {
                        filterResults.count = resultList.size();
                    }
                    catch (NullPointerException e) {
                        Log.e(LOG_TAG,"-------Null exception at line 52!------");
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }
}
