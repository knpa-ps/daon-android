package kr.go.knpa.daon.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.Locale;

import kr.go.knpa.daon.R;
import kr.go.knpa.daon.io.api.Api;
import kr.go.knpa.daon.io.model.VersionResponse;
import kr.go.knpa.daon.util.GCMUtils;

import static kr.go.knpa.daon.util.LogUtils.LOGE;
import static kr.go.knpa.daon.util.LogUtils.makeLogTag;

public class MainActivity extends BaseActivity implements ActionBar.TabListener {

    private static final String TAG = makeLogTag(MainActivity.class);
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new DeptTreeListFragment();
                case 1:
                    return new OfficersListFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.dept_tree).toUpperCase(l);
                case 1:
                    return getString(R.string.favorite).toUpperCase(l);
            }
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        new DialogFragment() {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_launcher)
                        .setTitle(R.string.confirm_exit_title)
                        .setMessage(R.string.confirm_exit_message)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                return b.create();
            }
        }.show(getSupportFragmentManager(), "confirm");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_update_app) {
            checkUpdate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkUpdate() {
        new AsyncTask<Void, Void, Void>() {
            DialogFragment progress;
            boolean needUpdate;
            public VersionResponse versionResponse;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress = new DialogFragment() {

                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new ProgressDialog.Builder(getActivity())
                                .setTitle(R.string.update_app_title)
                                .setIcon(R.drawable.ic_launcher)
                                .setMessage(R.string.update_app_message)
                                .create();
                    }
                };

                progress.show(getSupportFragmentManager(), "progress");
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    versionResponse = new Api().checkVersion();

                    if (versionResponse == null) {
                        needUpdate = false;
                        return null;
                    }

                    int currentVersion = GCMUtils.getAppVersion(MainActivity.this);

                    needUpdate = currentVersion < versionResponse.getCode();

                } catch (IOException e) {
                    LOGE(TAG, "io exception during version check", e);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (isDestroyed()) {
                    return;
                }

                progress.dismiss();

                if (needUpdate) {
                    confirmDownload();
                } else {
                    Toast.makeText(MainActivity.this, R.string.no_update, Toast.LENGTH_SHORT).show();
                }
            }

            private void confirmDownload() {
                new DialogFragment() {
                    @Override
                    public Dialog onCreateDialog(Bundle savedInstanceState) {
                        return new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.update_app_title)
                                .setMessage(R.string.update_available)
                                .setIcon(R.drawable.ic_launcher)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startUpdateActivity();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, null)
                                .create();
                    }
                }.show(getSupportFragmentManager(), "confirm");

            }

            private void startUpdateActivity() {

                Intent intent = new Intent(MainActivity.this, ApkUpdateActivity.class);
                intent.setData(Uri.parse(versionResponse.getDownloadUrl()));
                startActivity(intent);
            }
        }.execute();
    }
}
