package waldo.bike.waldo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;


import Utilities.Constants;
import Utilities.DeviceConnection;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by nmihai on 09.12.2014.
 */
public class SplashScreen extends Activity{
    static AlertDialog staticDialog = null;
    private static final String LOG_TAG = SplashScreen.class.getSimpleName();
    private static Context mContext;
    private IntentFilter mIntentFilter;
    private static boolean isGPSEnabled = false;
    private static boolean isInternetEnabled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);
        mContext = getApplicationContext(); //needed to start the Main Activity
        //instantiate the filter and assign a value
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Constants.BROADCAST_ACTION);
        //instantiante the action bar
/*        ActionBar actionBar = getActionBar();
        actionBar.setIcon(R.drawable.waldo_action_bar);
        actionBar.setTitle("");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFFFFF")));*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        DeviceConnection deviceConnection = new DeviceConnection(mContext);
        Log.i(LOG_TAG,"in onResume");
        if (!deviceConnection.checkGpsEnabled()) {
            showGPSDisabledAlertToUser();
        }
        else {
            isGPSEnabled = true;
        }
        //Log.i(LOG_TAG,"Outside if in onResume()");
        if (isGPSEnabled && isInternetEnabled) {
            startMainActivity(mContext);
        }
        registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showInternetDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.internet_is_disabled)
                .setCancelable(false)
                .setPositiveButton(R.string.enable_internet,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callSettingIntent = new Intent(
                                        Settings.ACTION_SETTINGS); //open Settings screen
                                startActivity(callSettingIntent);
                            }
                        });
        staticDialog = alertDialogBuilder.create();

        staticDialog.show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mBroadcastReceiver != null) {
                unregisterReceiver(mBroadcastReceiver);
            }
        }
        catch (IllegalArgumentException e) {
            Log.e(LOG_TAG,e.toString());
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //onReceive is called every time the receiver is registered in onResume. We check for the internet connectivity here and for the GPS
            //in onResume.
            if (mContext != null) {
                DeviceConnection deviceConnection = new DeviceConnection(mContext);
                if (!deviceConnection.checkInternetConnected()) {
                    showInternetDisabledAlertToUser();
                    isInternetEnabled = false;
                } else {
                    isInternetEnabled = true;
                    if ((staticDialog != null) && staticDialog.isShowing()) {
                        staticDialog.cancel();
                        isInternetEnabled = true;
                    }
                    //onReceive is also called whenever we register the receiver in onResume, so we also have to double-check that the Internet is on
                    if (isGPSEnabled && isInternetEnabled) {
                        startMainActivity(mContext);
                    }
                }
            } else {
                Log.i(LOG_TAG, "mContext is null ");
            }
        };
    };

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.gps_is_disabled)
                .setCancelable(false)
                .setPositiveButton(R.string.enable_gps,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);//open GPS Settings screen
                                startActivity(callGPSSettingIntent);
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public static void startMainActivity(Context c) {
        Intent MainActivityIntent = new Intent(c,MainActivity.class);
        MainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //without this flag, the main activity can't start
        c.startActivity(MainActivityIntent);
    }
}
