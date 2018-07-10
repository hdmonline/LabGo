package alpha.labgo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;

import alpha.labgo.adapters.ItemAdapter;
import alpha.labgo.backend.RestUtils;
import alpha.labgo.dialogs.ClearTagsDialog;
import alpha.labgo.dialogs.UpdateInventoryConfirmDialog;
import alpha.labgo.models.Item;
import alpha.labgo.models.ScannedItem;


public class UpdateInventoryActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        UpdateInventoryConfirmDialog.OnAddInventoryListener,
        LoaderCallbacks<ArrayList<Item>> {

    private static final String TAG = "UpdateInventoryActivity";
    private static final int ADD_ITEM_LOADER_ID = 28;
    private static final int ADD_INVENTORY = 10;
    private static final int DELETE_INVENTORY = 11;

    /* Use this code for returning instead of use CommonStatusCodes.SUCCESS.
     * Because when returning back by finish(), it sends that code.
     */
    private static final int SUCCESS = 123;

    // View
    private Toolbar mToolbar;
    private MenuItem mRefresh;
    private MenuItem mQrCode;
    private MenuItem mSearch;
    private SearchView mSearchView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mRfidTag;
    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;

    private ItemAdapter mItemAdapter;

    private boolean mScannedSingleItem;

    private int mAddOrDelete;
    private String mTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_inventory);

        mAddOrDelete = getIntent().getIntExtra("addOrDelete", -1);

        mScannedSingleItem = false;

        // Views
        mToolbar = findViewById(R.id.toolbar_update_inventory);
        mRecyclerView = findViewById(R.id.recyclerview_search_item);
        mLoadingIndicator = findViewById(R.id.pb_add_inventory_loading);
        mRfidTag = findViewById(R.id.text_item_tag);

        // Display toolbar and add back button
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // SwipeRefreshLayout
        mSwipeRefreshLayout = findViewById(R.id.swipe_container_update_inventory);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        // Set the layoutManager on mRecyclerView
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(UpdateInventoryActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVisibility(View.INVISIBLE);

        // Set adapter
        mItemAdapter = new ItemAdapter(UpdateInventoryActivity.this, mAddOrDelete);
        mRecyclerView.setAdapter(mItemAdapter);
        LoaderCallbacks<ArrayList<Item>> callback = UpdateInventoryActivity.this;
        Bundle bundleAddItem = null;

        getSupportLoaderManager().initLoader(ADD_ITEM_LOADER_ID, bundleAddItem, callback);

        new RestUtils.ListNewTags(UpdateInventoryActivity.this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mScannedSingleItem) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mRfidTag.setVisibility(View.INVISIBLE);
                    mItemAdapter.getFilter().filter(newText);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mRefresh = menu.findItem(R.id.action_refresh);
        mQrCode = menu.findItem(R.id.action_qr_code);
        mSearch = menu.findItem(R.id.action_search_item);
        mRefresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        mQrCode.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        mSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS|MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        // Make the SearchView fill the width of the toolbar
        mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        search(mSearchView);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        } else if (item == mSearch) {
            if (mScannedSingleItem) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mRfidTag.setVisibility(View.INVISIBLE);
            }
        } else if (item == mRefresh) {
            refreshTags();
        }
        return super.onOptionsItemSelected(item);
    }



    @NonNull
    @Override
    public Loader<ArrayList<Item>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<ArrayList<Item>>(UpdateInventoryActivity.this) {

            ArrayList<Item> mItems = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (mItems != null) {
                    deliverResult(mItems);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * in the background.
             *
             * @return Add item tag
             */
            @Nullable
            @Override
            public ArrayList<Item> loadInBackground() {
                ArrayList<Item> data = RestUtils.getItems();
                return data;
            }

            /**
             * Send the result of the load to the registered listener.
             * @param data The result of the load
             */
            public void deliverResult(ArrayList<Item> data) {
                mItems = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Item>> loader, ArrayList<Item> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mItemAdapter.setList(data);
        mSwipeRefreshLayout.setRefreshing(false);
        //mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Item>> loader) {

    }

    @Override
    public void onRefresh() {
        refreshTags();
    }

    /**
     * This method updates UI after hitting the OK button on the dialog. (When updating inventory item)
     */
    @Override
    public void updateUi() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mRfidTag.setVisibility(View.VISIBLE);
        mRfidTag.setText(R.string.refresh_scanned_item_hint);
        mRfidTag.setBackgroundColor(Color.parseColor("#00000000"));
        mRfidTag.setTextColor(Color.parseColor("#80000000"));
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    public void onSuccessFinishing() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        Intent returnIntent = new Intent();
        setResult(SUCCESS, returnIntent);
        finish();
    }

    public void onFailureFinishing() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        Intent returnIntent = new Intent();
        setResult(CommonStatusCodes.ERROR, returnIntent);
        finish();
    }

    public void refreshUi(ArrayList<ScannedItem> scannedItems) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (scannedItems.size() > 1) {
            mScannedSingleItem = false;
//            mRfidTag.setText(R.string.clean_scanned_item_hint);
//            mRfidTag.setBackgroundColor(Color.parseColor("#00000000"));
//            mRfidTag.setTextColor(Color.parseColor("#80000000"));
            ClearTagsDialog dialog = new ClearTagsDialog().newInstance("Only 1 tag should be scanned. Press CLEAR to clear the tags.");
            dialog.show(getFragmentManager(), "ClearTagsDialog");
        } else if (scannedItems.size() == 0) {
            mScannedSingleItem = false;
            mRfidTag.setText(R.string.refresh_scanned_item_hint);
            mRfidTag.setBackgroundColor(Color.parseColor("#00000000"));
            mRfidTag.setTextColor(Color.parseColor("#80000000"));
        } else {
            mTag = scannedItems.get(0).getRfidTag();
            mItemAdapter.setTag(mTag);
            if (mAddOrDelete == DELETE_INVENTORY) {
                mRfidTag.setVisibility(View.INVISIBLE);
                mLoadingIndicator.setVisibility(View.VISIBLE);
                new RestUtils.GetItemByTag(this).execute(mTag);
            } else {
                mScannedSingleItem = true;
                String result = "Scanned RFID tag: " + mTag;
                mRfidTag.setText(result);
                mRfidTag.setBackgroundColor(Color.parseColor("#FF00DD00"));
                mRfidTag.setTextColor(Color.parseColor("#FFFFFFFF"));
            }
        }
    }

    /**
     * refresh data
     */
    public void refreshTags() {
        new RestUtils.ListNewTags(UpdateInventoryActivity.this).execute();
    }

    public void showDialogAfterGettingItem(Item item) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        UpdateInventoryConfirmDialog dialog = new UpdateInventoryConfirmDialog()
                .newInstance(mTag, item.getItemName(), item.getItemImage(), item.getItemDescription(), mAddOrDelete);
        dialog.show(getFragmentManager(), "UpdateInventoryConfirmDialog");
    }

    public void onDeleteFail() {
        String result = "Scanned RFID tag: " + mTag;
        mRfidTag.setText(result);
        mRfidTag.setBackgroundColor(Color.parseColor("#00000000"));
        mRfidTag.setTextColor(Color.parseColor("#80000000"));
        ClearTagsDialog dialog = new ClearTagsDialog().newInstance("No item found for this tag. Press CLEAR to clear the tags.");
        dialog.show(getFragmentManager(), "ClearTagsDialog");
    }

    public void showToast(String message) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        Toast.makeText(this, message,
                Toast.LENGTH_LONG).show();
    }
}
