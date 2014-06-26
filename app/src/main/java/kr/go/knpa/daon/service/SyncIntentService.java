package kr.go.knpa.daon.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import java.util.ArrayList;

import kr.go.knpa.daon.io.DepartmentsHandler;
import kr.go.knpa.daon.io.OfficersHandler;
import kr.go.knpa.daon.provider.DaonContract;

import static kr.go.knpa.daon.util.LogUtils.LOGD;
import static kr.go.knpa.daon.util.LogUtils.makeLogTag;

public class SyncIntentService extends IntentService {
    public static final String EXTRA_STATE = "kr.go.knpa.daon.extra.STATE";

    public static final int STATE_ERROR = -100;
    public static final int STATE_START = 100;
    public static final int STATE_SUCCESS = 101;
    public static final String EXTRA_SYNC_TIMESTAMP = "kr.go.knpa.daon.extra.SYNC_TIMESTAMP";
    public static final String ACTION_SYNC = "kr.go.knpa.daon.action.SYNC";

    private static final String TAG = makeLogTag(SyncIntentService.class);

    public SyncIntentService() {
        super("SyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SYNC.equals(action)) {
                performSync(intent.getLongExtra(EXTRA_SYNC_TIMESTAMP, DaonContract.UPDATED_NEVER));
            }
        }
    }

    /**
     * 서버에서 특정 timestamp 이후로 업데이트 된 자료들을 가져온다.
     * @param ts 이 이후의 자료를 가져올 기준이 되는 timestamp. 처음 동기화할 때는 DaonContract.SYNCED_NEVER로
     *           지정하면 모든 자료를 동기화한다.
     */
    private void performSync(long ts) {

        LOGD(TAG, "start to perform sync operation");
        sendSyncStateBroadcast(STATE_START);

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        // departments
        LOGD(TAG, "fetch and parse departments");
        ops.addAll(new DepartmentsHandler(this).fetchAndParse(ts));
        // officers
        LOGD(TAG, "fetch and parse officers");
        ops.addAll(new OfficersHandler(this).fetchAndParse(ts));

        try {
            getContentResolver().applyBatch(DaonContract.CONTENT_AUTHORITY, ops);
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
        Intent intent = new Intent(ACTION_SYNC);
        intent.putExtra(EXTRA_STATE, state);

        sendBroadcast(intent);
    }

}
