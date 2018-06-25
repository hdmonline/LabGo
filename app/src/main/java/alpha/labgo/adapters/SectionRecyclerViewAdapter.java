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

public class SectionRecyclerViewAdapter extends RecyclerView.Adapter<SectionRecyclerViewAdapter.SectionViewHolder> {

    private static final String TAG = "SectionRecyclerViewAdapter";

    private ArrayList<String> mToolNames = new ArrayList<>();
    private ArrayList<String> mToolImages = new ArrayList<>();
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

    public SectionRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_borrowed_item, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
