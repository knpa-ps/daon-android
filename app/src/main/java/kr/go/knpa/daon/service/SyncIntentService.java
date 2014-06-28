package kr.go.knpa.daon.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import java.io.IOException;
import java.util.ArrayList;

import kr.go.knpa.daon.io.DepartmentsHandler;
import kr.go.knpa.daon.io.OfficersHandler;
import kr.go.knpa.daon.provider.DaonContract;
import kr.go.knpa.daon.util.NetUtils;

import static kr.go.knpa.daon.util.LogUtils.LOGD;
import static kr.go.knpa.daon.util.LogUtils.LOGE;
import static kr.go.knpa.daon.util.LogUtils.makeLogTag;

public class SyncIntentService extends IntentService {
    public static final String EXTRA_STATE = "kr.go.knpa.daon.extra.STATE";

    public static final int STATE_ERROR = -100;
    public static final int STATE_START = 100;
    public static final int STATE_SUCCESS = 101;
    public static final String ACTION_START_SYNC = "kr.go.knpa.daon.action.START_SYNC";
    public static final String ACTION_SYNC_STATE_CHANGED =
            "kr.go.knpa.daon.actino.SYNC_STATE_CHANGED";
    private static final String TAG = makeLogTag(SyncIntentService.class);

    public SyncIntentService() {
        super("SyncIntentService");
    }

    public static int getSyncState(Intent intent) {
        return intent.getIntExtra(EXTRA_STATE, STATE_ERROR);
    }

    public static void startSync(Context c) {
        Intent intent = new Intent(c, SyncIntentService.class);
        intent.setAction(ACTION_START_SYNC);
        c.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START_SYNC.equals(action)) {
                performSync();
            }
        }
    }

    private void performSync() {

        if (!NetUtils.isConnected(this)) {
            LOGD(TAG, "no connection to network. sync aborted");
            return;
        }

        LOGD(TAG, "start to perform sync operation");
        sendSyncStateBroadcast(STATE_START);

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        try {
            // departments
            LOGD(TAG, "fetch and parse departments");
            ops.addAll(new DepartmentsHandler(this).fetchAndParse());
            // officers
            LOGD(TAG, "fetch and parse officers");
            ops.addAll(new OfficersHandler(this).fetchAndParse());
        } catch (IOException e) {
            LOGE(TAG, "io exception occurred", e);
            sendSyncStateBroadcast(STATE_ERROR);
            return;
        }


        try {
            LOGD(TAG, "apply batch operations");
            getContentResolver().applyBatch(DaonContract.CONTENT_AUTHORITY, ops);

            LOGD(TAG, "sync success");
            sendSyncStateBroadcast(STATE_SUCCESS);
        } catch (RemoteException e) {
            sendSyncStateBroadcast(STATE_ERROR);
            throw new RuntimeException("apply batch failed", e);
        } catch (OperationApplicationException e) {
            sendSyncStateBroadcast(STATE_ERROR);
            throw new RuntimeException("apply batch failed", e);
        }
    }

    private void sendSyncStateBroadcast(int state) {
        Intent intent = new Intent(ACTION_SYNC_STATE_CHANGED);
        intent.putExtra(EXTRA_STATE, state);

        sendBroadcast(intent);
    }

}
