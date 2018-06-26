package alpha.labgo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class BorrowedItemAdapter extends RecyclerView.Adapter<BorrowedItemAdapter.BorrowedItemViewHolder> {

    private static final String TAG = "BorrowedItemAdapter";

    private Context mContext;
    private ArrayList<String> mToolImages = new ArrayList<>();
    private ArrayList<String> mToolNames = new ArrayList<>();
    private ArrayList<String> mCheckOutTimes = new ArrayList<>();

    public class BorrowedItemViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mToolImage;
        private TextView mToolName;
        private TextView mCheckOutTime;
        private RelativeLayout mParentLayout;

        public BorrowedItemViewHolder(View itemView) {
            super(itemView);
            mToolImage = itemView.findViewById(R.id.item_image);
            mToolName = itemView.findViewById(R.id.item_name);
            mCheckOutTime = itemView.findViewById(R.id.item_checkout_time);
            mParentLayout = itemView.findViewById(R.id.layout_borrowed_parent);
        }
    }

    // default constructor
    public BorrowedItemAdapter() {

    }

    // constructor passing parameters
    public BorrowedItemAdapter(Context mContext, ArrayList<String> mToolImages, ArrayList<String> mToolNames, ArrayList<String> mCheckOutTimes) {
        this.mContext = mContext;
        this.mToolImages = mToolImages;
        this.mToolNames = mToolNames;
        this.mCheckOutTimes = mCheckOutTimes;
    }

    @NonNull
    @Override
    public BorrowedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_borrowed_item, parent, false);
        return new BorrowedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BorrowedItemAdapter.BorrowedItemViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

        Glide.with(mContext)
                .asBitmap()
                .load(mToolImages.get(position))
                .into(holder.mToolImage);

        holder.mToolName.setText(mToolNames.get(position));
        holder.mCheckOutTime.setText(mCheckOutTimes.get(position));
    }

    @Override
    public int getItemCount() {
        return mCheckOutTimes.size();
    }

    /**
     * This method is used to set all the data of the list
     *
     * @param toolImages The new URLS of tool images to be displayed.
     * @param toolNames The new tool names to be displayed.
     * @param checkOutTimes The new tool check out times to be displayed.
     */
    public void setList(ArrayList<String> toolImages,
                        ArrayList<String> toolNames,
                        ArrayList<String> checkOutTimes) {
        mToolImages = toolImages;
        mToolNames = toolNames;
        mCheckOutTimes = checkOutTimes;
        notifyDataSetChanged();
    }
}
