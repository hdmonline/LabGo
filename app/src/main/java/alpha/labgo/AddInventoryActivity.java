package alpha.labgo;

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

import java.util.ArrayList;

import alpha.labgo.adapters.ItemAdapter;
import alpha.labgo.database.RestUtils;
import alpha.labgo.dialogs.AddItemConfirmDialog;
import alpha.labgo.models.Item;
import alpha.labgo.models.ScannedItem;


public class AddInventoryActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        AddItemConfirmDialog.OnAddInventoryListener,
        LoaderCallbacks<ArrayList<Item>> {

    private static final String TAG = "AddInventoryActivity";
    private static final int ADD_ITEM_LOADER_ID = 28;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);

        mScannedSingleItem = false;

        // Views
        mToolbar = findViewById(R.id.toolbar_add_inventory);
        mRecyclerView = findViewById(R.id.recyclerview_search_item);
        mLoadingIndicator = findViewById(R.id.pb_add_inventory_loading);
        mRfidTag = findViewById(R.id.text_item_tag);

        // Display toolbar and add back button
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // SwipeRefreshLayout
        mSwipeRefreshLayout = findViewById(R.id.swipe_container_add_item);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        // Set the layoutManager on mRecyclerView
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(AddInventoryActivity.this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVisibility(View.INVISIBLE);

        // Set adapter
        mItemAdapter = new ItemAdapter(AddInventoryActivity.this);
        mRecyclerView.setAdapter(mItemAdapter);
        LoaderCallbacks<ArrayList<Item>> callback = AddInventoryActivity.this;
        Bundle bundleAddItem = null;

        getSupportLoaderManager().initLoader(ADD_ITEM_LOADER_ID, bundleAddItem, callback);

        new RestUtils.ListNewTags(AddInventoryActivity.this).execute();
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

        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
            default:
                break;
        }

        if (item == mSearch) {
            // TODO: check if this is working
            mRecyclerView.setVisibility(View.VISIBLE);
            mRfidTag.setVisibility(View.INVISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }



    @NonNull
    @Override
    public Loader<ArrayList<Item>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<ArrayList<Item>>(AddInventoryActivity.this) {

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

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    public void invalidateData() {
        mItemAdapter.setList(new ArrayList<Item>());
    }

    /**
     * refresh data
     */
    public void refreshData() {
        invalidateData();
        getSupportLoaderManager().restartLoader(ADD_ITEM_LOADER_ID,
                null, AddInventoryActivity.this);
    }

    public void refreshUi(ArrayList<ScannedItem> scannedItems) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (scannedItems.size() > 1) {
            mScannedSingleItem = false;
            mRfidTag.setText(R.string.clean_scanned_item_hint);
            mRfidTag.setBackgroundColor(Color.parseColor("#00000000"));
            mRfidTag.setTextColor(Color.parseColor("#80000000"));
        } else if (scannedItems.size() == 0) {
            mScannedSingleItem = false;
            mRfidTag.setText(R.string.refresh_scanned_item_hint);
            mRfidTag.setBackgroundColor(Color.parseColor("#00000000"));
            mRfidTag.setTextColor(Color.parseColor("#80000000"));
        } else {
            mScannedSingleItem = true;
            String result = "Scanned RFID tag: " + scannedItems.get(0).getRfidTag();
            mRfidTag.setText(result);
            mRfidTag.setBackgroundColor(Color.parseColor("#FF00DD00"));
            mRfidTag.setTextColor(Color.parseColor("#FFFFFFFF"));
        }
    }

    @Override
    public void onRefresh() {
        new RestUtils.ListNewTags(AddInventoryActivity.this).execute();
    }

    /**
     * This method updates UI after hitting the OK button on the dialog.
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

    public void finishing() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        finish();
    }
}
