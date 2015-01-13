package waldo.bike.waldo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;


import java.util.prefs.PreferenceChangeListener;

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
public class SettingsActivity extends PreferenceActivity implements
        Preference.OnPreferenceChangeListener
        {

    private static final String LOG_TAG = SettingsActivity.class.getSimpleName();
    private static Context mContext;
    private String mOldPreferenceUnit = "";
    private String mNewPreferenceUnit = "";
    private PreferenceScreen mPreferenceScreen;
    private static final String KEY_METRIC_SCREEN = "metric_settings";
    private static final String KEY_IMPERIAL_SCREEN = "imperial_settings";
    private static boolean mFirstLoad;
    private static String mOldMetricRange = "";
    private static String mOldImperialRange = "";
    //private boolean loadedPrefGeneral = false;
    //private boolean loadedPrefGeneral = false;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    SharedPreferences mPrefs;
    PreferenceChangeListener mPreferenceListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        mFirstLoad = true;
        loadPreferenceScreen(mFirstLoad);
     //   addPreferencesFromResource(R.xml.pref_general);
       // addPreferencesFromResource(R.xml.pref_general);
    //    loadPreferenceScreen();
        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        mContext = getApplicationContext();
/*        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragment())
                .commit();*/

       // prefs.registerOnSharedPreferenceChangeListener(listener);
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
                Log.i(LOG_TAG,"***PREFERENCE IS*** = " + preference.toString());
                Log.i(LOG_TAG,"New preference: " + listPreference.getEntries()[prefIndex]);
                Log.i(LOG_TAG, "Old preference: " + ((ListPreference) preference).getValue());
                Log.i(LOG_TAG,"prefIndex = " + prefIndex);
                mOldPreferenceUnit = ((ListPreference) preference).getValue().toString();
                mNewPreferenceUnit = listPreference.getEntries()[prefIndex].toString();
                //if the old pref!=new pref && (new pref is Imperial or Metric)
                if (!mOldPreferenceUnit.equals(mNewPreferenceUnit) && (mNewPreferenceUnit.indexOf(getResources().getString(R.string.unit_array_metric))== 0
                        || mNewPreferenceUnit.indexOf(getResources().getString(R.string.unit_array_imperial)) ==0)) {
                        mFirstLoad = false;
                        loadPreferenceScreen(mFirstLoad);
                }

            } else {
                // For other preferences, set the summary to the value's simple string representation.
                //stringValue represents the values that do not correspond to the selected unit. e.g if it is imperial, it holds the range/speed in km
                Log.i(LOG_TAG,"stringValue is " + stringValue);
/*                if (preference.toString().indexOf(getResources().getString(R.string.pref_range_label)) > 0) {
                    Log.i(LOG_TAG, "preference is " + preference);
                    preference.setSummary(getResources().getString(R.string.pref_range_imperial_default));
                }*/
            }
        }
        else {
            Log.i(LOG_TAG,"preference is NULL!");
        }
        return true;
    }


    private void loadPreferenceScreen(boolean firstLoad) {
        String preferredUnit = Utility.getPreferredUnit(getApplicationContext());
     //   Log.i(LOG_TAG,"Preferred unit is " + preferredUnit);
            if (mNewPreferenceUnit.equals(getResources().getString(R.string.unit_array_metric))) {
                if (firstLoad) {
                    addPreferencesFromResource(R.xml.pref_general);
                    Log.i(LOG_TAG, "Loaded pref_general");
                }
                else {
                    //setting up the Range option
                    ListPreference rangeListPreference = (ListPreference) findPreference(getResources().getString(R.string.pref_range_key));
                    rangeListPreference.setEntries(R.array.range_values_array);
                    rangeListPreference.setSummary(Utility.getPreferredRangeMetric(getApplicationContext()));
                    Log.i(LOG_TAG,"Preferred range metric = " + Utility.getPreferredRangeMetric(getApplicationContext()));

                    //setting up the Speed option
                    ListPreference speedListPreference = (ListPreference) findPreference(getResources().getString(R.string.pref_speed_key));
                    speedListPreference.setEntries(R.array.speed_values_array);
                    speedListPreference.setSummary(Utility.getPreferredSpeed(getApplicationContext()));
                    Log.i(LOG_TAG,"Preferred speed metric: " + Utility.getPreferredSpeed(getApplicationContext()));
                    Log.i(LOG_TAG,"Changed metric range&speed");
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Log.i(LOG_TAG,"HARDCORE RANGE = " + prefs.getString("range","3 km"));
                }

            } else {
                if (firstLoad) {
                    addPreferencesFromResource(R.xml.pref_imperial_general);
                    Log.i(LOG_TAG, "Loaded pref_imperial_general");

                }
                else {
                    ListPreference rangeListPreference = (ListPreference) findPreference(getResources().getString(R.string.pref_range_key));
                    rangeListPreference.setEntries(R.array.range_values_imperial_array);
                    rangeListPreference.setSummary(Utility.getPreferredRangeImperial(getApplicationContext()));
                    Log.i(LOG_TAG,"Preferred range imperial = " + Utility.getPreferredRangeImperial(getApplicationContext()));

                    //setting up the Speed option
                    ListPreference speedListPreference = (ListPreference) findPreference(getResources().getString(R.string.pref_speed_key));
                    speedListPreference.setEntries(R.array.speed_values_array);
                    speedListPreference.setSummary(Utility.getPreferredSpeed(getApplicationContext()));
                    Log.i(LOG_TAG,"Preferred speed imperial: " + Utility.getPreferredSpeed(getApplicationContext()));
                    Log.i(LOG_TAG,"Changed imperial range&speed");
                }
            }
    }











            /*            Preference preferenceMetricSpeed = findPreference(getString(R.string.pref_speed_key));
            ListPreference listPreference = (ListPreference) preferenceMetricSpeed;
            int prefIndex = listPreference.findIndexOfValue(getResources().getString(R.string.pref_speed_default_metric));
            Log.i(LOG_TAG,"prefIndex in loadPreferenceScreen = " + prefIndex);
            try {
               // preferenceMetricSpeed.setDefaultValue("17 km/h");
                Log.i(LOG_TAG,preferenceMetricSpeed.toString());
                preferenceMetricSpeed.setSummary("17 km/h");
            }
            catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }*/
/*

            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

               Log.i(LOG_TAG,"Key is " + key);
                if (key.equals(KEY_PREF_SYNC_CONN)) {
                    Preference connectionPref = findPreference(key);
                    // Set summary to be the user-description for the selected value
                    Log.i(LOG_TAG,"sharedPreferences = " + sharedPreferences.getString(key, ""));
                    Log.i(LOG_TAG,"connectionPref = " + connectionPref.toString());
                    connectionPref.setSummary(sharedPreferences.getString(key, ""));
                    //sharedPreferences.registerOnSharedPreferenceChangeListener(this);
                    Log.i(LOG_TAG,"sharedPreferences = " + sharedPreferences.getString(key, ""));
                }
            }
*/


        public static class PrefFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.i(LOG_TAG, "Arguments: " + getArguments());
            addPreferencesFromResource(R.xml.pref_general);

        }
        }
}