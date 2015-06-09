package waldo.bike.form;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import waldo.bike.bikeshops.BikeShopsDetector;

/**
 * Created by Narcis11 on 10.01.2015.
 */
public class PostForm extends AsyncTask<String, Void, String> {

    private static final String LOG_TAG = PostForm.class.getSimpleName();
    Application mApplication;
    public PostForm(Application application) {
        mApplication = application;
    }
    @Override
    protected String doInBackground(String... params) {
        Tracker mGaTracker = ((BikeShopsDetector)  mApplication).getTracker(
                BikeShopsDetector.TrackerName.APP_TRACKER);
        String jsonString = createJSONObject(params, mGaTracker);
       // String url = "https://maps.googleapis.com/maps/api/place/add/json?key=" + Constants.API_KEY;
        String url = "http://app.waldo.bike:8888/places";
        String ERROR_STATUS = "error";
        HttpPost httpPost = new HttpPost(url);
        HttpClient httpClient = new DefaultHttpClient();
        String status = "";
        try {
            StringEntity stringEntity = new StringEntity(jsonString, HTTP.UTF_8);
            httpPost.setEntity(stringEntity);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response = httpClient.execute(httpPost, responseHandler);
            status = response.toString();
        }
        catch (Exception e){
            mGaTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription("Exception in PostForm, doInBackground")
                    .setFatal(false)
                    .build());
            return ERROR_STATUS;
        }
        return status;
    }

    protected String createJSONObject (String[] parameters, Tracker gaTracker) {
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
        //param[0]=lang; param[1]=lng; param[2]=name; param[3]=phone_number; param[4]=address; param[5]=types; param[6]=website;
        try {
            fullJson.put(name,parameters[2]);
            locationJson.put(lat, parameters[0]);
            locationJson.put(lng,parameters[1]);
            fullJson.put(location,locationJson);
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
/*            gaTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription("JSONException in PostForm, createJSONObject")
                    .setFatal(false)
                    .build());*/
        }
        catch(NullPointerException e) {
/*            gaTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription("NullPointerException in PostForm, createJSONObject")
                    .setFatal(false)
                    .build());*/
        }
        return fullJson.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
