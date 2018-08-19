package alpha.labgo.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

import alpha.labgo.R;
import alpha.labgo.adapters.StudentInventoryAdapter;
import alpha.labgo.backend.RestUtils;
import alpha.labgo.models.BorrowedItem;
import alpha.labgo.models.PreStudentInventory;
import alpha.labgo.models.StudentInventory;

public class DashboardTaFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        LoaderCallbacks<PreStudentInventory> {

    private static final String TAG = "DashboardTaFragment";
    private static final int STUDENT_INVENTORY_LOADER_ID = 25;

    private String mGtid;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private TextView mNoItemText;
    private ProgressBar mLoadingIndicator;

    private StudentInventoryAdapter mAdapter;

    FirebaseFirestore mFirestore;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param gtid Student GTID
     * @return A new instance of fragment DashboardTaFragment.
     */
    public static DashboardTaFragment newInstance(String gtid) {
        DashboardTaFragment dashboardFragment = new DashboardTaFragment();
        Bundle args = new Bundle();
        args.putString("gtid", gtid);
        dashboardFragment.setArguments(args);
        return dashboardFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirestore = FirebaseFirestore.getInstance();
        if (getArguments() != null) {
            mGtid = getArguments().getString("gtid");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Views
        View rootView = inflater.inflate(R.layout.fragment_dashboard_ta, container, false);
        mRecyclerView = rootView.findViewById(R.id.recyclerview_dashboard_ta);
        mErrorMessageDisplay = rootView.findViewById(R.id.text_dashboard_error_message_display_ta);
        mNoItemText = rootView.findViewById(R.id.text_dashboard_no_item_ta);
        mLoadingIndicator = rootView.findViewById(R.id.pb_dashboard_loading_indicator_ta);

        // SwipeRefreshLayout
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipe_container_dashboard_ta);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        // TODO: may need to change color of it
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        // will pass the list later
        mAdapter = new StudentInventoryAdapter(getContext());

        // set adapter
        mRecyclerView.setAdapter(mAdapter);

        // Set the layoutManager on mRecyclerView
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        //mRecyclerView.setHasFixedSize(true);

        int loaderId = STUDENT_INVENTORY_LOADER_ID;
        LoaderCallbacks<PreStudentInventory> callback = DashboardTaFragment.this;
        Bundle bundleDashboard = null;

        // TODO: check getActivity().getSupportLoaderManager()
        getLoaderManager().initLoader(loaderId, bundleDashboard, callback);

        // for testing views
        //loadBorrowedTools();
        return rootView;
    }

    @Override
    public Loader<PreStudentInventory> onCreateLoader(int id, @Nullable Bundle args) {
        // TODO: check the context here
        return new AsyncTaskLoader<PreStudentInventory>(getContext()) {

            PreStudentInventory mPreStudentInventory = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (mPreStudentInventory != null) {
                    deliverResult(mPreStudentInventory);
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
            public PreStudentInventory loadInBackground() {
                PreStudentInventory data = RestUtils.getStudentInventories(DashboardTaFragment.this);
                return data;
            }

            /**
             * Send the result of the load to the registered listener.
             * @param data The result of the load
             */
            public void deliverResult(PreStudentInventory data) {
                mPreStudentInventory = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<PreStudentInventory> loader, PreStudentInventory data) {
        Log.d(TAG, "onLoadFinished");
        findStudentNames(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<PreStudentInventory> loader) {

    }

    private void findStudentNames(PreStudentInventory preStudentInventory) {

        Log.d(TAG, "findStudentNames");

        if (preStudentInventory == null || preStudentInventory.size() == 0) {
            mAdapter.setList(new ArrayList<StudentInventory>());
            showNoItemText();
            return;
        }

        final ArrayList<String> gtids = preStudentInventory.getGtids();
        final ArrayList<ArrayList<BorrowedItem>> studentItems = preStudentInventory.getStudentItems();
        final HashMap<String, String> gtidNames = new HashMap<>();

        final int numStudents = gtids.size();

        for (int i = 0; i < numStudents; i++) {
            final String currGtid = gtids.get(i);
            mFirestore.collection("gtid").document(currGtid).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                String currName = documentSnapshot.getString("name");
                                gtidNames.put(currGtid, currName);

                                // all the names are received, put them together into StudentInventory objects
                                if (gtidNames.size() == numStudents) {
                                    ArrayList<StudentInventory> studentInventories = buildStudentInventories(gtidNames, gtids, studentItems);
                                    mAdapter.setList(studentInventories);
                                    showBorrowedItemView();
                                }
                            } else {
                                Log.e(TAG, "getStudentInventories: can't get student name from gtid, please check internet or firestore");
                            }
                        }
                    });
        }
    }

    private static ArrayList<StudentInventory> buildStudentInventories(
            HashMap<String, String> gtidNames,
            ArrayList<String> gtids,
            ArrayList<ArrayList<BorrowedItem>> items) {

        ArrayList<StudentInventory> studentInventories = new ArrayList<>();
        int numStudent = gtidNames.size();
        if (numStudent != gtids.size() || numStudent != items.size()) {
            Log.e(TAG, "buildStudentInventories: sizes of inputs are different.");
            return null;
        }

        for (int i = 0; i < numStudent; i++)  {
            studentInventories.add(new StudentInventory(
                    gtidNames.get(gtids.get(i)),
                    gtids.get(i),
                    items.get(i)
            ));
        }
        return studentInventories;
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
        mSwipeRefreshLayout.setRefreshing(false);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mNoItemText.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will show a text on the view when there is no items checked out.
     */
    private void showNoItemText() {
        mSwipeRefreshLayout.setRefreshing(false);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mNoItemText.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    public void invalidateData() {
        mAdapter.setList(new ArrayList<StudentInventory>());
    }

    /**
     * refresh data
     */
    public void refreshData() {
        invalidateData();
        getLoaderManager().restartLoader(STUDENT_INVENTORY_LOADER_ID, null, DashboardTaFragment.this);
    }

    /**
     * Filter list with constraint string
     *
     * @param constraint Constraint string
     */
    public void filterData(String constraint) {
        mAdapter.getFilter().filter(constraint);
    }

    @Override
    public void onRefresh() {
        refreshData();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mAdapter.onRestoreInstanceState(savedInstanceState);
    }
}
