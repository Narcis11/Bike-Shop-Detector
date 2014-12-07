package Utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Narcis11 on 07.12.2014.
 */
public class DeviceConnection {

    private final Context mContext;
    public DeviceConnection(Context context){
        mContext = context;
    }
    public boolean checkInternetConnection () {

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
}
