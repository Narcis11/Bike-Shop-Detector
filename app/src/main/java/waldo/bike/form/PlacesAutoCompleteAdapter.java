package waldo.bike.form;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import Places.FetchPlacesAutocomplete;

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
                    //we only sync if there are at least three letters
                    if (constraint.toString().length() > 2) {
                        Log.i(LOG_TAG, "Started syncing");
                        FetchPlacesAutocomplete fetchPlacesAutocomplete = new FetchPlacesAutocomplete(getContext());
                        resultList = fetchPlacesAutocomplete.autocomplete(constraint.toString());
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
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
