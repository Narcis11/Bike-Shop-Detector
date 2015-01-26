package waldo.bike.waldo;

import android.app.Application;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import Utilities.Constants;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Narcis11 on 26.01.2015.
 */
public class Waldo extends Application {
    private TwitterAuthConfig authConfig;
    @Override
    public void onCreate() {
        super.onCreate();
        authConfig =
                new TwitterAuthConfig(Constants.CONSUMER_KEY,
                        Constants.CONSUMER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
    }
}
