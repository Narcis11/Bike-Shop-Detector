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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop_form);
        EditText shopName = (EditText) findViewById(R.id.new_shop_name);
        shopName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(LOG_TAG,"In afterTextChanged Listener");
                checkShopName();
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
        EditText shopName = (EditText) findViewById(R.id.new_shop_name);
        TextView errorMessage = (TextView) findViewById(R.id.placeholder_text);
        if (shopName.getText().toString().length() == 0) {
            errorMessage.setText(getResources().getString(R.string.empty_shop_name));
            mShopNameOk = false;
        }
        else {
            errorMessage.setText("");
            mShopNameOk = true;
        }
    }


    public void addShop(View v) {
        EditText shopName = (EditText) findViewById(R.id.new_shop_name);

        EditText shopWebsiteAddress = (EditText) findViewById(R.id.new_shop_website);
        TextView errorMessage = (TextView) findViewById(R.id.placeholder_text);
        String wwwPrefix = "www";
        if (shopName.getText().toString().length() == 0) {
            errorMessage.setText(getResources().getString(R.string.empty_shop_name));
        }
/*        else if (shopWebsiteAddress.toString().indexOf(wwwPrefix) == 0 || !Patterns.WEB_URL.matcher(shopWebsiteAddress.getText()).matches()) {
            Log.i(LOG_TAG,"Invalid web address");
            errorMessage.setText(getResources().getString(R.string.invalid_url));
        }*/
    }
}
