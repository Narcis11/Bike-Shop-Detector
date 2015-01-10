package waldo.bike.form;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.regex.Pattern;

import waldo.bike.waldo.R;

public class AddShopFormActivity extends ActionBarActivity {

    public static final String LOG_TAG = AddShopFormActivity.class.getSimpleName();
    private static boolean mShopNameOk = false;
    private static boolean mShopWebsiteOk = true;
    EditText mShopName;
    EditText mShopWebsite;
    TextView mErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_form);
        mShopName = (EditText) findViewById(R.id.new_shop_name);
        mShopWebsite = (EditText) findViewById(R.id.new_shop_website);
        mErrorMessage = (TextView) findViewById(R.id.placeholder_text);

        mShopName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkShopName();
            }
        });

        mShopWebsite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkShopWebsite();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_shop_form, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    public void checkShopName () {
        if (mShopName.getText().toString().length() == 0) {
            mErrorMessage.setText(getResources().getString(R.string.empty_shop_name));
            mShopNameOk = false;
        }
        else {
            mErrorMessage.setText("");
            mShopNameOk = true;
        }
    }

    public void checkShopWebsite() {
        String wwwPrefix = "www";
        //mShopWebsite.toString().indexOf(wwwPrefix) == 0 ||
        if (mShopWebsite.getText().toString().length() > 0) {
            if (!Patterns.WEB_URL.matcher(mShopWebsite.getText()).matches()) {
                Log.i(LOG_TAG, "Invalid web address");
                mErrorMessage.setText(getResources().getString(R.string.invalid_url));
                mShopWebsiteOk = false;
            } else {
                mErrorMessage.setText("");
                mShopWebsiteOk = true;
            }
        }
        else {
            mErrorMessage.setText("");
            mShopWebsiteOk = true;
        }
    }

    public void addShop(View v) {
        if (mShopWebsite.getText().toString().length() == 0) {
            //this field is not mandatory
            mShopWebsiteOk = true;
        }
        if (mShopWebsiteOk && mShopNameOk) {
            Log.i(LOG_TAG,"OK to submit form");
        }
        else {
            mErrorMessage.setText(getResources().getString(R.string.invalid_form));
        }
    }
}
