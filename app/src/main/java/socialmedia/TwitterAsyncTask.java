package socialmedia;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import Utilities.Constants;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by nmihai on 23.01.2015.
 */
public class TwitterAsyncTask extends AsyncTask<String,Void,String> {
    private static final String LOG_TAG = TwitterAsyncTask.class.getSimpleName();
    private Context mContext;
    @Override
    protected String doInBackground(String... params) {

        String screenName = "waldotheknight";
        final String USER_TOKEN = params[0].toString();
        final String USER_SECRET_TOKEN = params[1].toString();
        final String OPERATION = params[2].toString();
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(Constants.CONSUMER_KEY)
                .setOAuthConsumerSecret(Constants.CONSUMER_SECRET)
                .setOAuthAccessToken(USER_TOKEN)
                .setOAuthAccessTokenSecret(USER_SECRET_TOKEN);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        try {
            if (OPERATION.equals(Constants.TWITTER_FOLLOW)) {
                twitter.createFriendship(screenName);
                Log.i(LOG_TAG, "Followed you!");
            }
            else {
                twitter.destroyFriendship(screenName);
                Log.i(LOG_TAG, "UnFollowed you!");
            }
        } catch (TwitterException e) {

            e.printStackTrace();
            return Constants.RETURN_ERROR_STRING;
        }
        return Constants.OK_STATUS;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
