package waldo.bike.waldo;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import Utilities.Constants;
import Utilities.DeviceConnection;


public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    int countResume = 0;
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
        //check if there's Internet Connection
        DeviceConnection deviceConnection = new DeviceConnection(getApplicationContext());
        if (!deviceConnection.checkInternetConnection()) {
            showInternetDisabledAlertToUser();
            Log.i(LOG_TAG, "in OnCreate() Internet");
            //Toast.makeText(getApplicationContext(), Constants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
        }
        //check if GPS in enabled
        if (deviceConnection.checkGpsEnabled()) {

            Toast.makeText(getApplicationContext(), Constants.GPS_IS_ENABLED, Toast.LENGTH_SHORT).show();
        }
        else {
            Log.i(LOG_TAG, "in OnCreate() GPS");
            showGPSDisabledAlertToUser();
            //Toast.makeText(getApplicationContext(), Constants.GPS_DISABLED, Toast.LENGTH_SHORT).show();
        }
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(Constants.GPS_IS_DISABLED)
                .setCancelable(false)
                .setPositiveButton(Constants.ENABLE_GPS,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(Constants.CANCEL_BUTTON,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void showInternetDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(Constants.INTERNET_IS_DISABLED)
                .setCancelable(false)
                .setPositiveButton(Constants.ENABLE_INTERNET,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callSettingIntent = new Intent(
                                        Settings.ACTION_SETTINGS);
                                startActivity(callSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(Constants.CANCEL_BUTTON,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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
        if (countResume == 0) {
            countResume+=1;
        }
        else {
        DeviceConnection deviceConnection = new DeviceConnection(getApplicationContext());
        if (!deviceConnection.checkInternetConnection()) {
            Log.i(LOG_TAG, "in OnResume()");
            showInternetDisabledAlertToUser();
            //Toast.makeText(getApplicationContext(), Constants.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT).show();
        }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
