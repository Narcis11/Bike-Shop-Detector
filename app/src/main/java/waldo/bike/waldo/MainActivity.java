package waldo.bike.waldo;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.zip.CheckedOutputStream;

import Utilities.Constants;
import Utilities.DeviceConnection;


public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static Context mContext;
    private static boolean firstLoad = true;
    private static String previousNetworkState = "CONNECTED"; //main activity only loads if there's Internet connection, so it's safe to assign this value
    private TextView mLocationView;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        //instantiante the action bar
        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.drawable.waldo_action_bar);
        actionBar.setTitle("");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));
        mLocationView = new TextView(this);

       // setContentView(mLocationView); CRASHES BECAUSE OF THIS LINE

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mContext = getApplicationContext();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MainNetworkReceiver extends BroadcastReceiver {
        public MainNetworkReceiver() {

            super();
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            DeviceConnection deviceConnection = new DeviceConnection(context);
            if (!firstLoad) { //if this is the first load of the Activity, we need to ignore network changes
                Log.i(LOG_TAG, "Main Activity: Network state changed!");
                //we don't display the message if the user turns on wifi when data connection is turned on or viceversa
                if (deviceConnection.checkInternetConnected() && (!previousNetworkState.equals("CONNECTED"))) {
                    Toast.makeText(context, "Reconnected!", Toast.LENGTH_SHORT).show();
                    previousNetworkState = Constants.PREVIOUS_STATE_CONNECTED;
                }
                if (deviceConnection.checkInternetDisConnected() && !(deviceConnection.checkInternetConnected() || deviceConnection.checkInternetConnecting())) {
                    Toast.makeText(context, "Lost Internet Connection!", Toast.LENGTH_SHORT).show();
                    previousNetworkState = Constants.PREVIOUS_STATE_DISCONNECTED;
                }
            }
            firstLoad = false;
        }

    }
    //required methods for managing the location
    @Override
    public void onLocationChanged(Location location) {
        mLocationView.setText("Location received: " + location.toString());
        Log.i(LOG_TAG,"Location is " + location.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG_TAG,"Connected to GPS");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "GoogleApiClient connection has failed");
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
