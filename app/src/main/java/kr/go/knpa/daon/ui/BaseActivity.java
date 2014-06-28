package kr.go.knpa.daon.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import kr.go.knpa.daon.util.GCMUtils;

public abstract class BaseActivity extends ActionBarActivity {

    public static final String PREF_SETUP_COMPLETE = "pref_setup_complete";
    private boolean setupCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean setup = isSetupCompleted();

        if (!setup) {
            Intent intent = new Intent(this, SetupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public boolean isSetupCompleted() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        return pref.getBoolean(PREF_SETUP_COMPLETE, false) &&
                !TextUtils.isEmpty(GCMUtils.getRegistrationId(this));
    }
}
