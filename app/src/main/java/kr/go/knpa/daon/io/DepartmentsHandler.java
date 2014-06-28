package kr.go.knpa.daon.io;

import android.content.ContentProviderOperation;
import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.go.knpa.daon.io.api.Api;
import kr.go.knpa.daon.io.model.DepartmentResponse;
import kr.go.knpa.daon.provider.DaonContract.Departments;
import kr.go.knpa.daon.util.UIUtils;
import kr.go.knpa.daon.util.Lists;

public class DepartmentsHandler {

    private Context mContext;

    public DepartmentsHandler(Context context) {
        mContext = context;
    }

    public ArrayList<ContentProviderOperation> fetchAndParse() throws IOException {

        List<DepartmentResponse> depts = new Api().departments();

        ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        // local db 자료 삭제
        mContext.getContentResolver().delete(Departments.CONTENT_URI, null, null);

        for (DepartmentResponse o : depts) {
            ContentProviderOperation.Builder builder =
                    ContentProviderOperation.newInsert(Departments.CONTENT_URI);

            builder.withValue(Departments.DEPARTMENT_ID, o.getId())
                    .withValue(Departments.DEPARTMENT_FULL_NAME, o.getFullName())
                    .withValue(Departments.DEPARTMENT_NAME, o.getName())
                    .withValue(Departments.DEPARTMENT_PARENT_ID, o.getParentId())
                    .withValue(Departments.UPDATED_AT, UIUtils.parse(o.getUpdatedAt()));

            batch.add(builder.build());
        }

        return batch;

    }
}
