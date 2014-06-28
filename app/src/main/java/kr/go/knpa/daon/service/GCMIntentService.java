package kr.go.knpa.daon.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import kr.go.knpa.daon.receiver.GCMBroadcastReceiver;

import static kr.go.knpa.daon.util.LogUtils.LOGD;
import static kr.go.knpa.daon.util.LogUtils.makeLogTag;


public class GCMIntentService extends IntentService {
    private static final String TAG = makeLogTag(GCMIntentService.class);
    private static final int GCM_ACTION_SYNC = 1;

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        LOGD(TAG, "got new GCM intent with type="+messageType);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                int action = Integer.parseInt(extras.getString("action"));

                LOGD(TAG, "start gcm action = "+action);

                switch (action) {
                    case GCM_ACTION_SYNC:
                        SyncIntentService.startSync(this);
                        break;
                }

            }
        }

        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

}
