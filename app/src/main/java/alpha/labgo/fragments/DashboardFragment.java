package alpha.labgo.fragments;

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

import alpha.labgo.R;
import alpha.labgo.adapters.BorrowedItemAdapter;

// TODO
public class DashboardFragment extends Fragment {

    private static final String TAG = "DashboardFragment";

    private RecyclerView mRecyclerView;

    private BorrowedItemAdapter mBorrowedItemAdapter;

    private ProgressBar mLoadingIndicator;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mRecyclerView = rootView.findViewById(R.id.recyclerview_dashboard);

        // Set the layoutManager on mRecyclerView
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        // will pass the parameters later
        // TODO: chech if this constructor usable
        mBorrowedItemAdapter = new BorrowedItemAdapter();

        // set adapter
        mRecyclerView.setAdapter(mBorrowedItemAdapter);

        mLoadingIndicator = rootView.findViewById(R.id.pb_loading_indicator);

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

    private void loadBorrowedTools() {

    }
}
