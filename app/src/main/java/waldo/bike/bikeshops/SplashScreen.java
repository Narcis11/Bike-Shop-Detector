package waldo.bike.bikeshops;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;


import Utilities.Constants;
import Utilities.DeviceConnection;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by nmihai on 09.12.2014.
 */
public class SplashScreen extends Activity{
    static AlertDialog mStaticInternetDialogue;
    static AlertDialog mStaticGPSDialogue;
    private static final String LOG_TAG = SplashScreen.class.getSimpleName();
    private static Context mContext;
    private IntentFilter mIntentFilter;
    private static boolean isGPSEnabled = false;
    private static boolean isInternetEnabled = false;
    //the Google Analytics tracker
    Tracker mGaTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPlayServices();
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
        mGaTracker = ((BikeShopsDetector) getApplication()).getTracker(
                BikeShopsDetector.TrackerName.APP_TRACKER);
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
        mStaticInternetDialogue = alertDialogBuilder.create();

        mStaticInternetDialogue.show();

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
            mGaTracker.send(new HitBuilders.ExceptionBuilder()
                    .setDescription("IllegalArgumentException in SplashScreen, onPause")
                    .setFatal(false)
                    .build());
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
                    if ((mStaticInternetDialogue != null) && mStaticInternetDialogue.isShowing()) {
                        mStaticInternetDialogue.cancel();
                        isInternetEnabled = true;
                    }
                    //onReceive is also called whenever we register the receiver in onResume, so we also have to double-check that the Internet is on
                    //TODO: Remove the comment from checkPlayServices before you release the app into production
                    if (isGPSEnabled && isInternetEnabled && checkPlayServices()) {
                        startMainActivity(mContext);
                        //open the main activity after two seconds

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
        mStaticGPSDialogue = alertDialogBuilder.create();
        mStaticGPSDialogue.show();
    }


    public static void startMainActivity(Context c) {
        Log.i(LOG_TAG,"In startMainActivity");

        Handler handler = new Handler();
        final Context context = c;
        final long DELAY_TIME = 2000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent MainActivityIntent = new Intent(context,MainActivity.class);
                MainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //without this flag, the main activity can't start
                context.startActivity(MainActivityIntent);
            }

        }, DELAY_TIME);
/*        Intent MainActivityIntent = new Intent(c,MainActivity.class);
        MainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //without this flag, the main activity can't start
        c.startActivity(MainActivityIntent);*/
    }

    private boolean checkPlayServices() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.i(LOG_TAG,"In checkPlayServices");
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mStaticGPSDialogue != null && mStaticGPSDialogue.isShowing()) {
            mStaticGPSDialogue.cancel();
        }

        if (mStaticInternetDialogue != null && mStaticInternetDialogue.isShowing()) {
            mStaticGPSDialogue.cancel();
        }
    }
}
