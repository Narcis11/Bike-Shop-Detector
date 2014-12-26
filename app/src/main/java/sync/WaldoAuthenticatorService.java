package sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.security.Provider;

/**
 * Created by Narcis11 on 26.12.2014.
 */
public class WaldoAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private WaldoAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new WaldoAuthenticator(this);
    }

    /*
      * When the system binds to this Service to make the RPC call
      * return the authenticator's IBinder.
      */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}

