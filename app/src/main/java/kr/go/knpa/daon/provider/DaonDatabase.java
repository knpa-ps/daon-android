package kr.go.knpa.daon.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import kr.go.knpa.daon.provider.DaonContract.OfficersColumns;
import kr.go.knpa.daon.provider.DaonContract.SyncColumns;
import kr.go.knpa.daon.provider.DaonContract.DepartmentsColumns;

public class DaonDatabase extends SQLiteOpenHelper {

    interface Tables {
        String OFFICERS = "officers";
        String DEPARTMENTS = "departments";

        String OFFICERS_JOIN_DEPARTMENTS = "officers left join departments" +
                " on departments.department_id = officers.department_id";
    }

    private static final String DATABASE_NAME = "daon.db";
    private static final int DATABASE_VERSION = 1;

    public DaonDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+Tables.OFFICERS+" (" +
                BaseColumns._ID + " integer primary key autoincrement," +
                SyncColumns.UPDATED_AT + " integer not null," +
                OfficersColumns.OFFICER_ID + " integer not null unique on conflict replace," +
                OfficersColumns.OFFICER_NAME + " text not null default ''," +
                OfficersColumns.OFFICER_RANK + " text not null default ''," +
                OfficersColumns.OFFICER_ROLE + " text not null default ''," +
                OfficersColumns.OFFICER_PHONE + " text not null default ''," +
                OfficersColumns.OFFICER_CELLPHONE + " text not null default ''," +
                OfficersColumns.OFFICER_DEPARTMENT_ID + " integer not null default 0," +
                OfficersColumns.OFFICER_STARRED +" integer not null default 0" +
                ")");

        db.execSQL("create table "+Tables.DEPARTMENTS+" (" +
                BaseColumns._ID + " integer primary key autoincrement," +
                SyncColumns.UPDATED_AT + " integer not null," +
                DepartmentsColumns.DEPARTMENT_ID + " integer not null unique on conflict replace," +
                DepartmentsColumns.DEPARTMENT_NAME + " text not null default ''," +
                DepartmentsColumns.DEPARTMENT_PARENT_ID + " integer," +
                DepartmentsColumns.DEPARTMENT_FULL_NAME + " text not null default ''" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
