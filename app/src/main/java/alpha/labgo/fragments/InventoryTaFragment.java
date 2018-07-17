package alpha.labgo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;

import alpha.labgo.UpdateInventoryActivity;
import alpha.labgo.R;
import alpha.labgo.UpdateItemActivity;
import alpha.labgo.adapters.InventoryItemAdapter;
import alpha.labgo.backend.RestUtils;
import alpha.labgo.models.InventoryItem;

public class InventoryTaFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        LoaderCallbacks<ArrayList<InventoryItem>> {

    private static final String TAG = "InventoryFragment";

    private static final int INVENTORY_TA_LOADER_ID = 24;
    private static final int ADD_INVENTORY = 10;
    private static final int DELETE_INVENTORY = 11;
    private static final int ADD_ITEM = 12;
    private static final int EDIT_ITEM = 13;

    /* Use this code for returning instead of use CommonStatusCodes.SUCCESS.
     * Because when returning back by finish(), it sends that code.
     */
    private static final int SUCCESS = 123;


    private String mGtid;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private TextView mNoItemText;
    private FloatingActionMenu mFam;
    private FloatingActionButton mFabAddInventory, mFabAddItem, mFabDeleteInventory;

    private InventoryItemAdapter mInventoryItemAdapter;
    private ProgressBar mLoadingIndicator;
    
    /**
     * This method is to pass GTID from toolbar activity to this fragment.
     *
     * @param gtid Student GTID
     * @return
     */
    public static InventoryTaFragment newInstance(String gtid) {
        InventoryTaFragment inventoryFragment = new InventoryTaFragment();
        Bundle args = new Bundle();
        args.putString("gtid", gtid);
        inventoryFragment.setArguments(args);
        return inventoryFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mGtid = getArguments().getString("gtid");

        // Widgets
        View rootView = inflater.inflate(R.layout.fragment_inventory, container, false);
        mRecyclerView = rootView.findViewById(R.id.recyclerview_inventory);
        mErrorMessageDisplay = rootView.findViewById(R.id.text_inventory_error_message_display);
        mNoItemText = rootView.findViewById(R.id.text_inventory_no_item);
        mLoadingIndicator = rootView.findViewById(R.id.pb_inventory_loading_indicator);
        mFam = rootView.findViewById(R.id.fam_main);
        mFabAddInventory = rootView.findViewById(R.id.fab_main_add_inventory);
        mFabDeleteInventory = rootView.findViewById(R.id.fab_main_delete_inventory);
        mFabAddItem = rootView.findViewById(R.id.fab_main_add_item);

        // SwipeRefreshLayout
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container_inventory);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        // Handling FABs clicked
        mFabAddItem.setOnClickListener(onFabClick());
        mFabAddInventory.setOnClickListener(onFabClick());
        mFabDeleteInventory.setOnClickListener(onFabClick());

//        mFam.setVisibility(View.VISIBLE);
//        mFam.setClickable(true);

        // Handling FAM close by clicking the background
        mFam.setOnMenuToggleListener(onMenuToggleListener());

        // Set the layoutManager on mRecyclerView
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // will pass the parameters later
        // TODO: check if this constructor usable
        mInventoryItemAdapter = new InventoryItemAdapter(getActivity());

        // set adapter
        mRecyclerView.setAdapter(mInventoryItemAdapter);

        int loaderId = INVENTORY_TA_LOADER_ID;
        LoaderCallbacks<ArrayList<InventoryItem>> callback = InventoryTaFragment.this;
        Bundle bundleInventory = null;

        getLoaderManager().initLoader(loaderId, bundleInventory, callback);

        return rootView;
    }

    @Override
    public Loader<ArrayList<InventoryItem>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<InventoryItem>>(getContext()) {

            ArrayList<InventoryItem> mInventoryItems = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (mInventoryItems != null) {
                    deliverResult(mInventoryItems);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            /**
             * This is the method of the AsyncTaskLoader that will load and parse the JSON data
             * in the background.
             *
             * @return Borrowed item data
             */
            @Override
            public ArrayList<InventoryItem> loadInBackground() {

                ArrayList<InventoryItem> data = RestUtils.getInventoryItems();
                return data;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<InventoryItem>> loader, ArrayList<InventoryItem> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mInventoryItemAdapter.setList(data);
        if (data == null) {
            showErrorMessage();
        } else if (data.size() == 0) {
            showNoItemText();
        } else {
            showInventoryItemView();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<InventoryItem>> loader) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_INVENTORY) {
            if (resultCode == SUCCESS) {
                Toast.makeText(getActivity(), "The inventory item has been added!", Toast.LENGTH_LONG).show();
            } else if (resultCode == CommonStatusCodes.ERROR) {
                Toast.makeText(getActivity(), "Add inventory item FAILED!", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == DELETE_INVENTORY) {
            if (resultCode == SUCCESS) {
                Toast.makeText(getActivity(), "The inventory item has been deleted!", Toast.LENGTH_LONG).show();
            } else if (resultCode == CommonStatusCodes.ERROR) {
                Toast.makeText(getActivity(), "Delete inventory item FAILED!", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == ADD_ITEM) {
            if (resultCode == SUCCESS) {
                Toast.makeText(getActivity(), "The item has been added!", Toast.LENGTH_LONG).show();
            } else if (resultCode == CommonStatusCodes.ERROR) {
                Toast.makeText(getActivity(), "Add item FAILED!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        /* hide no item text */
    }

    /**
     * This method will make the View for the borrowed item data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showInventoryItemView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mNoItemText.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will show a text on the view when there is no items checked out.
     */
    private void showNoItemText() {
        mSwipeRefreshLayout.setRefreshing(false);
        mNoItemText.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    public void invalidateData() {
        mInventoryItemAdapter.setList(new ArrayList<InventoryItem>());
    }

    /**
     * refresh data
     */
    public void refreshData() {
        invalidateData();
        getLoaderManager().restartLoader(INVENTORY_TA_LOADER_ID, null, InventoryTaFragment.this);
    }

    /**
     * This method handles all the click events on FABs.
     *
     * @return
     */
    private View.OnClickListener onFabClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v == mFabAddInventory) {
                    Intent toUpdateInventoryItem = new Intent(getActivity(), UpdateInventoryActivity.class);
                    int addOrDelete = ADD_INVENTORY;
                    toUpdateInventoryItem.putExtra("addOrDelete", addOrDelete);
                    startActivityForResult(toUpdateInventoryItem, ADD_INVENTORY);
                } else if (v == mFabDeleteInventory) {
                    Intent toUpdateInventoryItem = new Intent(getActivity(), UpdateInventoryActivity.class);
                    int addOrDelete = DELETE_INVENTORY;
                    toUpdateInventoryItem.putExtra("addOrDelete", addOrDelete);
                    startActivityForResult(toUpdateInventoryItem, DELETE_INVENTORY);
                } else if (v == mFabAddItem) {
                    Intent toUpdateItem = new Intent(getActivity(), UpdateItemActivity.class);
                    toUpdateItem.putExtra("addOrEdit", ADD_ITEM);
                    startActivityForResult(toUpdateItem, ADD_ITEM);
                }
                mFam.close(true);
            }
        };
    }

    /**
     * This method is to handle when FAM is open,
     * it can be closed by clicking the background.
     *
     * @return
     */
    private FloatingActionMenu.OnMenuToggleListener onMenuToggleListener() {
        return new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened) {
                    mFam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mFam.close(true);
                        }
                    });
                }
            }
        };
    }

    /**
     * Filter list with constraint string
     *
     * @param constraint Constraint string
     */
    public void filterData(String constraint) {
        mInventoryItemAdapter.getFilter().filter(constraint);
    }

    @Override
    public void onRefresh() {
        refreshData();
    }
}
