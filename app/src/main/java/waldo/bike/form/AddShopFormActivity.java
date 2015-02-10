package waldo.bike.form;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.provider.Settings;
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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import Utilities.Constants;
import Utilities.GlobalState;
import Utilities.Utility;
import waldo.bike.waldo.MainActivity;
import waldo.bike.waldo.R;

public class AddShopFormActivity extends Activity {

    public static final String LOG_TAG = AddShopFormActivity.class.getSimpleName();
    private static boolean mShopNameOk;
    private static boolean mShopWebsiteOk;
    private static boolean mShopPhoneNumberOk;
    private static final String OK_STATUS = "ok";
    private static final String ERROR_STATUS = "error";
    private EditText mShopName;
    private EditText mShopWebsite;
    private EditText mShopPhoneNumber;
    private TextView mInfoMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_form);
        mShopName = (EditText) findViewById(R.id.new_shop_name);
        mShopWebsite = (EditText) findViewById(R.id.new_shop_website);
        mShopPhoneNumber = (EditText) findViewById(R.id.new_shop_phone);
        mInfoMessage = (TextView) findViewById(R.id.add_shop_status);
        mShopNameOk = false;
        mShopWebsiteOk = true;
        mShopPhoneNumberOk = true;



        mShopName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               if (!hasFocus) {
                   checkShopName();
               }
            }
        });

        mShopWebsite.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkShopWebsite();
                }
            }
        });

        mShopPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkShopPhoneNumber();
                }
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
        Log.i(LOG_TAG, "In checkShopName");
        if (mShopName.getText().toString().length() > 0) {
            if (mShopName.getText().toString().length() < 4) {
                mInfoMessage.setVisibility(View.VISIBLE);
                mInfoMessage.setText(getResources().getString(R.string.short_shop_name));
                mShopNameOk = false;
            } //255 is maximum allowed length by Google for the shop's name
            else if (mShopName.getText().toString().length() > 254) {
                mInfoMessage.setVisibility(View.VISIBLE);
                mInfoMessage.setText(getResources().getString(R.string.long_shop_name));
                mShopNameOk = false;
            } else {
                mInfoMessage.setText("");
                mInfoMessage.setVisibility(View.INVISIBLE);
                mShopNameOk = true;
            }
        }
    }

    public void checkShopWebsite() {
        if (mShopWebsite.getText().toString().length() > 0) {
            if (!Patterns.WEB_URL.matcher(mShopWebsite.getText()).matches()) {
                mInfoMessage.setVisibility(View.VISIBLE);
                mInfoMessage.setText(getResources().getString(R.string.invalid_url));
                mShopWebsiteOk = false;
            } else {
                mInfoMessage.setText("");
                mInfoMessage.setVisibility(View.INVISIBLE);
                mShopWebsiteOk = true;
            }
        }
        else { //the field is not mandatory, so it's ok if it's empty
            mInfoMessage.setText("");
            mInfoMessage.setVisibility(View.INVISIBLE);
            mShopWebsiteOk = true;
        }
    }

    public void checkShopPhoneNumber() {
        if (mShopPhoneNumber.getText().toString().length() > 0) {
            if (mShopPhoneNumber.getText().toString().length() < 7) {
                mInfoMessage.setVisibility(View.VISIBLE);
                mInfoMessage.setText(getResources().getString(R.string.invalid_phone));
                mShopPhoneNumberOk = false;
            } else {
                mInfoMessage.setText("");
                mInfoMessage.setVisibility(View.INVISIBLE);
                mShopPhoneNumberOk = true;
            }
        }
        else { //the field is not mandatory, so it's ok if it's empty
            mInfoMessage.setText("");
            mInfoMessage.setVisibility(View.INVISIBLE);
            mShopPhoneNumberOk = true;
        }
    }
    //This method is when the user presses "Add shop". It checks the form and displays an info message accordingly.
    public void addShop(View v) {
        final long DELAY_TIME = 2500;
        if (mShopWebsite.getText().toString().length() == 0) {
            //this field is not mandatory
            mShopWebsiteOk = true;
        }
        if (mShopWebsiteOk && mShopNameOk && mShopPhoneNumberOk) {
            Log.i(LOG_TAG,"OK to submit form");
            //styling the shop add status
            mInfoMessage.setVisibility(View.VISIBLE);
            mInfoMessage.setBackgroundColor(getResources().getColor(R.color.add_shop_pending));
            mInfoMessage.setText(getResources().getString(R.string.add_shop_pending));
            //styling the form's fields
            mShopName.setBackgroundColor(getResources().getColor(R.color.list_background));
            mShopWebsite.setBackgroundColor(getResources().getColor(R.color.list_background));
            mShopPhoneNumber.setBackgroundColor(getResources().getColor(R.color.list_background));
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
            String response = "";
            try {
                response = postForm.get();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (response.indexOf(OK_STATUS) >= 0) {
                mInfoMessage.setVisibility(View.VISIBLE);
                mInfoMessage.setBackgroundColor(getResources().getColor(R.color.map_textview));
                mInfoMessage.setText(getResources().getString(R.string.add_shop_finished));
                //open the main activity after two seconds
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(mainActivity);
                    }

                }, DELAY_TIME);
            }
            else if (response.indexOf(ERROR_STATUS) >= 0) {
                mInfoMessage.setVisibility(View.VISIBLE);
                mInfoMessage.setBackgroundColor(Color.RED);
                mInfoMessage.setText(getResources().getString(R.string.add_shop_failed));
            }
        }
        else {
            mInfoMessage.setVisibility(View.VISIBLE);
            mInfoMessage.setText(getResources().getString(R.string.invalid_form));
            if (!mShopNameOk) {
                mShopName.setBackgroundColor(getResources().getColor(R.color.invalid_field_color));
            }
            else {
                mShopName.setBackgroundColor(getResources().getColor(R.color.list_background));
            }

            if (!mShopWebsiteOk) {
                mShopWebsite.setBackgroundColor(getResources().getColor(R.color.invalid_field_color));
            }
            else {
                mShopWebsite.setBackgroundColor(getResources().getColor(R.color.list_background));
            }
            if (!mShopPhoneNumberOk) {
                mShopPhoneNumber.setBackgroundColor(getResources().getColor(R.color.invalid_field_color));
            }
            else {
                mShopPhoneNumber.setBackgroundColor(getResources().getColor(R.color.list_background));
            }
        }
    }
}
