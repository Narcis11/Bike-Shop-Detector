package sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Narcis11 on 26.12.2014.
 */
public class WaldoSyncService extends Service{
    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter sWaldoSyncAdapter = null;
    private static final String LOG_TAG = WaldoSyncService.class.getSimpleName();
    public WaldoSyncService() {
        super();
    }

    @Override
    public void onCreate() {
        Log.i(LOG_TAG, "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sWaldoSyncAdapter == null) {
                sWaldoSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sWaldoSyncAdapter.getSyncAdapterBinder();
    }
}
