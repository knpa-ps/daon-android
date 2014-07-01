package kr.go.knpa.daon.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import kr.go.knpa.daon.BuildConfig;
import kr.go.knpa.daon.R;

public class ApkUpdateActivity extends ActionBarActivity {

    String sDest = "";

    private Uri mDownloadUri;
    private long mQueueId = Long.MIN_VALUE;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long id = intent.getExtras().getLong(DownloadManager.EXTRA_DOWNLOAD_ID);

            if (id == mQueueId) {

                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(dm.getUriForDownloadedFile(id),
                                    dm.getMimeTypeForDownloadedFile(id));
                    startActivity(promptInstall);
                    finish();
                } else {
                    new DialogFragment() {
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                            return new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.update_download_complete_title)
                                    .setMessage(R.string.update_download_complete_message)
                                    .setIcon(R.drawable.ic_launcher)
                                    .create();
                        }
                    }.show(getSupportFragmentManager(), "alert");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apk_update);
        mDownloadUri = getIntent().getData();

        registerReceiver(mReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        startDownload();
    }

    private void startDownload() {
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(mDownloadUri);
        sDest = "file://" + android.os.Environment.getExternalStorageDirectory().toString() +
                "/Download/daon-install.apk";
        request.setDestinationUri(Uri.parse(sDest));
        mQueueId = dm.enqueue(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
