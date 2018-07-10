package alpha.labgo.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import alpha.labgo.R;
import alpha.labgo.models.InventoryItem;
import de.hdodenhof.circleimageview.CircleImageView;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.InventoryItemViewHolder> {

    private static final String TAG = "InventoryItemAdapter";

    public interface ShowItemEditDialog {
        void showItemEditDialog();
    }

    public ShowItemEditDialog mOnEditItemListener;

    private Context mContext;
    private ArrayList<InventoryItem> mInventoryItems = new ArrayList<>();

    public class InventoryItemViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mToolImage;
        private TextView mToolName;
        private TextView mDescription;
        private TextView mToolQuantity;
        private RelativeLayout mParentLayout; // for onclick listener

        public InventoryItemViewHolder(View itemView) {
            super(itemView);
            mToolImage = itemView.findViewById(R.id.image_item);
            mToolName = itemView.findViewById(R.id.text_item_name);
            mDescription = itemView.findViewById(R.id.text_item_description);
            mToolQuantity = itemView.findViewById(R.id.text_quantity);
            mParentLayout = itemView.findViewById(R.id.layout_inventory_parent);
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
        try {
            mOnEditItemListener = (ShowItemEditDialog) mContext;
        } catch (ClassCastException e) {
            Log.e(TAG, "onCreateViewHolder: ClassCastException: " + e.getMessage());
        }
        return new InventoryItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryItemViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        InventoryItem inventoryItem = mInventoryItems.get(position);

        Glide.with(mContext)
                .asBitmap()
                .load(inventoryItem.getItemImage())
                .into(holder.mToolImage);

        holder.mToolName.setText(inventoryItem.getItemName());
        holder.mDescription.setText(inventoryItem.getItemDescription());
        int quantity = inventoryItem.getItemQuantity();
        holder.mToolQuantity.setText(Integer.toString(quantity));
        if (quantity == 0) {
            holder.mToolQuantity.setTextColor(mContext.getResources().getColor(R.color.red));
            holder.mToolName.setTextColor(mContext.getResources().getColor(R.color.red));
        } else {
            holder.mToolQuantity.setTextColor(mContext.getResources().getColor(R.color.dark_gray));
            holder.mToolName.setTextColor(mContext.getResources().getColor(R.color.black));
        }

        holder.mParentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick: clicked on: " + mInventoryItems.get(position).getItemName());

                // Setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Choose an action");

                // Add a list
                String[] options = {"Edit"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Edit
                                mOnEditItemListener.showItemEditDialog();
                                break;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
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
