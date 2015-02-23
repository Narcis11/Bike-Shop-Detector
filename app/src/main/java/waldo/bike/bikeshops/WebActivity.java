package waldo.bike.bikeshops;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import Utilities.Constants;

/*Created by Narcis.
* This class is used to open our website or a shop's website.*/
public class WebActivity extends Activity {
    private WebView mWebView;
    private Bundle mBundle;
    private static final String LOG_TAG = WebActivity.class.getSimpleName();
    private static String mUrl = "";
    private static String mTitle = "";
    private static final String mDefaultUrl = "http://www.waldo.bike";
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_web);
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
        //enable the back button in the action bar
        getActionBar().setDisplayHomeAsUpEnabled(true);
        mBundle = getIntent().getExtras();
        mUrl = mBundle.getString(Constants.BUNDLE_WEBSITE);//can't be null, because we always send a website
        mTitle = mBundle.getString(Constants.BUNDLE_WEBVIEW_TITLE);////can't be null, because we always send a title
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);//without this line, the bar wouldn't appear at the second page load
        getActionBar().setTitle(mTitle);
        mWebView = (WebView) findViewById(R.id.website_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                return true;
                //return super.shouldOverrideUrlLoading(view, url);
            }

        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
                Log.i(LOG_TAG,"Progress is: " + newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }


        });
        mWebView.loadUrl(mUrl);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.home:
                if (mUrl.equals(mDefaultUrl)) {
                    Intent mainActivityIntent = new Intent(this,MainActivity.class);
                    startActivity(mainActivityIntent);
                }
                else {
                    Intent shopDetailsIntent = new Intent(this,ShopDetailActivity.class);
                    shopDetailsIntent.putExtras(mBundle);
                    startActivity(shopDetailsIntent);
                }
        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        if (mUrl.equals(mDefaultUrl)) {
            Intent mainActivityIntent = new Intent(this,MainActivity.class);
            return mainActivityIntent;
        }
        else {
            Intent shopDetailsIntent = new Intent(this,ShopDetailActivity.class);
            shopDetailsIntent.putExtras(mBundle);
            return shopDetailsIntent;
        }
    }
}
