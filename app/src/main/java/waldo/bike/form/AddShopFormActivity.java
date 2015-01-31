package waldo.bike.form;

import android.app.Activity;
import android.graphics.Color;
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

import Utilities.Constants;
import waldo.bike.waldo.R;

public class AddShopFormActivity extends Activity {

    public static final String LOG_TAG = AddShopFormActivity.class.getSimpleName();
    private static boolean mShopNameOk;
    private static boolean mShopWebsiteOk;
    private static boolean mShopPhoneNumberOk;
    EditText mShopName;
    EditText mShopWebsite;
    EditText mShopPhoneNumber;
    TextView mErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_form);
        mShopName = (EditText) findViewById(R.id.new_shop_name);
        mShopWebsite = (EditText) findViewById(R.id.new_shop_website);
        mShopPhoneNumber = (EditText) findViewById(R.id.new_shop_phone);
        mErrorMessage = (TextView) findViewById(R.id.placeholder_text);
        mShopNameOk = false;
        mShopWebsiteOk = true;
        mShopPhoneNumberOk = true;

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

        mShopPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkShopPhoneNumber();
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
        } //255 is maximum allowed length by Google for the shop's name
        else if (mShopName.getText().toString().length() > 254) {
            mErrorMessage.setText(getResources().getString(R.string.long_shop_name));
            mShopNameOk = false;
        }
        else {
            mErrorMessage.setText("");
            mShopNameOk = true;
        }
    }

    public void checkShopWebsite() {
        if (mShopWebsite.getText().toString().length() > 0) {
            if (!Patterns.WEB_URL.matcher(mShopWebsite.getText()).matches()) {
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

    public void checkShopPhoneNumber() {
        if (mShopPhoneNumber.getText().toString().length() > 0) {
            if (mShopPhoneNumber.getText().toString().length() < 7) {
                mErrorMessage.setText(getResources().getString(R.string.invalid_phone));
                mShopPhoneNumberOk = false;
            } else {
                mErrorMessage.setText("");
                mShopPhoneNumberOk = true;
            }
        }
        else {
            mErrorMessage.setText("");
            mShopPhoneNumberOk = true;
        }
    }

    public void addShop(View v) {
        if (mShopWebsite.getText().toString().length() == 0) {
            //this field is not mandatory
            mShopWebsiteOk = true;
        }
        if (mShopWebsiteOk && mShopNameOk && mShopPhoneNumberOk) {
            Log.i(LOG_TAG,"OK to submit form");
            mShopName.setBackgroundColor(getResources().getColor(R.color.temporary_form_background));
            mShopWebsite.setBackgroundColor(getResources().getColor(R.color.temporary_form_background));
            mShopPhoneNumber.setBackgroundColor(getResources().getColor(R.color.temporary_form_background));
            Bundle bundle = getIntent().getExtras();
            Double latitude = bundle.getDouble(Constants.ADD_SHOP_BUNDLE_LAT_KEY);
            Double longitude = bundle.getDouble(Constants.ADD_SHOP_BUNDLE_LNG_KEY);
            String address = bundle.getString(Constants.ADD_SHOP_BUNDLE_ADDRESS_KEY);
            String[] postParameters = new String[10];
            //we follow the order of the parameters from the Google API request (https://developers.google.com/places/documentation/actions#adding_a_place)
            postParameters[0] = String.valueOf(latitude);
            postParameters[1] = String.valueOf(longitude);
            postParameters[2] = mShopName.getText().toString();
            if (mShopPhoneNumber.getText().toString() != null) {
                postParameters[3] = mShopPhoneNumber.getText().toString();
            }
            postParameters[4] = address;
            postParameters[5] = Constants.PLACE_TYPE;
            if (mShopWebsite.getText().toString() != null) {
                postParameters[6] = mShopWebsite.getText().toString();
            }

            PostForm postForm = new PostForm();
            postForm.execute(postParameters);
        }
        else {
            mErrorMessage.setText(getResources().getString(R.string.invalid_form));
            if (!mShopNameOk) {
                mShopName.setBackgroundColor(getResources().getColor(R.color.invalid_field_color));
            }
            else {
                mShopName.setBackgroundColor(getResources().getColor(R.color.temporary_form_background));
            }

            if (!mShopWebsiteOk) {
                mShopWebsite.setBackgroundColor(getResources().getColor(R.color.invalid_field_color));
            }
            else {
                mShopWebsite.setBackgroundColor(getResources().getColor(R.color.temporary_form_background));
            }
            if (!mShopPhoneNumberOk) {
                mShopPhoneNumber.setBackgroundColor(getResources().getColor(R.color.invalid_field_color));
            }
            else {
                mShopPhoneNumber.setBackgroundColor(getResources().getColor(R.color.temporary_form_background));
            }
        }
    }
}
