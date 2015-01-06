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
    private long mLastSyncDate;
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
                    if (mFirstSync) {
                        SyncAdapter.syncImmediately(getContext());
                        mLastSyncDate = System.currentTimeMillis();
                        mFirstSync = false;
                    }
                    else {
                        Log.i(LOG_TAG,"Diff in time is " + (System.currentTimeMillis() - mLastSyncDate));
                        if (System.currentTimeMillis() - mLastSyncDate > SYNC_INTERVAL) {
                            SyncAdapter.syncImmediately(getContext());
                            mLastSyncDate = System.currentTimeMillis();
                        }
                        else {
                            Log.i(LOG_TAG,"Too early to sync");
                        }


                    }
                    Log.i(LOG_TAG,"Size of GlobalState.RESULT_LIST_GLOBAL = " + GlobalState.RESULT_LIST_GLOBAL.size());

                    resultList = GlobalState.RESULT_LIST_GLOBAL;

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    try {
                        filterResults.count = resultList.size();
                        Log.i(LOG_TAG,"filterResults.count = " + filterResults.count);
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
                    Log.i(LOG_TAG,"notified DataSetChanged");
                }
                else {
                    Log.i(LOG_TAG,"DataSetInvalidated()");
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }
}
