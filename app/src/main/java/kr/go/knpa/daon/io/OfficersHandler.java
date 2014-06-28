package kr.go.knpa.daon.io;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.database.Cursor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kr.go.knpa.daon.io.api.Api;
import kr.go.knpa.daon.io.model.OfficerReponse;
import kr.go.knpa.daon.provider.DaonContract.Officers;
import kr.go.knpa.daon.util.UIUtils;
import kr.go.knpa.daon.util.Lists;

public class OfficersHandler {

    private Context mContext;

    public OfficersHandler(Context context) {
        mContext = context;
    }

    public ArrayList<ContentProviderOperation> fetchAndParse() throws IOException {

        List<OfficerReponse> officers = new Api().officers();
        ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        // local db 에서 starred된 자료들을 set에 담아 놓고 참조하여,
        // 서버로부터 받아온 자료들을 insert할 때 starred 상태를 보존한다.
        Cursor c = mContext.getContentResolver().query(Officers.CONTENT_URI,
                new String[] { Officers.OFFICER_ID }, Officers.OFFICER_STARRED+"=1", null, null);
        Set<Integer> starredIds = new HashSet<Integer>();
        while (c.moveToNext()) {
            starredIds.add(c.getInt(0));
        }
        c.close();

        // local db 자료 삭제
        mContext.getContentResolver().delete(Officers.CONTENT_URI, null, null);

        for (OfficerReponse o : officers) {
            ContentProviderOperation.Builder builder =
                    ContentProviderOperation.newInsert(Officers.CONTENT_URI);

            boolean starred = starredIds.contains(o.getId());

            builder.withValue(Officers.OFFICER_ID, o.getId())
                    .withValue(Officers.OFFICER_DEPARTMENT_ID, o.getDepartmentId())
                    .withValue(Officers.OFFICER_NAME, o.getName())
                    .withValue(Officers.OFFICER_RANK, o.getRank())
                    .withValue(Officers.OFFICER_ROLE, o.getRole())
                    .withValue(Officers.OFFICER_PHONE, o.getPhone())
                    .withValue(Officers.OFFICER_CELLPHONE, o.getCellphone())
                    .withValue(Officers.UPDATED_AT, UIUtils.parse(o.getUpdatedAt()))
                    .withValue(Officers.OFFICER_STARRED, starred);

            batch.add(builder.build());
        }

        return batch;
    }
}
