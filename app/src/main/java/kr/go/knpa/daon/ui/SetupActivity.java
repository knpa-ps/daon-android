package kr.go.knpa.daon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import kr.go.knpa.daon.R;
import kr.go.knpa.daon.receiver.SyncStateChangeReceiver;
import kr.go.knpa.daon.service.SyncIntentService;

public class SetupActivity extends ActionBarActivity {

    private SyncStateChangeReceiver mReceiver = new SyncStateChangeReceiver() {
        @Override
        public void onError() {
            if (isDestroyed()) {
                return;
            }

            Toast.makeText(SetupActivity.this, R.string.error_sync, Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onSuccess() {
            if (isDestroyed()) {
                return;
            }
            PreferenceManager.getDefaultSharedPreferences(SetupActivity.this)
                    .edit()
                    .putBoolean(BaseActivity.PREF_SETUP_COMPLETE, true)
                    .commit();

            Toast.makeText(SetupActivity.this, R.string.sync_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SetupActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        registerReceiver(mReceiver, mReceiver.getIntentFilter());
        SyncIntentService.startSync(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
