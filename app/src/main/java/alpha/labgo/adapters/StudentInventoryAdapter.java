package alpha.labgo.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import alpha.labgo.R;
import alpha.labgo.models.BorrowedItem;
import alpha.labgo.models.StudentInventory;
import de.hdodenhof.circleimageview.CircleImageView;

public class StudentInventoryAdapter
        extends ExpandableRecyclerAdapter<
                StudentInventory,
                BorrowedItem,
                StudentInventoryAdapter.StudentViewHolder,
                StudentInventoryAdapter.StudentBorrowedItemViewHolder>
        implements Filterable {

    private static final String TAG = "StudentInventoryAdapter";

    private static final int PARENT_NORMAL = 1;
    private static final int CHILD_NORMAL = 2;

    private Context mContext;
    private List<StudentInventory> mStudentInventories;
    private List<StudentInventory> mFilteredStudentInventories;
    LayoutInflater mInflater;

    /**
     * Default constructor.
     *
     * @param context Context
     * @param studentInventoryList The group
     */
    public StudentInventoryAdapter(Context context, List<StudentInventory> studentInventoryList) {
        super(studentInventoryList);
        mContext = context;
        mStudentInventories = studentInventoryList;
        mFilteredStudentInventories = studentInventoryList;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * Constructor only with context.
     *
     * @param context Context
     */
    public StudentInventoryAdapter(Context context) {
        super(new ArrayList<StudentInventory>());
        mContext = context;
        mStudentInventories = new ArrayList<StudentInventory>();
        mFilteredStudentInventories = mStudentInventories;
        mInflater = LayoutInflater.from(context);
    }

    public class StudentViewHolder extends ParentViewHolder {

        private static final String TAG = "StudentViewHolder";

        private static final float INITIAL_POSITION = 0.0f;
        private static final float ROTATED_POSITION = -90f;

        private TextView mName, mGtid;
        private ImageView mArrow;
        private CircleImageView mProfileImage;

        public StudentViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.text_student_name);
            mGtid = itemView.findViewById(R.id.text_student_gtid);
            mProfileImage = itemView.findViewById(R.id.image_item);
            mArrow = itemView.findViewById(R.id.image_expand_arrow);
        }

        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                if (expanded) {
                    mArrow.setRotation(ROTATED_POSITION);
                } else {
                    mArrow.setRotation(INITIAL_POSITION);
                }
            }
        }

        @Override
        public void onExpansionToggled(boolean expanded) {
            super.onExpansionToggled(expanded);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                RotateAnimation rotateAnimation;
                if (expanded) { // rotate clockwise
                    rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                            INITIAL_POSITION,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                } else { // rotate counterclockwise
                    rotateAnimation = new RotateAnimation(-1 * ROTATED_POSITION,
                            INITIAL_POSITION,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                }

                rotateAnimation.setDuration(300);
                rotateAnimation.setFillAfter(true);
                mArrow.startAnimation(rotateAnimation);
            }
        }

        public void setStudentView(StudentInventory studentInventory) {
            if (studentInventory != null) {
                mName.setText(studentInventory.getName());
                String gtidText = "GTID: " + studentInventory.getGtid();
                mGtid.setText(gtidText);
            } else {
                Log.e(TAG, "setStudentView: the input type needs to be StudentInventory");
            }
        }
    }

    public class StudentBorrowedItemViewHolder extends ChildViewHolder {

        private CircleImageView mToolImage;
        private TextView mToolName;
        private TextView mCheckOutTime;
        private TextView mDescription;
        private RelativeLayout mParentLayout;

        public StudentBorrowedItemViewHolder(View itemView) {
            super(itemView);
            mToolImage = itemView.findViewById(R.id.image_item);
            mToolName = itemView.findViewById(R.id.text_item_name);
            mCheckOutTime = itemView.findViewById(R.id.text_checkout_time);
            mParentLayout = itemView.findViewById(R.id.layout_borrowed_parent);
        }

        public void setBorrowedItemView(BorrowedItem borrowedItem) {
            Glide.with(mContext)
                    .asBitmap()
                    .load(borrowedItem.getItemImage())
                    .into(mToolImage);
            mToolName.setText(borrowedItem.getItemName());
            mCheckOutTime.setText(borrowedItem.getCheckOutTime());
        }
    }

    public void setList(ArrayList<StudentInventory> studentInventories) {
        mStudentInventories = studentInventories;
        mFilteredStudentInventories = studentInventories;
        setParentList(studentInventories, true);
        notifyParentDataSetChanged(false);
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateParentViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater
                .inflate(R.layout.layout_student_inventory, parent, false);
        return new StudentViewHolder(view);
    }

    @NonNull
    @Override
    public StudentBorrowedItemViewHolder onCreateChildViewHolder(@NonNull ViewGroup child, int viewType) {
        View view = mInflater
                .inflate(R.layout.layout_borrowed_item_ta, child, false);
        return new StudentBorrowedItemViewHolder(view);
    }

    @UiThread
    @Override
    public void onBindParentViewHolder(StudentViewHolder holder, int parentPosition, StudentInventory group) {
        holder.setStudentView(group);
    }

    @UiThread
    @Override
    public void onBindChildViewHolder(@NonNull StudentBorrowedItemViewHolder holder, int parentPosition, int childPosition, @NonNull BorrowedItem borrowedItem) {
        holder.setBorrowedItemView(borrowedItem);
    }


    @Override
    public int getParentViewType(int parentPosition) {
        return PARENT_NORMAL;
    }

    @Override
    public int getChildViewType(int parentPosition, int childPosition) {
        return CHILD_NORMAL;
    }

    @Override
    public boolean isParentViewType(int viewType) {
        return viewType == PARENT_NORMAL;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String constrainString = constraint.toString().toLowerCase();

                if (constrainString.isEmpty()) {
                    mFilteredStudentInventories = mStudentInventories;
                } else {
                    ArrayList<StudentInventory> filteredList = new ArrayList<>();

                    for (StudentInventory item : mStudentInventories) {
                        if (item.getName().toLowerCase().contains(constrainString) ||
                                item.getGtid().toLowerCase().contains(constrainString)) {
                            filteredList.add(item);
                        }
                    }
                    mFilteredStudentInventories = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredStudentInventories;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                mFilteredStudentInventories = (ArrayList<StudentInventory>) results.values;
                notifyParentDataSetChanged(false);
            }
        };
    }
}
