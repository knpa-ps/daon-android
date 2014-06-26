package kr.go.knpa.daon.io;

import android.content.ContentProviderOperation;
import android.content.Context;

import java.util.ArrayList;

public class OfficersHandler {

    private Context mContext;

    public OfficersHandler(Context context) {
        mContext = context;
    }

    public ArrayList<ContentProviderOperation> fetchAndParse(long ts) {
        //TODO
        return null;
    }
}
