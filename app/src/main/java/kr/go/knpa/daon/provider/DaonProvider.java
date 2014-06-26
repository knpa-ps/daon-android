package kr.go.knpa.daon.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import kr.go.knpa.daon.provider.DaonDatabase.Tables;
import kr.go.knpa.daon.util.SelectionBuilder;

public class DaonProvider extends ContentProvider {
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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
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

        return newUri;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DaonDatabase(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

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
                        .where(DaonContract.Officers.OFFICER_DEPARTMENT_ID+"=?", deptId);
                break;
            }
            case OFFICERS_ID: {
                String officerId = uri.getLastPathSegment();
                builder.table(Tables.OFFICERS_JOIN_DEPARTMENTS)
                        .where(DaonContract.Officers.OFFICER_ID+"=?", officerId);
                break;
            }
            default:
                throw new UnsupportedOperationException("unknown uri for match="+match);
        }

        Cursor c = builder.query(db, projection, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
