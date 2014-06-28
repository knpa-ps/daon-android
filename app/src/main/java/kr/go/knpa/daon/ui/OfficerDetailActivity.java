package kr.go.knpa.daon.ui;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import kr.go.knpa.daon.R;
import kr.go.knpa.daon.provider.DaonContract.Officers;

public class OfficerDetailActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String EXTRA_OFFICER_ID = "kr.go.knpa.daon.extra.OFFICER_ID";
    private int mOfficerId;
    private boolean mStarred;
    private TextView mRankNameView;
    private TextView mDeptNameView;
    private TextView mRoleView;
    private TextView mPhoneView;
    private TextView mCellphoneView;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer_detail);
        mOfficerId = getIntent().getIntExtra(EXTRA_OFFICER_ID, -1);

        mRankNameView = (TextView) findViewById(R.id.rank_name);
        mDeptNameView = (TextView) findViewById(R.id.department);

        mRoleView = (TextView) findViewById(R.id.role);

        mPhoneView = (TextView) findViewById(R.id.phone);
        findViewById(R.id.call_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.call(OfficerDetailActivity.this, mPhoneView.getText().toString());
            }
        });

        mCellphoneView = (TextView) findViewById(R.id.cellphone);
        findViewById(R.id.call_cellphone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.call(OfficerDetailActivity.this, mCellphoneView.getText().toString());
            }
        });

        findViewById(R.id.sms_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.sendSMS(OfficerDetailActivity.this, mCellphoneView.getText().toString());
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.officer_detail, menu);
        mMenu = menu;
        if (mStarred) {
            mMenu.findItem(R.id.action_star).setIcon(R.drawable.ic_action_important_holo_dark);
        } else {
            mMenu.findItem(R.id.action_star).setIcon(R.drawable.ic_action_not_important_holo_dark);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_star) {
            toggleStarred();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleStarred() {
        ContentValues v = new ContentValues();

        v.put(Officers.OFFICER_STARRED, mStarred?0:1);

        getContentResolver().update(Officers.CONTENT_URI,
                v, Officers.OFFICER_ID+"=?", new String[] {String.valueOf(mOfficerId)});
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Officers.CONTENT_URI, OfficersQuery.PROJECTION,
                Officers.OFFICER_ID+"=?", new String[] { String.valueOf(mOfficerId) },
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        String rankName = data.getString(OfficersQuery.OFFICER_RANK)+" "+
                data.getString(OfficersQuery.OFFICER_NAME);

        mRankNameView.setText(rankName);

        mDeptNameView.setText(data.getString(OfficersQuery.DEPARTMENT_FULL_NAME));

        mRoleView.setText(data.getString(OfficersQuery.OFFICER_ROLE));

        mPhoneView.setText(data.getString(OfficersQuery.OFFICER_PHONE));
        mCellphoneView.setText(data.getString(OfficersQuery.OFFICER_CELLPHONE));

        mStarred = data.getInt(OfficersQuery.OFFICER_STARRED) > 0;
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private interface OfficersQuery {
        String[] PROJECTION = {
                Officers._ID,
                Officers.OFFICER_ID,
                Officers.DEPARTMENT_NAME,
                Officers.OFFICER_RANK,
                Officers.OFFICER_ROLE,
                Officers.OFFICER_PHONE,
                Officers.OFFICER_CELLPHONE,
                Officers.DEPARTMENT_ID,
                Officers.DEPARTMENT_FULL_NAME,
                Officers.OFFICER_STARRED,
                Officers.OFFICER_NAME
        };


        int _ID = 0;
        int OFFICER_ID = 1;
        int DEPARTMENT_NAME = 2;
        int OFFICER_RANK = 3;
        int OFFICER_ROLE = 4;
        int OFFICER_PHONE = 5;
        int OFFICER_CELLPHONE = 6;
        int DEPARTMENT_ID = 7;
        int DEPARTMENT_FULL_NAME = 8;
        int OFFICER_STARRED = 9;
        int OFFICER_NAME = 10;
    }
}
