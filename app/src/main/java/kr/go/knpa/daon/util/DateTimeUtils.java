package kr.go.knpa.daon.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateTimeUtils {
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
