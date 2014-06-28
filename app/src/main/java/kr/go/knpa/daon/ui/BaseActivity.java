package kr.go.knpa.daon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

public abstract class BaseActivity extends ActionBarActivity {

    public static final String PREF_SETUP_COMPLETE = "pref_setup_complete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean setup = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_SETUP_COMPLETE, false);

        if (!setup) {
            Intent intent = new Intent(this, SetupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
