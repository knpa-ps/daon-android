package kr.go.knpa.daon.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils {

    public static boolean isConnected(Context c) {
        ConnectivityManager cm = (ConnectivityManager)
                c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo net = cm.getActiveNetworkInfo();

        return net != null && net.isConnected();
    }
}
