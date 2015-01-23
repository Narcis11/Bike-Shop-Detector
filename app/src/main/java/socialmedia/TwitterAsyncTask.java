package socialmedia;

import android.os.AsyncTask;
import android.util.Log;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by nmihai on 23.01.2015.
 */
public class TwitterAsyncTask extends AsyncTask<Void,Void,Void> {
    private static final String LOG_TAG = TwitterAsyncTask.class.getSimpleName();
    @Override
    protected Void doInBackground(Void... params) {
        String screenName = "startupkitio";
        //TODO: Replace Twitter keys with those of Waldo
        final String CONSUMER_KEY = "Z5ubamevMWzVb8rqfnSBzN0dR";
        final String CONSUMER_SECRET_KEY = "bn8w8ibPjH56KfpC5LAj9CwZmyOUEtKQJohfps5PmwTJARvsJU";
        final String TOKEN = "2246081588-9YS4Y4hJDIOoCroeDhPYR4PmDSwFxSr1wnN9bcn";
        final String SECRET_TOKEN = "iHIfv0jz7WticHhrXjEtKu1m5ogZb5jiQQthtfHDCRrhn";
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET_KEY)
                .setOAuthAccessToken(TOKEN)
                .setOAuthAccessTokenSecret(SECRET_TOKEN);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        try {
            twitter.createFriendship(screenName);
            Log.i(LOG_TAG,"Followed you!");
        } catch (TwitterException e) {

            e.printStackTrace();
        }
        return null;
    }
}
