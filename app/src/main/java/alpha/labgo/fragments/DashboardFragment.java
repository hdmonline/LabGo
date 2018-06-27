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

// TODO
public class DashboardFragment extends Fragment implements LoaderCallbacks<String[]> {

    private static final String TAG = "DashboardFragment";

    private static final int REST_DASHBOARD_LOADER = 22;

    private static final int DASHBOARD_LOADER_ID = 0;


    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;

    private BorrowedItemAdapter mBorrowedItemAdapter;

    private ProgressBar mLoadingIndicator;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mRecyclerView = rootView.findViewById(R.id.recyclerview_dashboard);
        mErrorMessageDisplay = rootView.findViewById(R.id.tv_error_message_display);

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
        LoaderCallbacks<String[]> callback = DashboardFragment.this;
        Bundle bundleDashboard = null;

        // TODO: check getActivity().getSupportLoaderManager()
        getLoaderManager().initLoader(loaderId, bundleDashboard, callback);

        // for testing views
        loadBorrowedTools();
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
     */
    private void loadBorrowedTools() {
        ArrayList<String> toolImages = new ArrayList<>();
        ArrayList<String> toolNames = new ArrayList<>();
        ArrayList<String> checkOutTimes = new ArrayList<>();
        toolImages.add("https://images.homedepot-static.com/productImages/1f89a066-4101-4ade-b0c9-40f55ea30692/svn/ryobi-power-drills-p1810-64_1000.jpg");
        toolNames.add("powerdrill");
        checkOutTimes.add("asdfasdfasdfasdf");
        mBorrowedItemAdapter.setList(toolImages, toolNames, checkOutTimes);
    }

    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable Bundle args) {
        // TODO: check the context here
        return new AsyncTaskLoader<String[]>(getContext()) {

            String[] mBorrowedItems = null;

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
            public String[] loadInBackground() {
                return new String[0];
            }

            /**
             * Send the result of the load to the registered listener.
             * @param data The result of the load
             */
            public void deliverResult(String[] data) {
                mBorrowedItems = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mBorrowedItemAdapter.setList(data);
        if (data == null) {
            showErrorMessage();
        } else {
            showBorrowedItemView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {

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
    }

    /**
     * This method will make the View for the borrowed item data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showBorrowedItemView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
}
