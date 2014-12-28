package data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Narcis11 on 28.12.2014.
 */
public class ShopsProvider extends ContentProvider {

    private ShopsDbHelper mShopsHelper;
    @Override
    public boolean onCreate() {
        mShopsHelper = new ShopsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor shopsCursor;
        shopsCursor = mShopsHelper.getReadableDatabase().query(
                ShopsContract.ShopsEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                ShopsContract.ShopsEntry.SORT_ORDER
        );
        //Tells the cursor to register a Content Observer to watch for changes that appear in that uri or any of its descendants
        //descendants = uris that are like 'uri%'
        shopsCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return shopsCursor;
    }

    @Override
    public String getType(Uri uri) {
        //We need all the data from the table, so we return the list item (directory)
        return ShopsContract.ShopsEntry.CONTENT_TYPE;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
