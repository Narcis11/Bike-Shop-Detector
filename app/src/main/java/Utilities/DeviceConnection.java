package Utilities;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
/**
 * Created by Narcis11 on 07.12.2014.
 * This class is used to get the location and location updates of the user and check the internet connection status.
 */
public class DeviceConnection implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private final Context mContext;
    private static final String LOG_TAG = DeviceConnection.class.getSimpleName();

    public DeviceConnection(Context context){
        mContext = context;
    }

    public boolean checkInternetConnected() {

        ConnectivityManager connect = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connect != null) {
            NetworkInfo[] info = connect.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkInternetDisConnected() {

        ConnectivityManager connect = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connect != null) {
            NetworkInfo[] info = connect.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.DISCONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkInternetConnecting() {

        ConnectivityManager connect = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connect != null) {
            NetworkInfo[] info = connect.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTING) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkGpsEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        else {
            return false;
        }
    }
    //checking to see if Mobile Data is enabled and the user enables Wi-Fi in the same time
    //if Wi-Fi is enabling and Mobile Data is CONNECTED OR DISCONNECTING
    public boolean checkWifiDataToggle() {
        WifiManager wifiManager = (WifiManager)mContext.getSystemService(mContext.WIFI_SERVICE);
        ConnectivityManager connect = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED && ( (networkInfo.getState() == NetworkInfo.State.CONNECTED) || (networkInfo.getState() == NetworkInfo.State.DISCONNECTING))) {
            return true;
        }
        return false;
    }
    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(mContext);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.i(LOG_TAG,
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason.
            // resultCode holds the error code.
        } else {
            return false;
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(mContext,Constants.LOCATION_RECEIVED,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(mContext,Constants.LOCATION_LOST,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
