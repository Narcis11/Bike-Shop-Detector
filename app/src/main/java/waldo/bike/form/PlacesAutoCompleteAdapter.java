package waldo.bike.form;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import Places.FetchPlacesAutocomplete;
import Utilities.Constants;
import waldo.bike.waldo.R;

/**
 * Created by Narcis11 on 03.01.2015.
 */

public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private ArrayList<String> resultList;
    private boolean mFirstSync = true;
    private long mLastSyncDate;
    public static final int SYNC_INTERVAL = 1500;
    private Context mContext;
    private static final String LOG_TAG = PlacesAutoCompleteAdapter.class.getSimpleName();
    private boolean mIsTextFormatted = false;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.i(LOG_TAG,"convertView: " + convertView.toString());
       // Log.i(LOG_TAG,"Parent: " + parent.toString());
/*        for (int i = 0; i < 3; i++) {
            try {
                if (parent.getChildAt(i) != null)
                Log.i(LOG_TAG, "Child at " + i + "=" + parent.getChildAt(i).toString());
            }
            catch (NullPointerException e) {
                Log.i(LOG_TAG,e.getMessage());
            }

        }*/
        TextView resultView = (TextView) parent.findViewById(R.id.autocomplete);
        if (resultView != null && !mIsTextFormatted) {
                Log.i(LOG_TAG, "resultView: " + resultView.getText());
                String text = resultView.getText().toString();
                String address = text.substring(0, text.indexOf(Constants.COMMA_SEPARATOR));
                String location = text.substring(text.indexOf(Constants.COMMA_SEPARATOR) + 1);
                resultView.setText(address + "\n" + location);
                mIsTextFormatted = true;
        }
        return super.getView(position, convertView, parent);
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.i(LOG_TAG,"In getFilter");
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
