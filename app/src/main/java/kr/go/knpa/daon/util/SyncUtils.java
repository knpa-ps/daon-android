package kr.go.knpa.daon.util;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import kr.go.knpa.daon.service.SyncIntentService;

public class SyncUtils {
    private static final String PREF_SYNC_SETUP_COMPLETED = "pref_sync_setup_completed";
    private static final String PREF_LAST_UPDATED_AT = "pref_last_updated_at";

    public static long getLastUpdatedAt(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .getLong(PREF_LAST_UPDATED_AT, 0);
    }

    public static boolean setLastUpdatedAt(Context c, long ts) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .edit()
                .putLong(PREF_LAST_UPDATED_AT, ts)
                .commit();
    }

    public static boolean isSetupComplete(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .getBoolean(PREF_SYNC_SETUP_COMPLETED, false);
    }

    public static boolean setSyncSetupCompleted(Context c, boolean completed) {
        return PreferenceManager.getDefaultSharedPreferences(c)
                .edit()
                .putBoolean(PREF_SYNC_SETUP_COMPLETED, completed)
                .commit();
    }

    public static void startSync(Context c, long ts) {
        Intent intent = new Intent(c, SyncIntentService.class);
        intent.putExtra(SyncIntentService.EXTRA_SYNC_TIMESTAMP, ts);
        c.startService(intent);
    }
}
