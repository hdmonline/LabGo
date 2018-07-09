package alpha.labgo.fragments;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import alpha.labgo.R;
import alpha.labgo.models.Item;


/**
 * This fragment show the items from the database.
 * The TAs can search, edit, add or delete an item from this fragment.
 */
public class ItemTaFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Item> {

    private static final String TAG = "ItemTaFragment";

    // TODO: check if it is useful
    private String mGtid;

    public ItemTaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param gtid Student GTID
     * @return A new instance of fragment ItemTaFragment.
     */
    public static ItemTaFragment newInstance(String gtid) {
        ItemTaFragment fragment = new ItemTaFragment();
        Bundle args = new Bundle();
        args.putString("gtid", gtid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGtid = getArguments().getString("gtid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_item_ta, container, false);
        return rootView;
    }

    @Override
    public Loader<Item> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Item> loader, Item data) {

    }

    @Override
    public void onLoaderReset(Loader<Item> loader) {

    }

    @Override
    public void onRefresh() {

    }
}
