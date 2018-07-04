package alpha.labgo.fragments;

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import alpha.labgo.R;
import alpha.labgo.adapters.BorrowedItemAdapter;
import alpha.labgo.adapters.InventoryItemAdapter;
import alpha.labgo.database.RestUtils;
import alpha.labgo.models.BorrowedItem;
import alpha.labgo.models.InventoryItem;

// TODO
public class InventoryFragment extends BaseFragment implements LoaderCallbacks<ArrayList<InventoryItem>> {

    private static final String TAG = "InventoryFragment";

    private static final int INVENTORY_LOADER_ID = 23;

    private String mGtid;

    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private TextView mNoItemText;

    private InventoryItemAdapter mInventoryItemAdapter;
    private ProgressBar mLoadingIndicator;

    /**
     * This method is to pass GTID from toolbar activity to this fragment.
     *
     * @param gtid Student GTID
     * @return
     */
    public static InventoryFragment newInstance(String gtid) {
        InventoryFragment inventoryFragment = new InventoryFragment();
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

        // Views
        View rootView = inflater.inflate(R.layout.fragment_inventory, container, false);
        mRecyclerView = rootView.findViewById(R.id.recyclerview_inventory);
        mErrorMessageDisplay = rootView.findViewById(R.id.text_inventory_error_message_display);
        mNoItemText = rootView.findViewById(R.id.text_inventory_no_item);
        mLoadingIndicator = rootView.findViewById(R.id.pb_inventory_loading_indicator);

        // Set the layoutManager on mRecyclerView
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // will pass the parameters later
        // TODO: chech if this constructor usable
        mInventoryItemAdapter = new InventoryItemAdapter(getContext());

        // set adapter
        mRecyclerView.setAdapter(mInventoryItemAdapter);

        int loaderId = INVENTORY_LOADER_ID;
        LoaderCallbacks<ArrayList<InventoryItem>> callback = InventoryFragment.this;
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
        mNoItemText.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will show a text on the view when there is no items checked out.
     */
    private void showNoItemText() {
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
        getLoaderManager().restartLoader(INVENTORY_LOADER_ID, null, InventoryFragment.this);
    }
}
