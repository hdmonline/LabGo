package alpha.labgo.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import alpha.labgo.R;
import alpha.labgo.adapters.BorrowedItemAdapter;
import alpha.labgo.database.RestUtils;
import alpha.labgo.models.BorrowedItem;

// TODO
public class DashboardFragment extends Fragment implements LoaderCallbacks<ArrayList<BorrowedItem>> {

    private static final String TAG = "DashboardFragment";

    private static final int DASHBOARD_LOADER_ID = 22;


    private String mGtid;

    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private TextView mNoItemText;

    private BorrowedItemAdapter mBorrowedItemAdapter;

    private ProgressBar mLoadingIndicator;

    // TODO: delete this when done!
    private static final String test="[ { \"item_name\": \"powerdrill\", \"item_image_url\": \"https://images.homedepot-static.com/productImages/1f89a066-4101-4ade-b0c9-40f55ea30692/svn/ryobi-power-drills-p1810-64_1000.jpg\", \"checkout_time\": \"asdfasdf\" }, { \"item_name\": \"powerdrill2\", \"item_image_url\": \"https://images.homedepot-static.com/productImages/1f89a066-4101-4ade-b0c9-40f55ea30692/svn/ryobi-power-drills-p1810-64_1000.jpg\", \"checkout_time\": \"asdfasdf2\" }, { \"item_name\": \"powerdrill3\", \"item_image_url\": \"https://images.homedepot-static.com/productImages/1f89a066-4101-4ade-b0c9-40f55ea30692/svn/ryobi-power-drills-p1810-64_1000.jpg\", \"checkout_time\": \"asdfasdf3\" }]";

    /**
     * This method is to pass GTID from main activity to this fragment.
     *
     * @param gtid Student GTID
     * @return
     */
    public static DashboardFragment newInstance(String gtid) {
        DashboardFragment dashboardFragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString("gtid", gtid);
        dashboardFragment.setArguments(args);
        return dashboardFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mRecyclerView = rootView.findViewById(R.id.recyclerview_dashboard);
        mErrorMessageDisplay = rootView.findViewById(R.id.text_error_message_display);
        mNoItemText = rootView.findViewById(R.id.text_no_item);
        mGtid = getArguments().getString("gtid");

        // Set the layoutManager on mRecyclerView
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // will pass the parameters later
        // TODO: chech if this constructor usable
        mBorrowedItemAdapter = new BorrowedItemAdapter(getContext());

        // set adapter
        mRecyclerView.setAdapter(mBorrowedItemAdapter);

        mLoadingIndicator = rootView.findViewById(R.id.pb_loading_indicator);

        int loaderId = DASHBOARD_LOADER_ID;
        LoaderCallbacks<ArrayList<BorrowedItem>> callback = DashboardFragment.this;
        Bundle bundleDashboard = null;

        // TODO: check getActivity().getSupportLoaderManager()
        getLoaderManager().initLoader(loaderId, bundleDashboard, callback);

        // for testing views
        //loadBorrowedTools();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * only for testing recycler view
     * TODO: delete this when done!
     */
    private void loadBorrowedTools() {
        ArrayList<BorrowedItem> borrowedItems = new ArrayList<>();
        borrowedItems.add(new BorrowedItem("https://images.homedepot-static.com/productImages/1f89a066-4101-4ade-b0c9-40f55ea30692/svn/ryobi-power-drills-p1810-64_1000.jpg",
                "powerdrill", "description", "asdfasdfasdfasdf"));
        mBorrowedItemAdapter.setList(borrowedItems);
    }

    @NonNull
    @Override
    public Loader<ArrayList<BorrowedItem>> onCreateLoader(int id, @Nullable Bundle args) {
        // TODO: check the context here
        return new AsyncTaskLoader<ArrayList<BorrowedItem>>(getContext()) {

            ArrayList<BorrowedItem> mBorrowedItems = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (mBorrowedItems != null) {
                    deliverResult(mBorrowedItems);
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
            public ArrayList<BorrowedItem> loadInBackground() {
                // COMPLETE: this should be mGtid not "903235213". it's for testing right now.
                ArrayList<BorrowedItem> data = RestUtils.studentBorrowedItems(mGtid);
                return data;
                //return RestUtils.testJson(test);
            }

            /**
             * Send the result of the load to the registered listener.
             * @param data The result of the load
             */
            public void deliverResult(ArrayList<BorrowedItem> data) {
                mBorrowedItems = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<BorrowedItem>> loader, ArrayList<BorrowedItem> data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mBorrowedItemAdapter.setList(data);
        if (data == null) {
            showErrorMessage();
        } else if (data.size() == 0) {
            showNoItemText();
        } else {
            showBorrowedItemView();
        }
    }



    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<BorrowedItem>> loader) {

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
    private void showBorrowedItemView() {
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
        mBorrowedItemAdapter.setList(new ArrayList<BorrowedItem>());
    }

    /**
     * refresh data
     */
    public void refreshData() {
        invalidateData();
        getLoaderManager().restartLoader(DASHBOARD_LOADER_ID, null, DashboardFragment.this);
    }
}
