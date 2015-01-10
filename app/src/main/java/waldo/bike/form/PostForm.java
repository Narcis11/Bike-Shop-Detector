package waldo.bike.form;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Narcis11 on 10.01.2015.
 */
public class PostForm extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = PostForm.class.getSimpleName();
    @Override
    protected String doInBackground(String... params) {
        String jsonString = createJSONObject(params);
        return null;
    }

    protected String createJSONObject (String[] parameters) {
        JSONObject fullJson = new JSONObject();
        JSONObject locationJson = new JSONObject();
        String lat = "lat";
        String lng = "lng";
        String location = "location";
        String name = "name";
        String phone_number = "phone_number";
        String address = "address";
        String types = "types";
        String website = "website";
        try {
            locationJson.put(lat, parameters[0]);
            locationJson.put(lng,parameters[1]);
            fullJson.put(location,locationJson);
            fullJson.put(name,parameters[2]);
            if (parameters[3] != null) {
                fullJson.put(phone_number,parameters[3]);
            }
            fullJson.put(address,parameters[4]);
            fullJson.put(types,parameters[5]);
            if (parameters[6] != null) {
                fullJson.put(website,parameters[6]);
            }
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG,"JSON String is: " + fullJson.toString());
        return fullJson.toString();
    }
}
