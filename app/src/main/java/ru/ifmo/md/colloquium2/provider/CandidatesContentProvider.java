package ru.ifmo.md.colloquium2.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class CandidatesContentProvider extends ContentProvider {
    public static final String LOG_TAG = "ContentProvider";
    public static final String AUTHORITY = "ru.ifmo.md.colloquium2.provider.CandidatesContentProvider";
    public static final String CANDIDATES_PATH = DatabaseHelper.TABLE_CANDIDATES;
    public static final String STATE_PATH = DatabaseHelper.TABLE_STATE;

    public static final Uri CANDIDATES_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + CANDIDATES_PATH);
    public static final Uri STATE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + STATE_PATH);

    public static final int URI_CANDIDATES = 1;
    public static final int URI_STATE = 2;

    static final String CANDIDATE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + CANDIDATES_PATH;
    static final String STATE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + STATE_PATH;
    //static final String NEWS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
    //+ AUTHORITY + "." + NEWS_PATH;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, CANDIDATES_PATH, URI_CANDIDATES);
        uriMatcher.addURI(AUTHORITY, STATE_PATH, URI_STATE);
    }

    private DatabaseHelper dbHelper;

    public CandidatesContentProvider() {
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int cnt = 0;
        if (uriMatcher.match(uri) == URI_CANDIDATES)
            cnt = dbHelper.getWritableDatabase().delete(DatabaseHelper.TABLE_CANDIDATES, selection, selectionArgs);
        else
            throw new IllegalArgumentException("delete exception");
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public String getType(Uri uri) {
        //Log.i("RSSContentProvider", "getType");
        if (uriMatcher.match(uri) == URI_CANDIDATES)
            return CANDIDATE_CONTENT_TYPE;
        if (uriMatcher.match(uri) == URI_STATE)
            return STATE_CONTENT_TYPE;
        throw new RuntimeException("incorrect getType");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //Log.d(LOG_TAG, "insert " + uri.toString());
        int m = uriMatcher.match(uri);
        Uri resultUri;
        if (m == URI_CANDIDATES) {
            long rowID = dbHelper.getWritableDatabase().insert(DatabaseHelper.TABLE_CANDIDATES, null, values);
            resultUri = ContentUris.withAppendedId(CANDIDATES_CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(resultUri, null);
        } else
            throw new IllegalArgumentException("Wrong URI: " + uri.toString());
        return resultUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        //Log.i(LOG_TAG, "query, " + uri.toString());
        int m = uriMatcher.match(uri);
        Cursor cursor;
        if (m == URI_CANDIDATES) {
            cursor = dbHelper.getReadableDatabase().query(DatabaseHelper.TABLE_CANDIDATES, projection, selection, selectionArgs, null, null, sortOrder);
            //cursor.setNotificationUri(getContext().getContentResolver(), NEWS_CONTENT_URI);
        } else if (m == URI_STATE) {
            cursor = dbHelper.getReadableDatabase().query(DatabaseHelper.TABLE_STATE, projection, selection, selectionArgs, null, null, sortOrder);
        } else
            throw new IllegalArgumentException("Wrong URI: " + uri);
        cursor.setNotificationUri(getContext().getContentResolver(), CANDIDATES_CONTENT_URI);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        int cnt = 0;
        if (uriMatcher.match(uri) == URI_CANDIDATES)
            cnt = dbHelper.getWritableDatabase().update(DatabaseHelper.TABLE_CANDIDATES, values, selection, selectionArgs);
        else if (uriMatcher.match(uri) == URI_STATE)
            cnt = dbHelper.getWritableDatabase().update(DatabaseHelper.TABLE_STATE, values, selection, selectionArgs);
        else
            throw new IllegalArgumentException("update");
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }
}
