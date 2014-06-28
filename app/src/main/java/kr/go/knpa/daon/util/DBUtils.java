package kr.go.knpa.daon.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class DBUtils {
    public static void dump(Context c) {
        File dbFile = new File("/data/data/kr.go.knpa.daon/databases/daon.db");
        File dump = new File(Environment.getExternalStorageDirectory(), "daon.db");

        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(dbFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream outStream = new FileOutputStream(dump);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
