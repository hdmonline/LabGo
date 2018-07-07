package alpha.labgo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import alpha.labgo.R;
import alpha.labgo.models.InventoryItem;
import de.hdodenhof.circleimageview.CircleImageView;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.InventoryItemViewHolder> {

    private static final String TAG = "InventoryItemAdapter";

    private Context mContext;
    private ArrayList<InventoryItem> mInventoryItems = new ArrayList<>();

    public class InventoryItemViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mToolImage;
        private TextView mToolName;
        private TextView mDescription;
        private TextView mToolQuantity;

        public InventoryItemViewHolder(View itemView) {
            super(itemView);
            mToolImage = itemView.findViewById(R.id.image_item);
            mToolName = itemView.findViewById(R.id.text_item_name);
            mDescription = itemView.findViewById(R.id.text_item_description);
            mToolQuantity = itemView.findViewById(R.id.text_quantity);
        }
    }

    // default constructor
    public InventoryItemAdapter(Context context) {
        this.mContext = context;
    }

    public InventoryItemAdapter(Context context, ArrayList<InventoryItem> inventoryItems) {
        this.mContext = context;
        this.mInventoryItems = inventoryItems;
    }

    @NonNull
    @Override
    public InventoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_inventory_item, parent, false);
        return new InventoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryItemViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        InventoryItem inventoryItem = mInventoryItems.get(position);

        Glide.with(mContext)
                .asBitmap()
                .load(inventoryItem.getItemImage())
                .into(holder.mToolImage);

        holder.mToolName.setText(inventoryItem.getItemName());
        holder.mDescription.setText(inventoryItem.getItemDescription());
        holder.mToolQuantity.setText(Integer.toString(inventoryItem.getItemQuantity()));
    }

    @Override
    public int getItemCount() {
        return mInventoryItems.size();
    }

    /**
     * This method is used to set all the data of the list
     *
     * @param inventoryItems List of borrowed items.
     */
    public void setList(ArrayList<InventoryItem> inventoryItems) {
        mInventoryItems = inventoryItems;
        notifyDataSetChanged();
    }
}
