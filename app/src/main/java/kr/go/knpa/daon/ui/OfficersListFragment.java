package kr.go.knpa.daon.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import kr.go.knpa.daon.R;
import kr.go.knpa.daon.provider.DaonContract.Officers;

public class OfficersListFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private OfficersAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new OfficersAdapter();
        setListAdapter(mAdapter);
        setEmptyText(getString(R.string.empty_favorite));
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Officers.CONTENT_URI,
                OfficersQuery.PROJECTION,
                Officers.OFFICER_STARRED+"=1",
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
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

    private class OfficersAdapter extends CursorAdapter {

        private final LayoutInflater mInflater;

        OfficersAdapter() {
            super(getActivity(), null, FLAG_REGISTER_CONTENT_OBSERVER);
            mInflater = getLayoutInflater(null);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return mInflater.inflate(R.layout.list_item_dept_tree, parent, false);
        }

        @Override
        public void bindView(View v, Context context, Cursor c) {

            ((ImageView) v.findViewById(R.id.thumbnail))
                    .setImageResource(R.drawable.ic_action_person);

            String display = c.getString(OfficersQuery.OFFICER_RANK)+" "+
                    c.getString(OfficersQuery.OFFICER_NAME);

            String role = c.getString(OfficersQuery.OFFICER_ROLE);
            if (!TextUtils.isEmpty(role)) {
                display += " ("+role+")";
            }

            ((TextView) v.findViewById(R.id.display))
                    .setText(display);

            v.findViewById(R.id.favorite).setVisibility(View.INVISIBLE);
        }
    }
}
