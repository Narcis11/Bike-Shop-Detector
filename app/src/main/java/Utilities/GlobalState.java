package Utilities;

import android.app.Application;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Narcis11 on 20.12.2014.
 */
public class GlobalState {
    public static String USER_LAT = "";
    public static String USER_LNG = "";
    public static String ALL_SHOPS_INFO = "";
    public static String FETCH_STATUS = "";
    public static String USERS_CITY = "";
    public static ArrayAdapter<String> GLOBAL_ADAPTER;
    public static boolean SYNC_SHOPS;
    public static String INPUT = "";
    public static ArrayList<String> RESULT_LIST_GLOBAL = null;
}
