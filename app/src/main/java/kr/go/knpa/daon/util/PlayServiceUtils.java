package kr.go.knpa.daon.util;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class PlayServiceUtils {

    public static boolean checkPlayServiceAvailable(Context c) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(c);
        return resultCode == ConnectionResult.SUCCESS;
    }
}
