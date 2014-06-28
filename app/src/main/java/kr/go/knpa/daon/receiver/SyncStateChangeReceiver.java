package kr.go.knpa.daon.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import kr.go.knpa.daon.service.SyncIntentService;

public abstract class SyncStateChangeReceiver extends BroadcastReceiver {
    public SyncStateChangeReceiver() {
    }

    public IntentFilter getIntentFilter() {
        return new IntentFilter(SyncIntentService.ACTION_SYNC_STATE_CHANGED);
    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        switch (intent.getIntExtra(SyncIntentService.EXTRA_STATE, SyncIntentService.STATE_ERROR)) {
            case SyncIntentService.STATE_SUCCESS:
                onSuccess();
                break;
            case SyncIntentService.STATE_ERROR:
                onError();
                break;
            case SyncIntentService.STATE_START:
                onStart();
                break;
        }
    }

    public void onStart() {

    }

    public void onError() {

    }

    public void onSuccess() {

    }
}
