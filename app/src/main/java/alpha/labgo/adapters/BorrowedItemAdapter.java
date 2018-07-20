package alpha.labgo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import alpha.labgo.R;
import alpha.labgo.models.BorrowedItem;
import de.hdodenhof.circleimageview.CircleImageView;

public class BorrowedItemAdapter extends RecyclerView.Adapter<BorrowedItemAdapter.BorrowedItemViewHolder>
        implements Filterable {

    private static final String TAG = "BorrowedItemAdapter";

    private Context mContext;
    private ArrayList<BorrowedItem> mBorrowedItems = new ArrayList<>();
    private ArrayList<BorrowedItem> mFilteredBorrowedItems = new ArrayList<>();

    public class BorrowedItemViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mToolImage;
        private TextView mToolName;
        private TextView mCheckOutTime;
        private TextView mDescription;
        private RelativeLayout mParentLayout;

        public BorrowedItemViewHolder(View itemView) {
            super(itemView);
            mToolImage = itemView.findViewById(R.id.image_item);
            mToolName = itemView.findViewById(R.id.text_item_name);
            mCheckOutTime = itemView.findViewById(R.id.text_checkout_time);
            mDescription = itemView.findViewById(R.id.text_item_description);
            mParentLayout = itemView.findViewById(R.id.layout_borrowed_parent);
        }
    }

    /**
     * Default constructor
     *
     * @param context Context
     */
    public BorrowedItemAdapter(Context context) {
        this.mContext = context;
    }

    /**
     * Constructor with initialization
     *
     * @param context
     * @param borrowedItems
     */
    public BorrowedItemAdapter(Context context, ArrayList<BorrowedItem> borrowedItems) {
        this.mContext = context;
        this.mBorrowedItems = borrowedItems;
    }

    @NonNull
    @Override
    public BorrowedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_borrowed_item, parent, false);
        return new BorrowedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowedItemAdapter.BorrowedItemViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        BorrowedItem borrowedItem = mFilteredBorrowedItems.get(position);

        Glide.with(mContext)
                .asBitmap()
                .load(borrowedItem.getItemImage())
                .into(holder.mToolImage);

        holder.mToolName.setText(borrowedItem.getItemName());
        holder.mDescription.setText(borrowedItem.getItemDescription());
        holder.mCheckOutTime.setText(borrowedItem.getCheckOutTime());
    }

    @Override
    public int getItemCount() {
        return mFilteredBorrowedItems.size();
    }

    /**
     * This method is used to set all the data of the list
     *
     * @param borrowedItems List of borrowed items.
     */
    public void setList(ArrayList<BorrowedItem> borrowedItems) {
        mBorrowedItems = borrowedItems;
        mFilteredBorrowedItems = borrowedItems;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String constrainString = constraint.toString().toLowerCase();

                if (constrainString.isEmpty()) {
                    mFilteredBorrowedItems = mBorrowedItems;
                } else {
                    ArrayList<BorrowedItem> filteredList = new ArrayList<>();

                    for (BorrowedItem item : mBorrowedItems) {
                        if (item.getItemName().toLowerCase().contains(constrainString) ||
                                item.getItemDescription().toLowerCase().contains(constrainString)) {
                            filteredList.add(item);
                        }
                    }
                    mFilteredBorrowedItems = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredBorrowedItems;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredBorrowedItems = (ArrayList<BorrowedItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
