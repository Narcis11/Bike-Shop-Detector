package waldo.bike.bikeshops;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;
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
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "uD0QY3adOHScxd64i1dTOekQymn9aZVrQTbc9N5N", "VuRbrENiboj1i9tHg73xSa3FT58spKnMaSnH7O38");
        authConfig =
                new TwitterAuthConfig(Constants.CONSUMER_KEY,
                        Constants.CONSUMER_SECRET);
        try {
            Fabric.with(this, new Twitter(authConfig));
        }
        catch (Exception e) {

        }
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
