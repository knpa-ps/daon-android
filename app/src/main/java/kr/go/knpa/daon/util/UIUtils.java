package kr.go.knpa.daon.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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

    public static long parse(String str, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(str).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }

    public static long parse(String str) {
        return parse(str, "yyyy-MM-dd HH:mm:ss");
    }
}
