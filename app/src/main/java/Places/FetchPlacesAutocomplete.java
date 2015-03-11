package Places;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import Utilities.Constants;
import Utilities.GlobalState;
import Utilities.Utility;

/**
 * Created by Narcis11 on 03.01.2015.
 */
public class FetchPlacesAutocomplete {

    private final Context mContext;

    public FetchPlacesAutocomplete(Context context) {
        mContext = context;
    }

    public ArrayList<String> autocomplete(String input) {

        final String LOG_TAG = FetchPlacesAutocomplete.class.getSimpleName();

        final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
        final String TYPE_AUTOCOMPLETE = "/autocomplete";
        final String OUT_JSON = "/json";

        final String API_KEY = Constants.API_KEY;
        String country_code = Utility.getCountryCodeFromCoordinates(mContext);
        //US country code is returned incorrectly
        if (country_code.equals("st")) country_code = "us";
        //UK country code is returned incorrectly
        if (country_code.equals("re")) country_code = "uk";
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
            try {
                StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
                sb.append("?key=" + API_KEY);
                sb.append("&components=country:" + country_code);
                sb.append("&input=" + URLEncoder.encode(input, "utf8"));
             //   Log.i(LOG_TAG,"URL is: " + sb.toString());
                URL url = new URL(sb.toString());
                conn = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(conn.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
            } catch (MalformedURLException e) {
               // Log.e(LOG_TAG, "Error processing Places API URL", e);
                return resultList;
            } catch (IOException e) {
               // Log.e(LOG_TAG, "Error connecting to Places API", e);
                return resultList;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            try {
                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                // Extract the Place descriptions from the results
                resultList = new ArrayList<String>(predsJsonArray.length());
                for (int i = 0; i < predsJsonArray.length(); i++) {
                    resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                }
            } catch (JSONException e) {
              //  Log.e(LOG_TAG, "Cannot process JSON results", e);
            }

           // Log.i(LOG_TAG,"End of sync with resultList.size = " + resultList.size());
/*            for (int i = 0; i < resultList.size(); i ++ ) {
                Log.i(LOG_TAG,resultList.get(i));
            }*/
        return resultList;
    }
}
