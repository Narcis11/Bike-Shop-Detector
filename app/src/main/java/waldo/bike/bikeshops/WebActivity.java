package waldo.bike.bikeshops;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import Utilities.Constants;

/*Created by Narcis.
* This class is used to open our website or a shop's website.*/
public class WebActivity extends Activity {
    private WebView mWebView;
    Bundle mWebsiteBundle;
    private static String mUrl = "";
    private static final String mDefaultWebsite = "http://www.waldo.bike/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
        mWebsiteBundle = getIntent().getExtras();
        mUrl = mWebsiteBundle.getString(Constants.BUNDLE_WEBSITE,mDefaultWebsite);//we load our website in case the bundle is null
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

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.xml.slide_in, R.xml.slide_out);
    }
}
