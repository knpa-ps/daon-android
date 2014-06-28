package kr.go.knpa.daon.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UIUtils {

    public static void call(Context c, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNumber));
        c.startActivity(intent);
    }

    public static void sendSMS(Context c, String phoneNumber) {

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("address", phoneNumber); // 받는사람 번호
        sendIntent.setType("vnd.android-dir/mms-sms");
        c.startActivity(sendIntent);
    }
}
