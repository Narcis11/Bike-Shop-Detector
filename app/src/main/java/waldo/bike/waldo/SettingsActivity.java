package waldo.bike.waldo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;


import java.util.List;
import java.util.prefs.PreferenceChangeListener;

import Utilities.GlobalState;
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
    private static String mOldMetricRange = "";
    private static String mOldImperialRange = "";
    private static boolean mFirstLoad;
    private static String mMetric = "Metric";
    private static String mImperial = "Imperial";
    private static String[] metricPreferences = new String[5];
    private static String[] imperialPreferences = new String[5];
    //private boolean loadedPrefGeneral = false;
    //private boolean loadedPrefGeneral = false;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    SharedPreferences mPrefs;
    PreferenceChangeListener mPreferenceListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file

    //    loadPreferenceScreen();
        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        mContext = getApplicationContext();
/*        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragment())
                .commit();*/

        mFirstLoad = true;
        loadPreferenceScreen(mFirstLoad);
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
         //       Log.i(LOG_TAG,"***PREFERENCE IS*** = " + preference.toString());
                Log.i(LOG_TAG,"New preference: " + listPreference.getEntries()[prefIndex]);
                Log.i(LOG_TAG, "Old preference: " + ((ListPreference) preference).getValue());
          //      Log.i(LOG_TAG,"prefIndex = " + prefIndex);
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
        String oldPreferredUnit = Utility.getPreferredUnit(getApplicationContext());
        String toUsePreferredUnit = "";
        String correctSummaryRange = "";
        String correctSummarySpeed = "";
            if (!mNewPreferenceUnit.equals("")) {
                toUsePreferredUnit = mNewPreferenceUnit;
            } else {
                toUsePreferredUnit = oldPreferredUnit;
            }
        Log.i(LOG_TAG,"toUsePreferredUnit = " + toUsePreferredUnit );
            if (toUsePreferredUnit.equals(getResources().getString(R.string.unit_array_metric))) {
                if (firstLoad) {
                    addPreferencesFromResource(R.xml.pref_metric_general);
                    Log.i(LOG_TAG, "Loaded pref_metric_general");
                }
                else { //Metric
                    //****Range option***
                    ListPreference rangeListPreference = (ListPreference) findPreference(getResources().getString(R.string.pref_range_key));
                    rangeListPreference.setEntries(R.array.range_values_array);
                    correctSummaryRange = Utility.formatPreferredRangeImperialToMetric(Utility.getPreferredRangeMetric(getApplicationContext()));
                    rangeListPreference.setSummary(correctSummaryRange);
                    Log.i(LOG_TAG,"Preferred range/correct metric = " + Utility.getPreferredRangeMetric(getApplicationContext()) + "/" + correctSummaryRange);
                    //***Speed option***
                    ListPreference speedListPreference = (ListPreference) findPreference(getResources().getString(R.string.pref_speed_key));
                    speedListPreference.setEntries(R.array.speed_values_array);
                    correctSummarySpeed = Utility.formatPreferredSpeedImperialToMetric(Utility.getPreferredSpeed(getApplicationContext()));
                    speedListPreference.setSummary(correctSummarySpeed);
                    Log.i(LOG_TAG,"Preferred speed/correct metric: " + Utility.getPreferredSpeed(getApplicationContext()) + "/" + correctSummarySpeed);
                    Log.i(LOG_TAG,"Changed metric range&speed");
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    //***Unit option***
                    ListPreference unitListPreference = (ListPreference) findPreference(getResources().getString(R.string.pref_unit_key));
                    unitListPreference.setSummary(getResources().getString(R.string.unit_array_metric));
                    unitListPreference.setDefaultValue(getResources().getString(R.string.unit_array_metric));
                }

            } else {
                if (firstLoad) {
                    addPreferencesFromResource(R.xml.pref_imperial_general);
                    Log.i(LOG_TAG, "Loaded pref_imperial_general");

                }
                else { //Imperial
                    //***Range option***
                    //TODO: Metric to imperial is working, but not imperial to metric (I tested only range). Write a function to convert the imperial range to metric range
                    ListPreference rangeListPreference = (ListPreference) findPreference(getResources().getString(R.string.pref_range_key));
                    rangeListPreference.setEntries(R.array.range_values_imperial_array);
                    correctSummaryRange = Utility.formatPreferredRangeMetricToImperial(Utility.getPreferredRangeImperial(getApplicationContext()));
                    rangeListPreference.setSummary(correctSummaryRange);
                    Log.i(LOG_TAG,"Preferred range/correctSummary imperial = " + Utility.getPreferredRangeImperial(getApplicationContext()) + "/" + correctSummaryRange);
                    //***Speed option***
                    ListPreference speedListPreference = (ListPreference) findPreference(getResources().getString(R.string.pref_speed_key));
                    speedListPreference.setEntries(R.array.speed_values_imperial_array);
                    correctSummarySpeed = Utility.formatPreferredSpeedMetricToImperial(Utility.getPreferredSpeed(getApplicationContext()));
                    speedListPreference.setSummary(correctSummarySpeed);
                    Log.i(LOG_TAG,"Preferred speed/correct imperial: " + Utility.getPreferredSpeed(getApplicationContext()) + "/" + correctSummarySpeed);
                    Log.i(LOG_TAG,"Changed imperial range&speed");
                    //***Range option***
                    ListPreference unitListPreference = (ListPreference) findPreference(getResources().getString(R.string.pref_unit_key));
                    unitListPreference.setSummary(getResources().getString(R.string.unit_array_imperial));
                    unitListPreference.setDefaultValue(getResources().getString(R.string.unit_array_imperial));
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
            addPreferencesFromResource(R.xml.pref_metric_general);

        }
        }
}