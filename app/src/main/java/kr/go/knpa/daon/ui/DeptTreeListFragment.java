package kr.go.knpa.daon.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.go.knpa.daon.R;
import kr.go.knpa.daon.provider.DaonContract.Departments;
import kr.go.knpa.daon.provider.DaonContract.Officers;
import kr.go.knpa.daon.util.Lists;

public class DeptTreeListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<DeptTreeListFragment.Item>> {

    private Integer mParentDeptId = null;
    private Integer mCurrentDeptId = 1;
    private ItemAdapter mAdapter;
    private RelativeLayout mHeaderView;

    ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (!isAdded()) {
                return;
            }

            getLoaderManager().restartLoader(0, null, DeptTreeListFragment.this);
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().getContentResolver()
                .registerContentObserver(Officers.CONTENT_URI, true, mObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().getContentResolver()
                .unregisterContentObserver(mObserver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ItemAdapter();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayout headerWrapper = new LinearLayout(getActivity());
        mHeaderView = (RelativeLayout) getLayoutInflater(null)
                .inflate(R.layout.list_item_dept_tree, headerWrapper, false);

        ((ImageView) mHeaderView.findViewById(R.id.thumbnail))
                .setImageResource(R.drawable.ic_action_previous_item);

        ((TextView) mHeaderView.findViewById(R.id.display))
                .setText(R.string.parent_department);

        mHeaderView.findViewById(R.id.favorite).setVisibility(View.INVISIBLE);

        headerWrapper.addView(mHeaderView);
        mHeaderView.setVisibility(View.GONE);
        getListView().addHeaderView(headerWrapper);

        setListAdapter(mAdapter);
        setListShown(false);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            if (mParentDeptId == null) {
                return;
            }

            navigateUp();

            return;
        }

        Item item = mAdapter.getItem(position-1);

        if (item.type == Item.TYPE_DEPT) {
            navigateDown(item.id);
        } else {
            Intent intent = new Intent(getActivity(), OfficerDetailActivity.class);
            intent.putExtra(OfficerDetailActivity.EXTRA_OFFICER_ID, item.id);
            startActivity(intent);
        }
    }

    private void navigateUp() {
        Cursor c = getActivity().getContentResolver().query(Departments.CONTENT_URI,
                new String[] { Departments.DEPARTMENT_PARENT_ID },
                Departments.DEPARTMENT_ID+"=?", new String[] {mParentDeptId.toString()}, null);

        c.moveToFirst();
        mCurrentDeptId = mParentDeptId;
        mParentDeptId = c.isNull(0) ? null : c.getInt(0);
        c.close();

        getLoaderManager().restartLoader(0, null, this);
    }

    private void navigateDown(int deptId) {
        mParentDeptId = mCurrentDeptId;
        mCurrentDeptId = deptId;

        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<List<Item>> onCreateLoader(int id, Bundle args) {
        return new ItemLoader(getActivity(), mCurrentDeptId);
    }

    @Override
    public void onLoadFinished(Loader<List<Item>> loader, List<Item> data) {
        mAdapter.setData(data);
        if (mParentDeptId == null) {
            mHeaderView.setVisibility(View.GONE);
        } else {
            mHeaderView.setVisibility(View.VISIBLE);
        }
        setListShown(true);
    }

    @Override
    public void onLoaderReset(Loader<List<Item>> loader) {

    }

    public static class ItemLoader extends AsyncTaskLoader<List<Item>> {
        private final Integer mCurrentDeptId;
        ContentResolver r;
        List<Item> mItems;

        public ItemLoader(Context c, Integer deptId) {
            super(c);
            mCurrentDeptId = deptId;
            r = c.getContentResolver();
        }

        @Override
        public List<Item> loadInBackground() {
            if (mItems == null) {
                mItems = Lists.newArrayList();
            }

            mItems.addAll(getDepartments());
            mItems.addAll(getOfficers());
            return mItems;
        }

        private List<Item> getDepartments() {
            List<Item> depts = Lists.newArrayList();
            Cursor c = r.query(Departments.CONTENT_URI,
                    new String[] {
                            Departments.DEPARTMENT_ID,
                            Departments.DEPARTMENT_FULL_NAME
                    }, Departments.DEPARTMENT_PARENT_ID+"=?",
                    new String[] { mCurrentDeptId.toString() },
                    Departments.DEPARTMENT_ID+" ASC");

            while (c.moveToNext()) {
                Item item = new Item();
                item.type = Item.TYPE_DEPT;
                item.id = c.getInt(0);
                item.display = c.getString(1);
                depts.add(item);
            }

            return depts;
        }

        private List<Item> getOfficers() {
            List<Item> data = Lists.newArrayList();
            Cursor c = r.query(Officers.CONTENT_URI,
                    new String[] {
                            Officers.OFFICER_ID,
                            Officers.OFFICER_NAME,
                            Officers.OFFICER_RANK,
                            Officers.OFFICER_ROLE,
                            Officers.OFFICER_PHONE,
                            Officers.OFFICER_CELLPHONE,
                            Officers.OFFICER_STARRED
                    }, Officers.OFFICER_DEPARTMENT_ID+"=?",
                    new String[] { mCurrentDeptId.toString() },
                    null);

            while (c.moveToNext()) {
                Item item = new Item();
                item.type = Item.TYPE_OFFICER;
                item.id = c.getInt(0);
                item.display = c.getString(2)+" "+c.getString(1);
                if (!TextUtils.isEmpty(c.getString(3))) {
                    item.display += " ("+c.getString(3)+")";
                }

                Map<String, Object> extras = new HashMap<String, Object>();
                extras.put("phone", c.getString(4));
                extras.put("cellphone", c.getString(5));
                extras.put("starred", c.getInt(6) > 0);
                item.data = extras;

                data.add(item);
            }

            return data;
        }

        @Override
        public void onStartLoading() {
            super.onStartLoading();

            if (mItems != null) {
                // If we currently have a result available, deliver it
                // immediately.
                deliverResult(mItems);
            }

            if (takeContentChanged() || mItems == null) {
                // If the data has changed since the last time it was loaded
                // or is not currently available, start a load.
                forceLoad();
            }
        }

        @Override
        protected void onStopLoading() {
            cancelLoad();
        }

        /**
         * Handles a request to completely reset the Loader.
         */
        @Override protected void onReset() {
            super.onReset();

            // Ensure the loader is stopped
            onStopLoading();

            // At this point we can release the resources associated with 'apps'
            // if needed.
            if (mItems != null) {
                mItems = null;
            }
        }
    }

    private class ItemAdapter extends BaseAdapter {

        private final LayoutInflater mInflater;

        ItemAdapter() {
            mInflater = getLayoutInflater(null);
        }

        private List<Item> mData = Lists.newArrayList();

        void setData(List<Item> data) {
            mData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Item getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mData.get(position).id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                v = mInflater.inflate(R.layout.list_item_dept_tree, parent, false);
            }

            ImageView thumb = (ImageView) v.findViewById(R.id.thumbnail);
            TextView display = (TextView) v.findViewById(R.id.display);
            ImageView favorite = (ImageView) v.findViewById(R.id.favorite);

            Item item = getItem(position);
            switch (item.type) {
                case Item.TYPE_DEPT: {

                    thumb.setImageResource(R.drawable.ic_action_group);
                    display.setText(item.display);
                    display.setTypeface(null, Typeface.NORMAL);
                    favorite.setVisibility(View.INVISIBLE);

                    break;
                }
                case Item.TYPE_OFFICER: {

                    thumb.setImageResource(R.drawable.ic_action_person);
                    display.setText(item.display);
                    display.setTypeface(null, Typeface.BOLD);
                    boolean starred = (Boolean) item.data.get("starred");
                    if (starred) {
                        favorite.setVisibility(View.VISIBLE);
                    } else {
                        favorite.setVisibility(View.INVISIBLE);
                    }

                    break;
                }
                default:
                    throw new IllegalStateException("invalid item type");
            }

            return v;
        }
    }

    public static class Item {
        public static final int TYPE_DEPT = 0;
        public static final int TYPE_OFFICER = 1;
        int type;

        int id;
        String display;
        Map<String, Object> data;
    }
}
