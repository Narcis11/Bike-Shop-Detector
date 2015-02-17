package waldo.bike.bikeshops;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.HashMap;

import Utilities.Constants;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Narcis11 on 26.01.2015.
 * We used this class to Instantiate the Fabric for using Twitter.
 */
public class BikeShopsDetector extends Application {
    private TwitterAuthConfig authConfig;
    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     *
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
    }
    private static final String PROPERTY_ID = "UA-55296895-2";
    private static final String PARSE_APP_ID = "RVmM8akk2DxY5T4EZwPOEKRWlAMXT6E9TbNTRbDW";
    private static final String PARSE_CLIENT_KEY = "N17hBgzIkAnYHSifVofBp4GFdee384SUl4XRFGHb";
    private static final String LOG_TAG = BikeShopsDetector.class.getSimpleName();
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
        authConfig =
                new TwitterAuthConfig(Constants.CONSUMER_KEY,
                        Constants.CONSUMER_SECRET);
        try {
            Fabric.with(this, new Twitter(authConfig));
        }
        catch (Exception e) {

        }
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(LOG_TAG, "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e(LOG_TAG, "failed to subscribe for push", e);
                }
            }
        });
    }
    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : analytics.newTracker(R.xml.app_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
