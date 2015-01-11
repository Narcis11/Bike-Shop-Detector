package waldo.bike.waldo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;


import java.util.List;

import Utilities.Utility;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener{

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();
    private static Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        //loadPreferenceScreen();
       // addPreferencesFromResource(R.xml.pref_general);
        loadPreferenceScreen();
        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        mContext = getApplicationContext();
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_range_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_speed_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_unit_key)));
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
                Log.i(LOG_TAG, "Modified preference is " + ((ListPreference) preference).getValue());
                if (((ListPreference) preference).getValue() != null) {
                    if (((ListPreference) preference).getValue().equals(Utility.getPreferredUnit(mContext))) {
                        addPreferencesFromResource(R.xml.pref_general);
                    }
                }
            } else {
                // For other preferences, set the summary to the value's simple string representation.
                preference.setSummary(stringValue);
            }
        }
        else {
            Log.i(LOG_TAG,"preference is NULL!");
        }
        return true;
    }


    private void loadPreferenceScreen() {
        String preferredUnit = Utility.getPreferredUnit(getApplicationContext());
        Log.i(LOG_TAG,"Preferred unit is " + preferredUnit);
        if (preferredUnit.equals(getResources().getString(R.string.unit_array_metric))) {
            addPreferencesFromResource(R.xml.pref_general);
            Log.i(LOG_TAG, "Loaded pref_general");
        }
        else {
            addPreferencesFromResource(R.xml.pref_imperial_general);
            Log.i(LOG_TAG, "Loaded pref_imperial_general");
        }
    }
}