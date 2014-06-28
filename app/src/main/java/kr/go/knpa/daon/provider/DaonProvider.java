package kr.go.knpa.daon.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Arrays;

import kr.go.knpa.daon.provider.DaonDatabase.Tables;
import kr.go.knpa.daon.util.SelectionBuilder;

import static kr.go.knpa.daon.util.LogUtils.LOGV;
import static kr.go.knpa.daon.util.LogUtils.makeLogTag;

public class DaonProvider extends ContentProvider {
    private static final String TAG = makeLogTag(DaonProvider.class);
    private DaonDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int DEPARTMENTS = 1;
    private static final int DEPARTMENTS_ID_CHILDREN = 2;
    private static final int DEPARTMENTS_ID_OFFICERS = 4;
    private static final int OFFICERS_ID = 3;
    private static final int OFFICERS = 5;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DaonContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "departments", DEPARTMENTS);
        matcher.addURI(authority, "departments/#/children", DEPARTMENTS_ID_CHILDREN);
        matcher.addURI(authority, "departments/#/officers", DEPARTMENTS_ID_OFFICERS);
        matcher.addURI(authority, "officers/#", OFFICERS_ID);
        matcher.addURI(authority, "officers", OFFICERS);

        return matcher;
    }

    public DaonProvider() {
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DaonDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        LOGV(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();

        int match = sUriMatcher.match(uri);
        switch (match) {
            case DEPARTMENTS_ID_CHILDREN: {
                String deptId = uri.getPathSegments().get(1);
                builder.table(Tables.DEPARTMENTS)
                        .where(DaonContract.Departments.DEPARTMENT_PARENT_ID+"=?", deptId);
                break;
            }
            case DEPARTMENTS_ID_OFFICERS: {
                String deptId = uri.getPathSegments().get(1);
                builder.table(Tables.OFFICERS_JOIN_DEPARTMENTS)
                        .mapToTable(BaseColumns._ID, Tables.OFFICERS)
                        .where(DaonContract.Officers.OFFICER_DEPARTMENT_ID+"=?", deptId);
                break;
            }
            case OFFICERS_ID: {
                String officerId = uri.getLastPathSegment();
                builder.table(Tables.OFFICERS_JOIN_DEPARTMENTS)
                        .mapToTable(BaseColumns._ID, Tables.OFFICERS)
                        .where(DaonContract.Officers.OFFICER_ID+"=?", officerId);
                break;
            }
            case OFFICERS: {
                builder.table(Tables.OFFICERS_JOIN_DEPARTMENTS)
                        .mapToTable(BaseColumns._ID, Tables.OFFICERS);
                break;
            }
            case DEPARTMENTS: {
                builder.table(Tables.DEPARTMENTS);
                break;
            }
            default:
                throw new UnsupportedOperationException("unknown uri for match="+match);
        }

        Cursor c = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        LOGV(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");

        int match = sUriMatcher.match(uri);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri newUri;
        switch (match) {
            case OFFICERS: {
                db.insertOrThrow(Tables.OFFICERS, null, values);
                String officerId = values.getAsString(DaonContract.Officers.OFFICER_ID);
                newUri = DaonContract.Officers.CONTENT_URI
                        .buildUpon()
                        .appendPath(officerId)
                        .build();
                break;
            }
            case DEPARTMENTS: {
                db.insertOrThrow(Tables.DEPARTMENTS, null, values);
                String deptId = values.getAsString(DaonContract.Departments.DEPARTMENT_ID);
                newUri = DaonContract.Departments.CONTENT_URI
                        .buildUpon()
                        .appendPath(deptId)
                        .build();
                break;
            }
            default:
                throw new UnsupportedOperationException("unknown uri for match="+match);
        }

        notifyChange(uri);

        return newUri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        LOGV(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");

        int match = sUriMatcher.match(uri);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case OFFICERS: {
                builder.table(Tables.OFFICERS);
                break;
            }
            default:
                throw new UnsupportedOperationException("unknown uri for match="+match);
        }
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        notifyChange(uri);
        return retVal;
    }

    private void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null, false);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        LOGV(TAG, "delete(uri=" + uri + ")");

        int match = sUriMatcher.match(uri);
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        SelectionBuilder builder = new SelectionBuilder();

        switch (match) {
            case OFFICERS: {
                builder.table(Tables.OFFICERS);
                break;
            }
            case DEPARTMENTS: {
                builder.table(Tables.DEPARTMENTS);
                break;
            }
            default:
                throw new UnsupportedOperationException("unknown uri for match="+match);
        }

        notifyChange(uri);

        return builder.where(selection, selectionArgs).delete(db);
    }
}
