package kr.go.knpa.daon.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import kr.go.knpa.daon.R;
import kr.go.knpa.daon.io.api.Api;
import kr.go.knpa.daon.receiver.SyncStateChangeReceiver;
import kr.go.knpa.daon.service.SyncIntentService;
import kr.go.knpa.daon.util.GCMUtils;

import static kr.go.knpa.daon.util.LogUtils.makeLogTag;

public class SetupActivity extends ActionBarActivity {
    private static final String SENDER_ID = "238410544235";
    private static final String TAG = makeLogTag(SetupActivity.class);
    private GoogleCloudMessaging gcm;
    private String regid;

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

        // Check device for Play Services APK.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = GCMUtils.getRegistrationId(this);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(SetupActivity.this);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    new Api().registerGCM(regid);

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    GCMUtils.storeRegistrationId(SetupActivity.this, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return resultCode == ConnectionResult.SUCCESS;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
