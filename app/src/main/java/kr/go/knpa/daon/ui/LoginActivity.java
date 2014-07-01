package kr.go.knpa.daon.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import kr.go.knpa.daon.R;

public class LoginActivity extends ActionBarActivity {
    private EditText mPassword;
    private EditText mPasswordConfirm;
    public static final String PREF_PASSWORD_SET = "pref_password_set";
    public static final String PREF_PASSWORD = "pref_password";

    private boolean mPasswordSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mPassword = (EditText) findViewById(R.id.password);
        mPasswordConfirm = (EditText) findViewById(R.id.password_confirmation);

        mPasswordSet = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_PASSWORD_SET, false);

        if (mPasswordSet) {
            mPasswordConfirm.setVisibility(View.GONE);
            mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        onSubmit();
                        return true;
                    }
                    return false;
                }
            });
        } else {
            new DialogFragment() {
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    return new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.set_password_title)
                            .setMessage(R.string.set_password_message)
                            .setIcon(R.drawable.ic_launcher)
                            .setPositiveButton(android.R.string.ok, null)
                            .create();
                }
            }.show(getSupportFragmentManager(), "alert");
            mPasswordConfirm.setVisibility(View.VISIBLE);
            mPasswordConfirm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        onSubmit();
                        return true;
                    }
                    return false;
                }
            });
        }

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmit();
            }
        });

    }

    private void onSubmit() {
        if (mPasswordSet) {
            if (mPassword.getText().length() != 4) {
                mPassword.setError(getString(R.string.error_password_length));
                return;
            }

            String password = PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(PREF_PASSWORD, "");

            if (mPassword.getText().toString().equals(password)) {
                startMainActivity();
            } else {
                mPassword.setError(getString(R.string.error_wrong_password));
            }
        } else {
            String password = mPassword.getText().toString();
            String passwordConfirm = mPasswordConfirm.getText().toString();

            if (password.length() != 4) {
                mPassword.setError(getString(R.string.error_password_length));
                return;
            } else if (passwordConfirm.length() != 4) {
                mPasswordConfirm.setError(getString(R.string.error_password_length));
                return;
            } else if (!password.equals(passwordConfirm)) {
                mPasswordConfirm.setError(getString(R.string.error_password_not_match));
                return;
            }

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(PREF_PASSWORD, password)
                    .putBoolean(PREF_PASSWORD_SET, true)
                    .commit();

            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
