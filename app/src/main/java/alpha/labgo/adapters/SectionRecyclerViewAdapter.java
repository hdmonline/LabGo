package alpha.labgo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import alpha.labgo.R;
import alpha.labgo.RecyclerItemType;

public class SectionRecyclerViewAdapter extends RecyclerView.Adapter<SectionRecyclerViewAdapter.SectionViewHolder> {

    private static final String TAG = "SectionRecyclerViewAdapter";

    private ArrayList<String> mSectionLabels = new ArrayList<>();
    private ArrayList<RecyclerItemType> mRecyclerItemTypes = new ArrayList<>();
    private Context mContext;

    public class SectionViewHolder extends RecyclerView.ViewHolder {
        private TextView mSectionLabel;
        private RecyclerView mItemRecyclerView;

        public SectionViewHolder(View itemView) {
            super(itemView);
            mSectionLabel = itemView.findViewById(R.id.section_label);
            mItemRecyclerView = itemView.findViewById(R.id.layout_item_recycler);
        }
    }

    public SectionRecyclerViewAdapter(Context context, ArrayList<RecyclerItemType> recyclerItemTypes) {
        this.mContext = context;
        this.mRecyclerItemTypes = recyclerItemTypes;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_borrowed_item, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        holder.mSectionLabel.setText(mSectionLabels.get(position));
        RecyclerItemType type = mRecyclerItemTypes.get(position);
//        switch (type) {
//            case BORROWED_ITEM:
//                BorrowedItemAdapter adapter =
//                        new BorrowedItemAdapter(mContext, )
//        }
    }

    @Override
    public int getItemCount() {
        return mSectionLabels.size();
    }
}
