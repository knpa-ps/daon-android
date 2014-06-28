package kr.go.knpa.daon.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class DaonContract {
    public static final long UPDATED_NEVER = -1;

    private DaonContract() {

    }

    interface SyncColumns {
        String UPDATED_AT = "updated";
    }

    interface OfficersColumns {
        String OFFICER_ID = "officer_id";
        String OFFICER_NAME = "name";
        String OFFICER_RANK = "rank";
        String OFFICER_ROLE = "role";
        String OFFICER_DEPARTMENT_ID = "department_id";
        String OFFICER_PHONE = "phone";
        String OFFICER_CELLPHONE = "cellphone";
        String OFFICER_STARRED = "starred";
    }

    interface DepartmentsColumns {
        String DEPARTMENT_ID = "department_id";
        String DEPARTMENT_NAME = "name";
        String DEPARTMENT_PARENT_ID = "parent_id";
        String DEPARTMENT_FULL_NAME = "full_name";
    }

    public static final String CONTENT_AUTHORITY = "kr.go.knpa.daon.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static String PATH_OFFICERS = "officers";
    static String PATH_DEPARTMENTS = "departments";

    public static class Officers implements BaseColumns, SyncColumns, OfficersColumns,
            DepartmentsColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_OFFICERS).build();


    }

    public static class Departments implements BaseColumns, SyncColumns, DepartmentsColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DEPARTMENTS).build();

    }
}
